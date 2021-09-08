package de.haw.eventlog2neo4j.camunda.demo.job;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

public class PrintTicketAndBillDelegate implements JavaDelegate {

    @Override
    public void execute(DelegateExecution delegateExecution) {
        delegateExecution.setVariable("printed", "yes");
    }

}
