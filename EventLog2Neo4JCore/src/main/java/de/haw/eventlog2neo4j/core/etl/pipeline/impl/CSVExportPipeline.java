package de.haw.eventlog2neo4j.core.etl.pipeline.impl;

import de.haw.eventlog2neo4j.core.etl.extractor.impl.Neo4jExtractor;
import de.haw.eventlog2neo4j.core.etl.loader.impl.CSVLoader;
import de.haw.eventlog2neo4j.core.etl.pipeline.AbstractPipeline;
import de.haw.eventlog2neo4j.core.etl.transformer.impl.Log2CsvTransformer;
import de.haw.eventlog2neo4j.core.model.Log;

import java.util.List;
import java.util.Map;

public class CSVExportPipeline extends AbstractPipeline<String, Log, Map<String, List<String>>> {

    public CSVExportPipeline(Map<String, Object> pipelineConfig,
                             Map<String, Object> neo4jConfig) {
        super(new Neo4jExtractor(neo4jConfig),
                new Log2CsvTransformer(),
                new CSVLoader((String) pipelineConfig.get("path"), ((String) pipelineConfig.get("separator")).charAt(0)));
    }

}
