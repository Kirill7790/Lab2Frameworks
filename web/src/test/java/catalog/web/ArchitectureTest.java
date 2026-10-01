package catalog.web;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

class ArchitectureTest {

    private static JavaClasses classes;

    @BeforeAll
    static void setup() {
        classes = new ClassFileImporter()
                .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
                .importPackages("catalog");
    }

    @Test
    void web_should_not_depend_on_persistence() {
        ArchRule rule = noClasses()
                .that().resideInAPackage("..web..")
                .and().haveSimpleNameNotEndingWith("AppContextListener")
                .should().dependOnClassesThat().resideInAPackage("..persistence..");
        rule.check(classes);
    }

    @Test
    void core_should_not_depend_on_servlet_or_jdbc() {
        ArchRule rule = noClasses()
                .that().resideInAPackage("..core..")
                .should().dependOnClassesThat().resideInAnyPackage(
                        "jakarta.servlet..",
                        "javax.servlet..",
                        "java.sql..",
                        "javax.sql..");
        rule.check(classes);
    }

    @Test
    void core_should_not_depend_on_web_or_persistence() {
        ArchRule rule = noClasses()
                .that().resideInAPackage("..core..")
                .should().dependOnClassesThat().resideInAnyPackage(
                        "..web..", "..persistence..");
        rule.check(classes);
    }

    @Test
    void controllers_should_live_only_in_web() {
        ArchRule rule = classes()
                .that().haveSimpleNameEndingWith("Servlet")
                .or().haveSimpleNameEndingWith("Controller")
                .should().resideInAPackage("..web..");
        rule.check(classes);
    }

    @Test
    void repositories_and_dao_should_live_only_in_persistence() {
        ArchRule rule = classes()
                .that().haveSimpleNameEndingWith("Repository")
                .or().haveSimpleNameEndingWith("Dao")
                .or().haveSimpleNameEndingWith("DAO")
                .should().resideInAPackage("..persistence..")
                .allowEmptyShould(true);
        rule.check(classes);
    }
}