package de.haw.eventlog2neo4j.core.etl.loader.impl;

import de.haw.eventlog2neo4j.core.etl.loader.Loader;
import lombok.RequiredArgsConstructor;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class CSVLoader implements Loader<Map<String, List<String>>> {

    private final String filePath;
    private final Character separator;

    public void load(Map<String, List<String>> csv) {
        String header = String.join(separator.toString(), csv.keySet());
        List<String> rows = new ArrayList<>();
        int logLength = getLogLength(csv);

        for (int logPosition = 0; logPosition < logLength; logPosition++) {
            List<String> row = new ArrayList<>();
            for (String column : csv.keySet()) {
                row.add(csv.get(column).get(logPosition));
            }
            rows.add(String.join(separator.toString(), row));
        }
        File csvOutputFile = new File(filePath);
        try (PrintWriter pw = new PrintWriter(csvOutputFile)) {
            pw.println(String.join(separator.toString(), header));
            rows.forEach(pw::println);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private int getLogLength(Map<String, List<String>> csv) {
        return csv.get(csv.keySet().toArray()[0]).size();
    }


}
