package com.aurea.testgenerator.args;

import java.util.Arrays;
import java.util.Optional;

public class ArgsParser {
    private String[] args;

    public ArgsParser(String[] args) {
        this.args = args;
    }

    public Optional<String> getKey(String key) {
        return Arrays.stream(args)
                .filter(arg -> arg.startsWith(key))
                .findFirst()
                .map(arg -> arg.substring(arg.indexOf(key) + key.length() + 1));
    }
}
