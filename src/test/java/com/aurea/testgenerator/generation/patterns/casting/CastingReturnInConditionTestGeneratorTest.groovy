package com.aurea.testgenerator.generation.patterns.casting

import com.aurea.testgenerator.MatcherPipelineTest
import com.aurea.testgenerator.generation.TestGenerator

class CastingReturnInConditionTestGeneratorTest extends MatcherPipelineTest {

    def "Generate"() {
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
            
                public Long getValueAsLong() {
                    if (value instanceof Long) {
                        return (Long) value;
                    } else {
                        return null;
                    }
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
                    Foo fixture = new Foo();
                    int expected = 42;
                    fixture.setValue(expected);
                    Long actual = fixture.getValueAsLong();
                    assertEquals(expected, actual);
                }
            }
            """

    }

    @Override
    TestGenerator generator() {
        return new ConditionCastingReturnTestGenerator(solver, reporter, visitReporter, nomenclatureFactory)
    }
}
