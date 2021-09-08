package de.haw.eventlog2neo4j.core.etl.transformer.impl;

import de.haw.eventlog2neo4j.core.etl.transformer.Transformer;
import de.haw.eventlog2neo4j.core.model.Attribute;
import de.haw.eventlog2neo4j.core.model.Event;
import de.haw.eventlog2neo4j.core.model.Log;
import de.haw.eventlog2neo4j.core.model.Trace;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@NoArgsConstructor
public class Log2CsvTransformer implements Transformer<Log, Map<String, List<String>>> {

    public Map<String, List<String>> transform(Log log) {
        Map<String, List<String>> csv = new LinkedHashMap<>();
        for (Trace trace : log.getTraces()) {
            for (Event event : trace.getEvents()) {
                Map<String, String> attributes = mapAttributes(event);
                for (String attributeName : log.getAllAttributeNames()) {
                    csv.computeIfAbsent(attributeName, v -> new ArrayList<>());
                    csv.get(attributeName).add(attributes.getOrDefault(attributeName, ""));
                }
            }
        }
        return csv;
    }

    private Map<String, String> mapAttributes(Event event) {
        return event.getAllAttributes().stream().collect(Collectors.toMap(Attribute::getName, Attribute::getValue));
    }
}
