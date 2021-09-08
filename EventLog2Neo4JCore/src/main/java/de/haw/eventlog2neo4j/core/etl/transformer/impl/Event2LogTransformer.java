package de.haw.eventlog2neo4j.core.etl.transformer.impl;

import de.haw.eventlog2neo4j.core.etl.transformer.AbstractEventLogTransformer;
import de.haw.eventlog2neo4j.core.model.Event;
import de.haw.eventlog2neo4j.core.model.Log;
import de.haw.eventlog2neo4j.core.model.Trace;

import java.util.List;

public class Event2LogTransformer extends AbstractEventLogTransformer<Event> {

    public Event2LogTransformer(String logFileName,
                                String dateTimePattern,
                                String caseIdColumn,
                                String activityColumn,
                                String timeStampColumn,
                                List<String> attributeNames) {
        super(logFileName, dateTimePattern, caseIdColumn, activityColumn, timeStampColumn, attributeNames);
    }

    public Log transform(Event event) {
        Log log = new Log(
                this.logFileName,
                this.dateTimePattern,
                this.caseIdColumn,
                this.activityColumn,
                this.timeStampColumn,
                this.attributeNames);
        Trace trace = new Trace(event.getCaseId().getValue());
        trace.addEvent(event);
        log.addTrace(trace);
        return log;
    }
}
