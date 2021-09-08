package de.haw.eventlog2neo4j.core.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.lang.String.format;

@EqualsAndHashCode
@AllArgsConstructor
@Getter
public class Attribute {

    private final String name;
    private final String value;
    private final List<String> labels = new ArrayList<>(Arrays.asList("attribute"));

    public void addLabel(String label) {
        labels.add(label);
    }

    public String toNeo4jNode(String prefix) {
        return format("(%s:%s {name: '%s', value: '%s'})",
                prefix,
                String.join(":", labels),
                name,
                value);
    }



}
