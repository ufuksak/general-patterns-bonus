package com.aurea.testgenerator.generation.patterns.casting

import com.aurea.testgenerator.generation.TestGeneratorResult
import com.aurea.testgenerator.generation.names.NomenclatureFactory
import com.aurea.testgenerator.reporting.CoverageReporter
import com.aurea.testgenerator.reporting.TestGeneratorResultReporter
import com.aurea.testgenerator.source.Unit
import com.github.javaparser.ast.body.MethodDeclaration
import com.github.javaparser.ast.expr.NameExpr
import com.github.javaparser.symbolsolver.javaparsermodel.JavaParserFacade
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component

@Component
@Profile("casting")
class ConditionCastingReturnTestGenerator extends CastingReturnTestGenerator {
    ConditionCastingReturnTestGenerator(JavaParserFacade solver, TestGeneratorResultReporter reporter, CoverageReporter visitReporter, NomenclatureFactory nomenclatures) {
        super(solver, reporter, visitReporter, nomenclatures);
    }

    @Override
    protected TestGeneratorResult generate(MethodDeclaration method, Unit unitUnderTest) {
        TestGeneratorResult result = super.generate(method, unitUnderTest)
        NameExpr fieldNameExpr = getCastField(method)
        def setter = findFieldSetterInUnit(fieldNameExpr, unitUnderTest).first()
        def fieldType = setter.parameters.first().type

        result.tests += generate(method, unitUnderTest, null, fieldType, setter.name).tests
        result
    }

    @Override
    protected boolean shouldBeVisited(Unit unit, MethodDeclaration method) {
        return super.shouldBeVisited(unit, method) &&
                hasCastReturn(method) &&
                {
                    def field = getCastField(method)
                    field && !(findFieldSetterInUnit(field, unit).isEmpty())
                }
    }

    private static boolean hasCastReturn(MethodDeclaration method) {
        method.getBody().present &&
                method.getBody().get().getStatements() != null &&
                method.getBody().get().getStatements().any {
                    it.isIfStmt() &&
                            it.asIfStmt().thenStmt.isBlockStmt() &&
                            it.asIfStmt().thenStmt.asBlockStmt().statements.any {
                                it.isReturnStmt() && it.asReturnStmt().expression
                                        .filter { it.isCastExpr() || containsCastMethod(it) }
                                        .present
                            }
                }
    }


    @Override
    protected NameExpr getCastField(MethodDeclaration method) {
        def ifstmt = method.getBody().get().getStatements().last()

        if (!(ifstmt.isIfStmt() && ifstmt.asIfStmt().thenStmt.isBlockStmt())) return null

        def returnStmtExpr = ifstmt.asIfStmt().thenStmt.asBlockStmt().statements.findAll {
            it.isReturnStmt() && it.asReturnStmt().expression
                    .filter { it.isCastExpr() || containsCastMethod(it) }
                    .present
        }.collect { it.asReturnStmt() }
                .first().expression
        if (returnStmtExpr.present && returnStmtExpr.get().isMethodCallExpr()) {
            returnStmtExpr.get().asMethodCallExpr().arguments.first() as NameExpr
        } else {
            returnStmtExpr.map { it.asCastExpr().expression }.get() as NameExpr
        }
    }
}