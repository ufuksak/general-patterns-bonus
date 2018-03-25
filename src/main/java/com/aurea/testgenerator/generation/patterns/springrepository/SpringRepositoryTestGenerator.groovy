package com.aurea.testgenerator.generation.patterns.springrepository

import com.aurea.testgenerator.generation.AbstractMethodTestGenerator
import com.aurea.testgenerator.generation.TestGeneratorResult
import com.aurea.testgenerator.generation.TestType
import com.aurea.testgenerator.generation.ast.DependableNode
import com.aurea.testgenerator.generation.names.NomenclatureFactory
import com.aurea.testgenerator.generation.names.TestMethodNomenclature
import com.aurea.testgenerator.reporting.CoverageReporter
import com.aurea.testgenerator.reporting.TestGeneratorResultReporter
import com.aurea.testgenerator.source.Unit
import com.github.javaparser.JavaParser
import com.github.javaparser.ast.ImportDeclaration
import com.github.javaparser.ast.Modifier
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration
import com.github.javaparser.ast.body.FieldDeclaration
import com.github.javaparser.ast.body.MethodDeclaration
import com.github.javaparser.ast.body.VariableDeclarator
import com.github.javaparser.ast.expr.AnnotationExpr
import com.github.javaparser.ast.expr.ClassExpr
import com.github.javaparser.ast.expr.MarkerAnnotationExpr
import com.github.javaparser.ast.expr.Name
import com.github.javaparser.ast.expr.SingleMemberAnnotationExpr
import com.github.javaparser.ast.type.ClassOrInterfaceType
import com.github.javaparser.symbolsolver.javaparsermodel.JavaParserFacade
import groovy.util.logging.Log4j2
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component

import static com.aurea.testgenerator.generation.source.Imports.ASSERTJ_ASSERTTHAT
import static com.aurea.testgenerator.generation.source.Imports.JUNIT_RUNWITH
import static com.aurea.testgenerator.generation.source.Imports.JUNIT_TEST
import static com.aurea.testgenerator.generation.source.Imports.SPRING_AUTOWIRED
import static com.aurea.testgenerator.generation.source.Imports.SPRING_DATAJPATEST
import static com.aurea.testgenerator.generation.source.Imports.SPRING_SPRINGRUNNER
import static com.aurea.testgenerator.generation.source.Imports.SPRING_TESTENTITYMANAGER

@Component
@Profile("manual")
@Log4j2
class SpringRepositoryTestGenerator extends AbstractMethodTestGenerator {

    private static final String REPOSITORY = 'Repository'
    private static final String STRING = 'String'
    private static final String LONG = 'Long'
    private static final String TEST_ENTITY_MANAGER = 'TestEntityManager'
    private static final String ENTITY_MANAGER = 'entityManager'
    private static final String AUTOWIRED = 'Autowired'
    private static final String DATA_JPA_TEST = 'DataJpaTest'
    private static final String RUN_WITH = 'RunWith'
    private static final String SPRINGRUNNER = 'SpringRunner'
    private static final String REPOSITORY_LOWER = 'repository'
    private static final String TEST_STRING = '"testString"'

    private NomenclatureFactory nomenclatures

    @Autowired
    SpringRepositoryTestGenerator(JavaParserFacade solver, TestGeneratorResultReporter reporter,
                                  CoverageReporter visitReporter, NomenclatureFactory nomenclatures) {
        super(solver, reporter, visitReporter, nomenclatures)
        this.nomenclatures = nomenclatures
    }

    @Override
    protected TestGeneratorResult generate(MethodDeclaration method, Unit unitUnderTest) {
        TestGeneratorResult result = new TestGeneratorResult()

        DependableNode<MethodDeclaration> testMethod = new DependableNode<>()

        TestMethodNomenclature testMethodNomenclature = nomenclatures.getTestMethodNomenclature(unitUnderTest.javaClass)
        String testName = testMethodNomenclature.requestTestMethodName(SpringRepositoryTestTypes.SPRING_REPOSITORY_FIND_ENTITY, method)
        ClassOrInterfaceDeclaration parentClass = method.getAncestorOfType(ClassOrInterfaceDeclaration).get()

        List<String> typeNames = parentClass.extendedTypes[0].findAll(ClassOrInterfaceType).nameAsString
        def entityTypeName = typeNames.find { !it.contains(REPOSITORY) && !it.contains(STRING) && !it.contains(LONG) }
        if (entityTypeName) { fillTestMethod(entityTypeName, testName, method, testMethod, result) }

        result
    }

