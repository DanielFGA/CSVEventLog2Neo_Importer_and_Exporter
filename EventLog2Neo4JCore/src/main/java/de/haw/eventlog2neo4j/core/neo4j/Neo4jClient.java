package de.haw.eventlog2neo4j.core.neo4j;

import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;

import java.util.Map;

import static java.lang.String.format;

public class Neo4jClient {

    private final String host;
    private final int port;
    private final String user;
    private final String password;

    public Neo4jClient(Map<String, Object> neo4jConfig) {
        this.host = (String) neo4jConfig.get("host");
        this.port = (Integer) neo4jConfig.get("port");
        this.user = (String) neo4jConfig.get("user");
        this.password = (String) neo4jConfig.get("password");
    }

    public Driver getDriver() {
        return GraphDatabase.driver(format("bolt://%s:%d", host, port), AuthTokens.basic(user, password));
    }

}
