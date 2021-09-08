package de.haw.eventlog2neo4j.core.etl.loader.impl;

import de.haw.eventlog2neo4j.core.etl.loader.Loader;
import de.haw.eventlog2neo4j.core.model.Attribute;
import de.haw.eventlog2neo4j.core.model.Event;
import de.haw.eventlog2neo4j.core.model.Log;
import de.haw.eventlog2neo4j.core.model.Trace;
import de.haw.eventlog2neo4j.core.neo4j.Neo4jClient;
import de.haw.eventlog2neo4j.core.util.Tuple;
import lombok.extern.slf4j.Slf4j;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Session;
import org.neo4j.driver.Transaction;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.lang.String.format;

@Slf4j
public class Neo4jLoader implements Loader<Log> {

    private final Neo4jClient neo4jClient;

    public Neo4jLoader(Map<String, Object> neo4jConfig) {
        this.neo4jClient = new Neo4jClient(neo4jConfig);
    }

    public void load(Log eventLog) {

        List<String> queries = getLogToNeo4jQueries(eventLog);

        log.info(format("Assembled %d queries. Start executing.", queries.size()));
        Driver driver = this.neo4jClient.getDriver();
        Session session = driver.session();
        Transaction transaction = session.beginTransaction();

        for (int i = 0; i < queries.size(); i++) {
            String query = queries.get(i);
            transaction.run(query);
            log.info(format("Executed %s of %s queries.", i + 1, queries.size()));
        }

        transaction.commit();
        transaction.close();
        session.close();
        driver.close();
    }

    private List<String> getLogToNeo4jQueries(Log log) {
        List<String> queries = new ArrayList<>();
        queries.add(format("MERGE %s", log.toNeo4jNode()));
        queries.addAll(log.getTraces()
                .stream()
                .map(e -> getTraceToNeo4jQueries(e, log))
                .flatMap(Collection::stream)
                .collect(Collectors.toList()));
        return queries;
    }

    private List<String> getTraceToNeo4jQueries(Trace trace, Log log) {
        List<String> queries = new ArrayList<>();
        queries.add(format(
                "MATCH %s " +
                        "MERGE %s " +
                        "MERGE (l)-[:has]->(t)",
                log.toNeo4jNode(), trace.toNeo4jNode()));
        queries.addAll(getEventToNeo4jQueries(trace.getEvents().get(0), trace));
        return queries;
    }

    private List<String> getEventToNeo4jQueries(Event event, Trace trace) {
        List<String> eventQueries = new ArrayList<>();
        if (event.hasNext()) {
            eventQueries.addAll(getEventToNeo4jQueries(event.getNextEvent(), trace));
        }
        List<Attribute> attributes = getEventAttributes(event);

        Map<String, String> matchSubQueries =
                IntStream.range(0, attributes.size())
                        .mapToObj(i -> new Tuple<>("a" + i, i))
                        .collect(Collectors.toMap(
                                Tuple::getLeft,
                                v -> attributes.get(v.getRight()).toNeo4jNode(v.getLeft())));

        List<String> relationSubQueries = matchSubQueries.keySet()
                .stream()
                .filter(e -> !e.equals("t"))
                .map(attributeId -> format("(e)-[:has]->(%s)", attributeId))
                .collect(Collectors.toList());

        relationSubQueries.add("(t)-[:has]->(e)");

        eventQueries.add(format("MATCH %s CREATE %s MERGE %s CREATE %s",
                trace.toNeo4jNode(),
                event.toNeo4jNode(),
                String.join(" MERGE ", matchSubQueries.values()),
                String.join(",", relationSubQueries)));

        return eventQueries;
    }

    private List<Attribute> getEventAttributes(Event event) {
        List<Attribute> attributes = new ArrayList<>(event.getAttributes());
        attributes.add(event.getCaseId());
        attributes.add(event.getTimeStamp());
        attributes.add(event.getActivity());
        return attributes;
    }

}
