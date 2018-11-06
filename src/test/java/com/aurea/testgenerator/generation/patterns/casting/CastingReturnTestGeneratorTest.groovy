package com.aurea.testgenerator.generation.patterns.casting

import com.aurea.testgenerator.MatcherPipelineTest
import com.aurea.testgenerator.generation.TestGenerator

class CastingReturnTestGeneratorTest extends MatcherPipelineTest {
    def "Generate"() {
    }

    def "GetType"() {
    }

    def "ShouldBeVisited"() {
    }

    def "Generate1"() {
        expect:
        onClassCodeExpect """
            public class MyClassType {
                private int value;
            
                public int getValue() {
                    return value;
                }
            
                public void setValue(int value) {
                    this.value = value;
                }
            
                public long getValueAsLong() {
                    return (long) value;
                }
            }
        """, """
            package sample;

            import javax.annotation.Generated;
            import org.junit.Test;
            import static org.junit.Assert.assertEquals;
            
            @Generated("GeneralPatterns")
            public class FooPatternTest {
            
                @Test
                public void getValueAsLongCasting() {
                    Foo object = new Foo();
                    int expected = 42;
                    object.setValue(expected);
                    long actual = object.getValueAsLong();
                    assertEquals(expected, actual);
                }
            }
            """

    }

    @Override
    TestGenerator generator() {
        return new CastingReturnTestGenerator(solver, reporter, visitReporter, nomenclatureFactory)
    }
}
