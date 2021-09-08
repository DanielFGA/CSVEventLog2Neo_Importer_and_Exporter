package de.haw.eventlog2neo4j.core.etl.extractor.impl;

import com.opencsv.CSVReader;
import de.haw.eventlog2neo4j.core.etl.extractor.Extractor;

import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class CSVExtractor implements Extractor<String, Map<String, List<String>>> {

    private final Character separator;
    private final String [] headers;

    public CSVExtractor(Character separator, Map<String, String> requiredAttributes, List<String> attributeNames) {
        this.separator = separator;
        this.headers = Stream.concat(requiredAttributes.values()
                .stream(), attributeNames.stream())
                .toArray(String[]::new);
    }

    public Map<String, List<String>> extract(String pathToCSV) {
        Map<String, List<String>> csvAsMap = new HashMap<>();

        try (CSVReader reader = new CSVReader(new FileReader(pathToCSV), separator)) {
            String[] lineInArray;
            boolean isHeader = true;
            while ((lineInArray = reader.readNext()) != null) {
                if (isHeader) {
                    isHeader = false;
                } else {
                    for (int i = 0; i < lineInArray.length; i++) {
                        csvAsMap.get(headers[i]).add(lineInArray[i]);
                    }
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return csvAsMap;
    }
}
