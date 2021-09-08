package de.haw.eventlog2neo4j.core.processor;

import de.haw.eventlog2neo4j.core.etl.pipeline.impl.FlowImportPipeline;
import de.haw.eventlog2neo4j.core.model.Event;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import static de.haw.eventlog2neo4j.core.util.FileUtils.readFile;

public class EventLogFlowProcessor {

    private final FlowImportPipeline flowImportPipeline;

    public EventLogFlowProcessor(String configPath) {
        Yaml yaml = new Yaml();
        String configurationContent = null;
        try {
            configurationContent = readFile(configPath, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Map<String, ?> configurationMap = yaml.load(configurationContent);
        this.flowImportPipeline = new FlowImportPipeline(
                (Map<String, Object>) configurationMap.get("event_log"),
                (Map<String, Object>) configurationMap.get("database"));
    }

    public EventLogFlowProcessor(Map<String, Object> configurationMap) {
        this.flowImportPipeline = new FlowImportPipeline(
                (Map<String, Object>) configurationMap.get("event_log"),
                (Map<String, Object>) configurationMap.get("database"));
    }

    public void process(List<Event> event) {
        event.forEach(this::process);
    }

    public void process(Event event) {
        flowImportPipeline.run(event);
    }


}
