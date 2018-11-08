package com.aurea.testgenerator.generation.patterns.casting;

import com.aurea.testgenerator.generation.names.NomenclatureFactory;
import com.aurea.testgenerator.reporting.CoverageReporter;
import com.aurea.testgenerator.reporting.TestGeneratorResultReporter;
import com.aurea.testgenerator.source.Unit;
import com.github.javaparser.ast.body.MethodDeclaration
import com.github.javaparser.ast.expr.Expression
import com.github.javaparser.ast.expr.NameExpr
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.symbolsolver.javaparsermodel.JavaParserFacade
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component;

@Component
@Profile("casting")
class PlainCastingReturnTestGenerator extends CastingReturnTestGenerator {
    PlainCastingReturnTestGenerator(JavaParserFacade solver, TestGeneratorResultReporter reporter, CoverageReporter visitReporter, NomenclatureFactory nomenclatures) {
        super(solver, reporter, visitReporter, nomenclatures);
    }

    @Override
    protected boolean shouldBeVisited(Unit unit, MethodDeclaration method) {
        return super.shouldBeVisited(unit, method) &&
                hasCastReturn(method) &&
                {
                    def field = getCastField(method)
                    !(findFieldSetterInUnit(unit, field).isEmpty())
                }
    }

    private static boolean hasCastReturn(MethodDeclaration method) {
        method.getBody().present &&
                method.getBody().get().getStatements() != null &&
                method.getBody().get().getStatements().any {
                    it.isReturnStmt() && it.asReturnStmt().expression
                            .filter { it.isCastExpr() || containsCastMethod(it) }
                            .present
                }
    }

    @Override
    protected NameExpr getCastField(MethodDeclaration method) {
        List<Statement> returnStmts = method.getBody().get().getStatements()
                .findAll { it.isReturnStmt() }
                .collect { it.asReturnStmt() }
        def returnStmtExpr = returnStmts
                .findAll { it.expression.map { it.castExpr || containsCastMethod(it) }.present }
                .first().expression
        if (returnStmtExpr.present && returnStmtExpr.get().isMethodCallExpr()) {
            returnStmtExpr.get().asMethodCallExpr().arguments.first() as NameExpr
        } else {
            returnStmtExpr.map { it.asCastExpr().expression }.get() as NameExpr
        }
    }
}