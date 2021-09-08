package de.haw.eventlog2neo4j.camunda.demo.job;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

public class ReceiveOrderDelegate implements JavaDelegate {

    private static int processId = 0;

    @Override
    public void execute(DelegateExecution delegateExecution) {
        delegateExecution.setVariable("processId", processId++);
    }
}
