package com.aurea.testgenerator.generation.patterns.casting

import com.aurea.testgenerator.generation.AbstractMethodTestGenerator
import com.aurea.testgenerator.generation.TestGeneratorResult
import com.aurea.testgenerator.generation.TestType
import com.aurea.testgenerator.generation.ast.DependableNode
import com.aurea.testgenerator.generation.names.NomenclatureFactory
import com.aurea.testgenerator.generation.names.TestMethodNomenclature
import com.aurea.testgenerator.generation.source.Imports
import com.aurea.testgenerator.reporting.CoverageReporter
import com.aurea.testgenerator.reporting.TestGeneratorResultReporter
import com.aurea.testgenerator.source.Unit
import com.github.javaparser.JavaParser
import com.github.javaparser.ast.body.MethodDeclaration
import com.github.javaparser.ast.expr.Expression
import com.github.javaparser.ast.expr.NameExpr
import com.github.javaparser.ast.stmt.Statement
import com.github.javaparser.symbolsolver.javaparsermodel.JavaParserFacade
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component

abstract class CastingReturnTestGenerator extends AbstractMethodTestGenerator {
    static def testValues = ["String" : "\"testValue\"", "int": 42, "Integer": 42,
                             "long"   : 42, "Long": 42, "short": 42, "Short": 42,
                             "Number" : 42, "char": 42, "Character": 42,
                             "float"  : 42, "Float": 42,
                             "double" : 42, "Double": 42,
                             "Boolean": Boolean.TRUE, "boolean": Boolean.TRUE]

    CastingReturnTestGenerator(JavaParserFacade solver, TestGeneratorResultReporter reporter, CoverageReporter visitReporter, NomenclatureFactory nomenclatures) {
        super(solver, reporter, visitReporter, nomenclatures);
    }

    @Override
    protected TestGeneratorResult generate(MethodDeclaration method, Unit unitUnderTest) {
        NameExpr fieldNameExpr = getCastField(method)

        TestMethodNomenclature testMethodNomenclature = nomenclatures.getTestMethodNomenclature(unitUnderTest.javaClass)
        TestGeneratorResult result = new TestGeneratorResult()
        String testName = testMethodNomenclature.requestTestMethodName(BootcampTestTypes.CASTING_RETURN, method)
        def setter = findFieldSetterInUnit(unitUnderTest, fieldNameExpr).first()
        def fieldType = setter.parameters.first().type
        def expectedValue = testValues.getOrDefault(fieldType.toString(), "new $fieldType()")
        String testBody =
                """@Test public void $testName(){
                ${unitUnderTest.className} fixture = new ${unitUnderTest.className}();
                $fieldType expected = $expectedValue;
                fixture.${setter.name}(expected);
                ${method.type.toString()} actual = fixture.${method.name}();
                assertEquals(expected, actual);
            }""";
        DependableNode<MethodDeclaration> node = DependableNode.from(
                JavaParser.parseBodyDeclaration(testBody).asMethodDeclaration())
        node.dependency.imports += [Imports.JUNIT_TEST, Imports.JUNIT_ASSERT_EQUALS]
        result.tests << node
        result
    }

    protected static boolean containsCastMethod(Expression expr) {
        expr.isMethodCallExpr() &&
                expr.asMethodCallExpr().scope.map {
                    it.isClassExpr()
                }.present &&
                expr.asMethodCallExpr().name.toString() == "cast"
    }

    protected abstract NameExpr getCastField(MethodDeclaration methodDeclaration)

    @Override
    protected TestType getType() {
        BootcampTestTypes.CASTING_RETURN;
    }

    protected static List<MethodDeclaration> findFieldSetterInUnit(Unit unit, NameExpr field) {
        unit.cu.findAll(MethodDeclaration.class).findAll {
            it.isPublic() &&
                    it.name.toString().toLowerCase().equalsIgnoreCase("set${field.name}") &&
                    it.parameters.every { it.name == field.name }
        }
    }

}
