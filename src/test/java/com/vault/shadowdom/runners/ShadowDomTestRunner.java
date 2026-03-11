package com.vault.shadowdom.runners;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;
import org.testng.annotations.DataProvider;

/**
 * TestNG test runner for the VAULT Shadow DOM Cucumber suite.
 *
 * Extends AbstractTestNGCucumberTests which wires Cucumber into TestNG's
 * lifecycle — each scenario becomes a TestNG test method.
 *
 * Run options:
 *   mvn test                          → runs all scenarios
 *   mvn test -Dcucumber.filter.tags="@smoke"   → runs only @smoke tagged scenarios
 */
@CucumberOptions(
        // Path to feature files
        features = "src/test/resources/features",

        // Package(s) containing step definitions and hooks
        glue = {
            "com.vault.shadowdom.steps",
            "com.vault.shadowdom.hooks"
        },

        // Reporting plugins
        plugin = {
            "pretty",                                           // readable console output
            "html:target/cucumber-reports/report.html",        // HTML report
            "json:target/cucumber-reports/report.json",        // JSON (for CI integrations)
            "junit:target/cucumber-reports/report.xml",        // JUnit XML (for TestNG / CI)
            "timeline:target/cucumber-reports/timeline"        // timeline view
        },

        // Show step snippets for any undefined steps
        snippets = CucumberOptions.SnippetType.CAMELCASE,

        // Publish summary to cucumber.io (set to true if you want cloud reports)
        publish = false,

        // Monochrome: cleaner console output without ANSI colour codes
        monochrome = true
)
public class ShadowDomTestRunner extends AbstractTestNGCucumberTests {

    /**
     * Override the data provider to allow parallel scenario execution.
     * Set parallel = true here (and in testng.xml) when you want to run
     * scenarios concurrently — keep false for shadow DOM tests to avoid
     * shared browser state issues.
     */
    @Override
    @DataProvider(parallel = false)
    public Object[][] scenarios() {
        return super.scenarios();
    }
}
