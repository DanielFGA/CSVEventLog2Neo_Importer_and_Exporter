package de.haw.eventlog2neo4j.core.etl.transformer.impl;

import de.haw.eventlog2neo4j.core.etl.transformer.AbstractEventLogTransformer;
import de.haw.eventlog2neo4j.core.model.Attribute;
import de.haw.eventlog2neo4j.core.model.Event;
import de.haw.eventlog2neo4j.core.model.Log;
import de.haw.eventlog2neo4j.core.model.Trace;

import java.util.*;
import java.util.stream.Collectors;

public class Csv2LogTransformer extends AbstractEventLogTransformer<Map<String, List<String>>> {


    public Csv2LogTransformer(String logFileName,
                              String dateTimePattern,
                              String caseIdColumn,
                              String activityColumn,
                              String timeStampColumn,
                              List<String> attributeNames) {
        super(logFileName, dateTimePattern, caseIdColumn, activityColumn, timeStampColumn, attributeNames);
    }

    public Log transform(Map<String, List<String>> eventLog) {
        Log log = new Log(logFileName,
                this.dateTimePattern,
                this.caseIdColumn,
                this.activityColumn,
                this.timeStampColumn,
                this.attributeNames);
        List<Attribute> attributes = new ArrayList<>();
        Map<String, Trace> events = new HashMap<>();
        int eventLogLength = eventLog.get(this.caseIdColumn).size();

        for (int logPosition = 0; logPosition < eventLogLength; logPosition++) {
            Attribute caseId = createCaseIdAttribute(eventLog, logPosition);
            Attribute activity = this.getAttribute(
                    this.activityColumn,
                    eventLog.get(this.activityColumn).get(logPosition),
                    attributes,
                    "activity");
            Attribute timeStampAttribute = createTimeStampAttribute(eventLog, logPosition);
            Event event = new Event(caseId, timeStampAttribute, activity);
            Event previousEvent = getLastEvent(caseId.getValue(), events);

            for (String attributeKey : getAttributeKeys(eventLog)) {
                Attribute attribute = this.getAttribute(attributeKey, eventLog.get(attributeKey).get(logPosition), attributes);
                event.addAttribute(attribute);
            }

            if (previousEvent != null) {
                previousEvent.setNextEvent(event);
            }

            if (!events.containsKey(caseId.getValue())) {
                Trace trace = new Trace(caseId.getValue());
                log.addTrace(trace);
                events.put(caseId.getValue(), trace);
                trace.addEvent(event);
            } else {
                events.get(caseId.getValue()).addEvent(event);
            }

        }
        return log;
    }

    private Attribute createCaseIdAttribute(Map<String, List<String>> eventLog, int logPosition) {
        Attribute caseIdAttribute = new Attribute(this.caseIdColumn, eventLog.get(this.caseIdColumn).get(logPosition));
        caseIdAttribute.addLabel("caseId");
        return caseIdAttribute;
    }

    private Attribute createTimeStampAttribute(Map<String, List<String>> eventLog,
                                               int logPosition) {
        Attribute timeStampAttribute = new Attribute(this.timeStampColumn, eventLog.get(this.timeStampColumn).get(logPosition));
        timeStampAttribute.addLabel("timestamp");
        return timeStampAttribute;

    }

    private Attribute getAttribute(String attributeName, String attributeValue, List<Attribute> attributes, String... labels) {
        Attribute attribute = new Attribute(attributeName, attributeValue);
        Arrays.stream(labels).forEach(attribute::addLabel);
        if (attributes.contains(attribute)) {
            attribute = attributes.get(attributes.indexOf(attribute));
        } else {
            attributes.add(attribute);
        }
        return attribute;
    }

    private List<String> getAttributeKeys(Map<String, List<String>> eventLog) {
        return eventLog.keySet()
                .stream()
                .filter(e -> !e.equals(this.caseIdColumn))
                .filter(e -> !e.equals(this.activityColumn))
                .filter(e -> !e.equals(this.timeStampColumn))
                .collect(Collectors.toList());
    }

    private Event getLastEvent(String caseId, Map<String, Trace> traces) {
        Trace trace = traces.get(caseId);
        return trace == null ? null : trace.getLastEvent();
    }
}
