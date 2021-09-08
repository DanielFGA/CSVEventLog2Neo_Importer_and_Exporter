package de.haw.eventlog2neo4j.camunda.demo;

import org.camunda.bpm.engine.ProcessEngines;
import org.camunda.bpm.engine.RuntimeService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

@SpringBootApplication
public class DemoApplication {

    private static final int PROCESS_AMOUNT = 3;

    public static void main(String... args) {
        SpringApplication.run(DemoApplication.class, args);
    }

    @EventListener(ApplicationStartedEvent.class)
    public void startProcesses() {
        RuntimeService runtimeService = ProcessEngines.getDefaultProcessEngine().getRuntimeService();
        List<String> movies = Arrays.asList(
                "Iron Man", "Titanic", "Avatar",
                "Herr der Ringe", "Harry Potter", "Star Wars",
                "Dragonball", "50 Shades of gray", "The Ring");
        Random random = new Random();

        for (int i = 0; i < PROCESS_AMOUNT; i++) {
            String movie = movies.get(random.nextInt(movies.size()));
            int amount = random.nextInt(10) + 1;

            runtimeService.createProcessInstanceByKey("demo_process")
                    .setVariable("movie", movie)
                    .setVariable("amount", amount)
                    .execute();
        }
    }

}
