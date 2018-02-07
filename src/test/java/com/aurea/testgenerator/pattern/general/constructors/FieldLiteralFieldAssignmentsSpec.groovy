package com.aurea.testgenerator.pattern.general.constructors

import com.aurea.testgenerator.MatcherPipelineTest
import com.aurea.testgenerator.ast.FieldAssignments
import com.aurea.testgenerator.generation.PatternToTest
import com.aurea.testgenerator.generation.constructors.FieldLiteralAssignmentsGenerator
import com.aurea.testgenerator.pattern.PatternMatcher
import com.aurea.testgenerator.value.ArbitraryClassOrInterfaceTypeFactory
import com.aurea.testgenerator.value.ArbitraryPrimitiveValuesFactory
import com.aurea.testgenerator.value.random.ValueFactoryImpl


class FieldLiteralFieldAssignmentsSpec extends MatcherPipelineTest {

    def "assigning integral literals should be asserted"() {
        expect:
        onClassCodeExpect """
            class Foo {
                int i;
                Foo() {
                    this.i = 42;
                }
            } 
        """, """     
            package sample;
            
            import static org.assertj.core.api.Assertions.assertThat;
            import org.junit.Test;
            
            public class FooTest {
                
                @Test
                public void test_Foo_AssignsConstantsToFields() throws Exception {
                    Foo foo = new Foo();
                    
                    assertThat(foo.i).isEqualTo(42);
                }
            }
        """
    }

    def "all fields are private - no test"() {
        expect:
        onClassCodeDoNotExpectTest """
            class Foo {
                private int i;
                Foo() {
                    this.i = 42;
                }
            } 
        """
    }

    def "assigning String literals should be asserted, field is a private one with a getter"() {
        expect:
        onClassCodeExpect """
            class Foo {
                private String i;
                Foo() {
                    this.i = "ABC";
                }
                
                public String getI() {
                    return this.i;
                }
            } 
        """, """     
            package sample;
            
            import static org.assertj.core.api.Assertions.assertThat;
            import org.junit.Test;
            
            public class FooTest {
                
                @Test
                public void test_Foo_AssignsConstantsToFields() throws Exception {
                    Foo foo = new Foo();
                    
                    assertThat(foo.getI()).isEqualTo("ABC");
                }
            }
        """
    }

    def "multiple assignments"() {
        expect:
        onClassCodeExpect """
            class Foo {
                String i;
                String b;
                Foo() {
                    this.i = "CFG";
                    this.b = "BDF";
                }
            } 
        """, """     
            package sample;
            
            import static org.assertj.core.api.Assertions.assertThat;
            import org.assertj.core.api.SoftAssertions;
            import org.junit.Test;
            
            public class FooTest {
                
                @Test
                public void test_Foo_AssignsConstantsToFields() throws Exception {
                    Foo foo = new Foo();
                    
                    SoftAssertions sa = new SoftAssertions();
                    sa.assertThat(foo.i).isEqualTo("CFG");
                    sa.assertThat(foo.b).isEqualTo("BDF");
                    sa.assertAll();
                }
            }
        """
    }

    def "multiple assignments to the same variable - should only assert the last one"() {
        expect:
        onClassCodeExpect """
            class Foo {
                String i;
                Foo() {
                    this.i = "CFG";
                    this.i = "BDF";
                }
            } 
        """, """     
            package sample;
            
            import static org.assertj.core.api.Assertions.assertThat;
            import org.junit.Test;
            
            public class FooTest {
                
                @Test
                public void test_Foo_AssignsConstantsToFields() throws Exception {
                    Foo foo = new Foo();
                    
                    assertThat(foo.i).isEqualTo("BDF");
                }
            }
        """
    }

    @Override
    PatternMatcher matcher() {
        return new ConstructorMatcher()
    }

    @Override
    PatternToTest patternToTest() {
        return new FieldLiteralAssignmentsGenerator(new FieldAssignments(solver), solver, valueFactory)
    }
}