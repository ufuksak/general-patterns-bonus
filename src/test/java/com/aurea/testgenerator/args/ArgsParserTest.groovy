package com.aurea.testgenerator.args

import spock.lang.Specification

class ArgsParserTest extends Specification {
    def "GetKey"() {
        given:
        def testKey = "--key"
        def testVal = "value"
        def parser = new ArgsParser(["$testKey=$testVal"] as String[])
        when:
        def key = parser.getKey(testKey)
        then:
        key.present
        key.get() == testVal
    }
}
