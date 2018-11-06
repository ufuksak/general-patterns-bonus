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
import com.github.javaparser.ast.expr.NameExpr
import com.github.javaparser.ast.stmt.Statement
import com.github.javaparser.symbolsolver.javaparsermodel.JavaParserFacade
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component

@Profile("casting")
@Component
class CastingReturnTestGenerator extends AbstractMethodTestGenerator {

    CastingReturnTestGenerator(JavaParserFacade solver, TestGeneratorResultReporter reporter, CoverageReporter visitReporter, NomenclatureFactory nomenclatures) {
        super(solver, reporter, visitReporter, nomenclatures);
    }

    @Override
    protected TestGeneratorResult generate(MethodDeclaration method, Unit unitUnderTest) {
        List<Statement> returnStmts = method.getBody().get().getStatements()
                .findAll { it.isReturnStmt() }
                .collect { it.asReturnStmt() }
        def returnStmtExpr = returnStmts
                .findAll { it.expression.map { it.castExpr }.present }
                .first().expression
        def field = returnStmtExpr.map { it.asCastExpr().expression }.get() as NameExpr
        TestMethodNomenclature testMethodNomenclature = nomenclatures.getTestMethodNomenclature(unitUnderTest.javaClass)
        TestGeneratorResult result = new TestGeneratorResult();
        String testName = testMethodNomenclature.requestTestMethodName(BootcampTestTypes.CASTING_RETURN, method)

        String testBody =
                """@Test public void $testName(){
                    //arrange
                    ${unitUnderTest.className} object = new ${unitUnderTest.className}();
                    object.${findSetter(unitUnderTest, field)}(42);
                    //act
                    ${method.type.toString()} actual = object.${method.name}();
                    //assert
                    assertEquals(42, actual);
            }""";
        DependableNode<MethodDeclaration> node = DependableNode.from(
                JavaParser.parseBodyDeclaration(testBody).asMethodDeclaration())
        node.dependency.imports += [Imports.JUNIT_TEST, Imports.JUNIT_ASSERT_EQUALS]
        result.tests << node
        result
    }

    private static String findSetter(Unit unit, NameExpr field) {
        // TODO reuse pojoMethodsFinder
        unit.cu.findAll(MethodDeclaration.class).findAll {
            it.isPublic() &&
                    it.name.toString().startsWith("set") &&
                    it.parameters.every { it.name == field.name }
        }.first().name // TODO case if no setter
    }

    @Override
    protected TestType getType() {
        BootcampTestTypes.CASTING_RETURN;
    }

    @Override
    protected boolean shouldBeVisited(Unit unit, MethodDeclaration method) {
        super.shouldBeVisited(unit, method) &&
                hasCastReturn(method)
//        && findSetter(unit, field)
    }

    private static boolean hasCastReturn(MethodDeclaration method) {
        !method.getType().resolve().isVoid() &&
                method.getBody().get().getStatements().any {
                    it.isReturnStmt() && it.asReturnStmt().expression
                            .filter { it.isCastExpr() }.present
                }
    }

    @Override
    Collection<TestGeneratorResult> generate(Unit unit) {
        // TODO: 11/5/2018 test gen logic
        super.generate(unit);
    }
}
