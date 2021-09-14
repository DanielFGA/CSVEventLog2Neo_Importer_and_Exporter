<h1>EventLog2Neo4j</h1>

The EventLog2Neo4j is a framework to import Event Logs into the graph database Neo4j. The features are:

<ul>
<li>Import an event log (csv file) into Neo4j</li>
<li>Export an event log (csv file) from Neo4j</li>
<li>Include the framework into your application to create Log objects and import these into Neo4j</li>
</ul>

<h2>How to use</h2>

The tool is configuration based, so first you need to create a configuration like this [example configuration](https://github.com/DanielFGA/CSVEventLog2Neo_Importer_and_Exporter/blob/master/example_configurations/CamundaDemoConfiguration.yml). Every attribute you don´t list in the configuration will be ignored.

If you have a valid configuration, then you can use the [UI](https://github.com/DanielFGA/CSVEventLog2Neo_Importer_and_Exporter/releases/tag/release) or include the framework into your application to import an event log via code. Create [event objects](https://github.com/DanielFGA/CSVEventLog2Neo_Importer_and_Exporter/blob/master/EventLog2Neo4JCore/src/main/java/de/haw/eventlog2neo4j/core/model/Event.java) and pass these to the [EventLogFlowProcessor](https://github.com/DanielFGA/CSVEventLog2Neo_Importer_and_Exporter/blob/master/EventLog2Neo4JCore/src/main/java/de/haw/eventlog2neo4j/core/processor/EventLogFlowProcessor.java). The EventLogFlowProcessor needs for initialisation the path to the configuration file. Look [here](https://github.com/DanielFGA/CSVEventLog2Neo_Importer_and_Exporter/blob/master/EventLog2Neo4JCamundaDemo/src/main/java/de/haw/eventlog2neo4j/camunda/demo/handler/DemoHistoryEventHandler.java) for example usage.