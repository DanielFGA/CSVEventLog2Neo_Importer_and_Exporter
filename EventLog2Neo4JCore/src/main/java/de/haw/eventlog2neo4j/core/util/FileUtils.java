package de.haw.eventlog2neo4j.core.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FileUtils {

    public static String readFile(String path, Charset encoding) throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, encoding);
    }

    public static Map<String, Object> readYAML(String path, Charset encoding) {
        Yaml yaml = new Yaml();
        String configurationContent = null;
        try {
            configurationContent = readFile(path, encoding);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return yaml.load(configurationContent);
    }

    public static String getFileName(String path) {
        return new File(path).getName();
    }
}
