package de.haw.eventlog2neo4j.core.model;

import de.haw.eventlog2neo4j.core.util.StringUtils;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Getter
public class Log {

    private final String logName;
    private final String dateTimePattern;
    private final String caseIdColumn;
    private final String activityColumn;
    private final String timeStampColumn;
    private final List<String> attributesColumns;
    private final List<Trace> traces = new ArrayList<>();

    public void addTrace(Trace trace) {
        this.traces.add(trace);
    }

    public String toNeo4jNode() {
        return String.format("(l:log {logName: '%s', dateTimePattern: '%s', caseIdColumn: '%s', " +
                        "activityColumn: '%s', timeStampColumn: '%s', attributes: [%s]})",
                logName,
                StringUtils.escapeSingleQuotes(dateTimePattern),
                caseIdColumn,
                activityColumn,
                getTimeStampColumn(),
                attributesColumns.stream().map(e -> String.format("'%s'", e)).collect(Collectors.joining(",")));
    }

    public List<String> getAllAttributeNames() {
        List<String> attributeNames = new ArrayList<>();
        attributeNames.add(caseIdColumn);
        attributeNames.add(timeStampColumn);
        attributeNames.add(activityColumn);
        attributeNames.addAll(attributesColumns);
        return attributeNames;
    }

    public int getLength() {
        return traces.stream()
                .map(Trace::getSize)
                .reduce(0, Integer::sum);
    }

}
