package de.haw.eventlog2neo4j.core.etl.transformer;

import de.haw.eventlog2neo4j.core.model.Log;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public abstract class AbstractEventLogTransformer<I>  implements Transformer<I, Log>{

    protected final String logFileName;
    protected final String dateTimePattern;
    protected final String caseIdColumn;
    protected final String activityColumn;
    protected final String timeStampColumn;
    protected final List<String> attributeNames;


}
