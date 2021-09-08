package de.haw.eventlog2neo4j.camunda.demo.handler;

import de.haw.eventlog2neo4j.core.model.Attribute;
import de.haw.eventlog2neo4j.core.model.Event;
import de.haw.eventlog2neo4j.core.processor.EventLogFlowProcessor;
import org.camunda.bpm.engine.ProcessEngines;
import org.camunda.bpm.engine.impl.history.event.HistoricActivityInstanceEventEntity;
import org.camunda.bpm.engine.impl.history.event.HistoryEvent;
import org.camunda.bpm.engine.impl.history.event.HistoryEventTypes;
import org.camunda.bpm.engine.impl.history.handler.HistoryEventHandler;
import org.springframework.stereotype.Component;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import static de.haw.eventlog2neo4j.core.util.FileUtils.readYAML;

@Component
public class DemoHistoryEventHandler implements HistoryEventHandler {

    private final EventLogFlowProcessor eventLogFlowProcessor;
    private final Map<String, String> requiredAttributes;
    private final List<String> attributes;

    protected DemoHistoryEventHandler() throws URISyntaxException {
        Map<String, Object> configuration = readYAML(getConfigFilePath(), StandardCharsets.UTF_8);
        Map<String, Object> eventLogConfig = (Map<String, Object>) configuration.get("event_log");
        this.requiredAttributes = (Map<String, String>) eventLogConfig.get("required_attributes");
        this.attributes = (List<String>) eventLogConfig.get("attributes");
        this.eventLogFlowProcessor = new EventLogFlowProcessor(configuration);
    }

    protected String getConfigFilePath() throws URISyntaxException{
        URL resource = getClass().getClassLoader().getResource("DemoConfiguration.yml");
        if (resource == null) {
            throw new IllegalArgumentException("file not found!");
        } else {
            return new File(resource.toURI()).getPath();
        }
    }

    @Override
    public void handleEvents(List<HistoryEvent> list) {
        list.forEach(this::handleEvent);
    }

    @Override
    public void handleEvent(HistoryEvent historyEvent) {
        if (isHistoricActivityInstanceEventEntity(historyEvent)) {
            HistoricActivityInstanceEventEntity activityHistoryEvent = (HistoricActivityInstanceEventEntity) historyEvent;
            if (isServiceTask(activityHistoryEvent) && isActivityEnd(activityHistoryEvent)) {
                addLogEntry(activityHistoryEvent);
            }
        }
    }

    private boolean isHistoricActivityInstanceEventEntity(HistoryEvent historyEvent) {
        return historyEvent instanceof HistoricActivityInstanceEventEntity;
    }

    private boolean isServiceTask(HistoricActivityInstanceEventEntity historyEvent) {
        return historyEvent.getActivityType().equals("serviceTask");
    }

    private boolean isActivityEnd(HistoryEvent historyEvent) {
        return historyEvent.isEventOfType(HistoryEventTypes.ACTIVITY_INSTANCE_END);
    }

    private void addLogEntry(HistoricActivityInstanceEventEntity activityHistoryEvent) {
        Map<String, Object> variables = getVariables(activityHistoryEvent.getExecutionId());

        Attribute caseId = new Attribute(
                this.requiredAttributes.get("case_id"),
                String.valueOf(variables.get("processId")));
        caseId.addLabel("caseId");

        Attribute activity = new Attribute(
                this.requiredAttributes.get("activity"),
                activityHistoryEvent.getActivityName());
        activity.addLabel("activity");

        Attribute timeStamp = new Attribute(
                this.requiredAttributes.get("time_stamp"),
                activityHistoryEvent.getEndTime().toString());
        timeStamp.addLabel("timestamp");

        Event event = new Event(caseId, timeStamp, activity);

        for (String attribute : this.attributes) {
            if (variables.containsKey(attribute)) {
                event.addAttribute(new Attribute(attribute, variables.get(attribute).toString()));
            }
        }

        this.eventLogFlowProcessor.process(event);
    }

    private static Map<String, Object> getVariables(String executionId) {
        return ProcessEngines.getDefaultProcessEngine().getRuntimeService().getVariables(executionId);
    }

}
