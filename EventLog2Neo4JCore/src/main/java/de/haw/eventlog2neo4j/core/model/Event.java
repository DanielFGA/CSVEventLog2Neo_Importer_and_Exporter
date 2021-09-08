package de.haw.eventlog2neo4j.core.model;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class Event {

    private Event nextEvent = null;
    private final Attribute caseId;
    private final Attribute timeStamp;
    private final Attribute activity;
    private final List<Attribute> attributes = new ArrayList<>();

    public Event(Attribute caseId, Attribute timeStamp, Attribute acitivity) {
        this.caseId = caseId;
        this.timeStamp = timeStamp;
        this.activity = acitivity;
    }

    public void addAttribute(Attribute attribute) {
        if (!this.attributes.contains(attribute)) {
            this.attributes.add(attribute);
        }
    }

    public List<Attribute> getAllAttributes() {
        List<Attribute> allAttributes = new ArrayList<>(this.attributes);
        allAttributes.add(this.caseId);
        allAttributes.add(this.timeStamp);
        allAttributes.add(this.activity);
        return allAttributes;
    }

    public boolean hasNext() {
        return this.nextEvent != null;
    }

    public String toNeo4jNode() {
        return "(e:event)";
    }

}
