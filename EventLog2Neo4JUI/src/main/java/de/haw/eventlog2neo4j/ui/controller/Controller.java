package de.haw.eventlog2neo4j.ui.controller;

import de.haw.eventlog2neo4j.core.etl.pipeline.impl.CSVExportPipeline;
import de.haw.eventlog2neo4j.core.etl.pipeline.impl.CSVImportPipeline;
import de.haw.eventlog2neo4j.ui.view.View;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import static de.haw.eventlog2neo4j.core.util.FileUtils.getFileName;
import static de.haw.eventlog2neo4j.core.util.FileUtils.readFile;

public class Controller {

    public static final String CONFIG_ERROR_MESSAGE =
            "Could not upload configuration file. " +
            "Please check your configuration file.";

    private final View view;

    public Controller() {
        this.view = new View(this);
    }

    public void start() {
        this.view.setVisible(true);
    }

    public void loadCSVEventLogToNeo4j(String configPath) {
        Yaml yaml = new Yaml();
        try {
            String configurationContent = readFile(configPath, StandardCharsets.UTF_8);
            Map<String, Map<String, Object>> configurationMap = yaml.load(configurationContent);
            CSVImportPipeline csvImportPipeline = new CSVImportPipeline(
                    configurationMap.get("event_log"),
                    configurationMap.get("database"));
            csvImportPipeline.run((String) configurationMap.get("event_log").get("path"));
        } catch (IOException e) {
            this.view.setOutput(CONFIG_ERROR_MESSAGE);
        }
    }

    public void extractCSVEventLogFromNeo4j(String configPath) {
        Yaml yaml = new Yaml();
        try {
            String configurationContent = readFile(configPath, StandardCharsets.UTF_8);
            Map<String, Map<String, Object>> configurationMap = yaml.load(configurationContent);
            CSVExportPipeline csvExportPipeline = new CSVExportPipeline(
                    configurationMap.get("event_log"),
                    configurationMap.get("database"));
            csvExportPipeline.run(getFileName((String) configurationMap.get("event_log").get("path")));
        } catch (Exception e) {
            this.view.setOutput(CONFIG_ERROR_MESSAGE);
            e.printStackTrace();
        }
    }


}
