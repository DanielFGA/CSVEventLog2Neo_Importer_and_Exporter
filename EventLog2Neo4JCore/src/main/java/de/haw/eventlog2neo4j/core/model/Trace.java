package de.haw.eventlog2neo4j.core.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Getter
public class Trace {

    private final String traceId;
    private final List<Event> events = new ArrayList<>();

    public int getSize() {
        return events.size();
    }

    public boolean isEmpty() {
        return this.events.isEmpty();
    }

    public Event getFirstEvent() {
        return this.isEmpty() ?
                null :
                events.get(0);
    }

    public Event getLastEvent() {
        return this.isEmpty() ?
                null :
                events.get(this.getSize() - 1);
    }

    public void addEvents(List<Event> events) {
        this.events.addAll(events);
    }

    public void addEvent(Event event) {
        events.add(event);
    }

    public String toNeo4jNode() {
        return String.format("(t:trace {traceId: '%s'})", traceId);
    }

}
