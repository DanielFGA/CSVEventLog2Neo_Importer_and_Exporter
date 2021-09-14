package de.haw.eventlog2neo4j.core.etl.extractor.impl;

import de.haw.eventlog2neo4j.core.etl.extractor.Extractor;
import de.haw.eventlog2neo4j.core.model.Attribute;
import de.haw.eventlog2neo4j.core.model.Event;
import de.haw.eventlog2neo4j.core.model.Log;
import de.haw.eventlog2neo4j.core.model.Trace;
import de.haw.eventlog2neo4j.core.neo4j.Neo4jClient;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;
import org.neo4j.driver.Value;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static de.haw.eventlog2neo4j.core.util.DateUtils.toLocalDateTime;

public class Neo4jExtractor implements Extractor<String, Log> {

    private final Neo4jClient neo4jClient;
    private final List<Attribute> attributes = new ArrayList<>();

    public Neo4jExtractor(Map<String, Object> neo4jConfig) {
        this.neo4jClient = new Neo4jClient(neo4jConfig);
    }

    public Log extract(String logName) {
        attributes.clear();
        Log log = getLog(logName);
        sortTraces(extractTraces(log), log.getDateTimePattern()).forEach(log::addTrace);
        return log;
    }

    private Log getLog(String logName) {
        Session session = this.neo4jClient.getDriver().session();
        Result result = session.run(String.format("MATCH (l:log {logName: '%s'}) RETURN l", logName));
        Log log = null;
        if (result.hasNext()) {
            Value logValue = result.next().get(0);
            log = new Log(
                    logValue.get("logName", ""),
                    logValue.get("dateTimePattern", ""),
                    logValue.get("caseIdColumn", ""),
                    logValue.get("activityColumn", ""),
                    logValue.get("timeStampColumn", ""),
                    logValue.get("attributes", Collections.emptyList(), Value::asString));
        }
        return log;
    }

    private List<Trace> extractTraces(Log log) {
        List<Trace> traces = getTraces(log);
        traces.forEach(trace -> trace.addEvents(extractEvents(log, trace)));
        return traces;
    }

    private List<Trace> getTraces(Log log) {
        Session session = this.neo4jClient.getDriver().session();
        Result result = session.run(String.format("MATCH (t:trace)--%s RETURN t", log.toNeo4jNode()));
        List<Trace> traces = new ArrayList<>();
        while (result.hasNext()) {
            traces.addAll(result.next().values().stream()
                    .map(e -> new Trace(e.get("traceId", "")))
                    .collect(Collectors.toList()));
        }
        return traces;
    }

    private List<Event> extractEvents(Log log, Trace trace) {
        List<Event> events;
        Map<Integer, List<Attribute>> eventAttributes = new HashMap<>();
        Session session = this.neo4jClient.getDriver().session();
        Result result = session.run(String.format("MATCH (e:event)--%s MATCH (e)--(a:attribute) RETURN ID(e),a,labels(a)",
                trace.toNeo4jNode()));
        while (result.hasNext()) {
            Record currentRecord = result.next();
            int eventId = currentRecord.get(0).asInt();
            eventAttributes.computeIfAbsent(eventId, k -> new ArrayList<>());
            eventAttributes.get(eventId).add(extractAttribute(currentRecord.get(1), currentRecord.get(2)));
        }
        events = sortEvents(createEvents(log, eventAttributes), log.getDateTimePattern());
        connectEvents(events);
        return events;
    }

    private Attribute extractAttribute(Value attributeValue, Value labels) {
        Attribute attribute = new Attribute(
                attributeValue.get("name").asString(),
                attributeValue.get("value").asString());
        labels.asList(Value::asString).forEach(attribute::addLabel);
        if (!this.attributes.contains(attribute)) {
            this.attributes.add(attribute);
            return attribute;
        }
        return this.attributes.stream().filter(e -> e.equals(attribute)).findFirst().orElse(attribute);
    }

    private Event getEvent(Log log, List<Attribute> attributes) {
        Attribute caseId = getAttributeByName(attributes, log.getCaseIdColumn());
        Attribute timeStamp = getAttributeByName(attributes, log.getTimeStampColumn());
        Attribute activity = getAttributeByName(attributes, log.getActivityColumn());
        Event event = new Event(caseId, timeStamp, activity);
        getNonRequiredAttributes(attributes, log).forEach(event::addAttribute);
        return event;
    }

    private Attribute getAttributeByName(List<Attribute> attributes, String attributeName) {
        return attributes.stream().filter(e -> e.getName().equals(attributeName)).findFirst().orElse(null);
    }

    private List<Attribute> getNonRequiredAttributes(List<Attribute> attributes, Log log) {
        return attributes.stream()
                .filter(e ->
                        !e.getName().equals(log.getCaseIdColumn()) &&
                                !e.getName().equals(log.getTimeStampColumn()) &&
                                !e.getName().equals(log.getActivityColumn()))
                .collect(Collectors.toList());
    }

    private List<Event> createEvents(Log log, Map<Integer, List<Attribute>> eventAttributes) {
        return eventAttributes.values()
                .stream()
                .map(e -> getEvent(log, e))
                .collect(Collectors.toList());
    }

    private List<Event> sortEvents(List<Event> events, String timestampPattern) {
        return events.stream()
                .sorted((event1, event2) -> {
                    LocalDateTime eventTime1 = toLocalDateTime(event1.getTimeStamp().getValue(), timestampPattern);
                    LocalDateTime eventTime2 = toLocalDateTime(event2.getTimeStamp().getValue(), timestampPattern);
                    return eventTime1.compareTo(eventTime2);
                })
                .collect(Collectors.toList());
    }

    private void connectEvents(List<Event> events) {
        for (int i = 0; i < events.size()-1; i++) {
            events.get(i).setNextEvent(events.get(i+1));
        }
    }

    private List<Trace> sortTraces(List<Trace> traces, String timestampPattern) {
        return traces.stream()
                .sorted((trace1, trace2) -> {
                    LocalDateTime eventTime1 = toLocalDateTime(trace1.getFirstEvent().getTimeStamp().getValue(), timestampPattern);
                    LocalDateTime eventTime2 = toLocalDateTime(trace2.getFirstEvent().getTimeStamp().getValue(), timestampPattern);
                    return eventTime1.compareTo(eventTime2);
                })
                .collect(Collectors.toList());
    }


}
