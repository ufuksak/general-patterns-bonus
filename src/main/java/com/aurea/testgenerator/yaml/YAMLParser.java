package com.aurea.testgenerator.yaml;

import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class YAMLParser {
    private Yaml yaml = new Yaml();

    public Stream<String[]> parse(String path) {
        Object load;
        try {
            load = yaml.load(new String(Files.readAllBytes(Paths.get(path))));
        } catch (IOException e) {
            e.printStackTrace();
            return Stream.empty();
        }
        return ((List<Map<?, ?>>) load).stream()
                .map(rec -> rec.entrySet().stream()
                        .map(e -> "--" + e)
                        .toArray()
                )
                .map(objArr -> Arrays.copyOf(objArr, objArr.length, String[].class));

    }
}
