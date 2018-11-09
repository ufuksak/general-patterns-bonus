package com.aurea.testgenerator.yaml

import spock.lang.Specification

import java.util.stream.Stream

class YAMLParserTest extends Specification {
    def GetParsed() {
        given:
        def parser = new YAMLParser()
        when:
        Stream<String[]> parsed = parser.getParsed(
                """
                        - 1: a
                          2: b
                        - 3: c
                          4: d
                        """)
        then:
        parsed.toArray() == [["--1=a", "--2=b"], ["--3=c", "--4=d"]].toArray()
    }
}
