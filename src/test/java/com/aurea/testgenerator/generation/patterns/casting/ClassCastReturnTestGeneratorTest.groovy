package com.aurea.testgenerator.generation.patterns.casting

import com.aurea.testgenerator.MatcherPipelineTest
import com.aurea.testgenerator.generation.TestGenerator

class ClassCastReturnTestGeneratorTest extends MatcherPipelineTest {

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
            
                public long getValueAsLong() {
                    return Long.class.cast(value);
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
                    long actual = fixture.getValueAsLong();
                    assertEquals(expected, actual);
                }
            }
            """

    }

    @Override
    TestGenerator generator() {
        return new PlainCastingReturnTestGenerator(solver, reporter, visitReporter, nomenclatureFactory)
    }
}
