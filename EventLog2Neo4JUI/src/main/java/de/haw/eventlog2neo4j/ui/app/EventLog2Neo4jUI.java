package de.haw.eventlog2neo4j.ui.app;

import de.haw.eventlog2neo4j.ui.controller.Controller;

public class EventLog2Neo4jUI {

    public static void main(String[] args) {
        Controller controller = new Controller();
        controller.start();
    }

}