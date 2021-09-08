package de.haw.eventlog2neo4j.core.etl.pipeline.impl;

import de.haw.eventlog2neo4j.core.etl.extractor.impl.IdleExtractor;
import de.haw.eventlog2neo4j.core.etl.loader.impl.Neo4jLoader;
import de.haw.eventlog2neo4j.core.etl.pipeline.AbstractPipeline;
import de.haw.eventlog2neo4j.core.etl.transformer.impl.Event2LogTransformer;
import de.haw.eventlog2neo4j.core.model.Event;
import de.haw.eventlog2neo4j.core.model.Log;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class FlowImportPipeline extends AbstractPipeline<Event, Event, Log> {


    public FlowImportPipeline(Map<String, Object> pipelineConfig,
                             Map<String, Object> neo4jConfig) {
        super(new IdleExtractor<>(),
                new Event2LogTransformer(
                        new File((String) pipelineConfig.get("path")).getName(),
                        (String) pipelineConfig.get("date_time_pattern"),
                        ((Map<String, String>) pipelineConfig.get("required_attributes")).get("case_id"),
                        ((Map<String, String>) pipelineConfig.get("required_attributes")).get("activity"),
                        ((Map<String, String>) pipelineConfig.get("required_attributes")).get("time_stamp"),
                        (List<String>) pipelineConfig.getOrDefault("attributes", Collections.emptyList())),
                new Neo4jLoader(neo4jConfig));
    }

}