    private static fillTestMethod(String className, String testName, MethodDeclaration method,
                                  DependableNode<MethodDeclaration> testMethod, result) {
        def methodName = method.nameAsString
        def firstParamName = method.parameters[0].nameAsString.capitalize()
        String testCode = """
                @Test
                public void ${testName}() throws Exception {
                    // given
                    ${className} entity = new ${className}();

                    ${fillParams(method)}
                    entityManager.persist(entity);
                    entityManager.flush();
                    
                    // when
                    ${className} found = repository.${methodName}(${method.parameters.name.join(', ')});
                    
                    // then
                    assertThat(found.get${firstParamName}()).isEqualTo(entity.get${firstParamName}());
                }
            """

        testMethod.node = JavaParser.parseBodyDeclaration(testCode).asMethodDeclaration()
        fillClassLevel(testMethod, method)
        result.tests << testMethod
    }

    // TODO: parse param types, assign default values
    // TODO: parse collections types
    // TODO: parse other standard method names
    private static fillParams(MethodDeclaration methodDeclaration) {
        def sb = new StringBuilder()
        methodDeclaration.parameters.each {
            def fieldName = it.name.identifier
            def fieldType = it.type.asString()
            sb.append("$fieldType $fieldName = new $fieldType($TEST_STRING);${System.lineSeparator()}")
            sb.append("entity.set${fieldName.capitalize()}($fieldName);${System.lineSeparator()}")
        }
        sb.toString()
    }

    private static void fillClassLevel(DependableNode<MethodDeclaration> testMethod, MethodDeclaration method) {
        testMethod.dependency.classAnnotations.addAll(annotationsList())
        ClassOrInterfaceDeclaration parentClass = method.getAncestorOfType(ClassOrInterfaceDeclaration).get()
        testMethod.dependency.fields.addAll(listFields(parentClass.nameAsString))

        testMethod.dependency.imports.addAll(importsList())
    }

    private static List<FieldDeclaration> listFields(String parentClassName) {
        [new FieldDeclaration(EnumSet.of(Modifier.PRIVATE),
                new VariableDeclarator(new ClassOrInterfaceType(TEST_ENTITY_MANAGER), ENTITY_MANAGER))
                 .addAnnotation(AUTOWIRED),
         new FieldDeclaration(EnumSet.of(Modifier.PRIVATE),
                new VariableDeclarator(new ClassOrInterfaceType(parentClassName), REPOSITORY_LOWER))
                 .addAnnotation(AUTOWIRED)]
    }

    private static List<AnnotationExpr> annotationsList() {
        [new SingleMemberAnnotationExpr(new Name(RUN_WITH), new ClassExpr(new ClassOrInterfaceType(SPRINGRUNNER))),
         new MarkerAnnotationExpr(DATA_JPA_TEST)]
    }

    private static List<ImportDeclaration> importsList() {
        [JUNIT_TEST, JUNIT_RUNWITH, SPRING_AUTOWIRED, SPRING_TESTENTITYMANAGER,
         SPRING_DATAJPATEST, SPRING_SPRINGRUNNER, ASSERTJ_ASSERTTHAT]
    }


    @Override
    protected TestType getType() {
        SpringRepositoryTestTypes.SPRING_REPOSITORY_FIND_ENTITY
    }

    @Override
    protected boolean shouldBeVisited(Unit unit, MethodDeclaration method) {
        super.shouldBeVisited(unit, method) && isInterfaceExtendedFromRepository(method)
    }

    private static boolean isInterfaceExtendedFromRepository(MethodDeclaration method) {
        def parentNode = method.parentNode.get()
        parentNode instanceof ClassOrInterfaceDeclaration && parentNode.interface &&
                parentNode.extendedTypes.nameAsString.any { it.contains REPOSITORY }
    }
}
