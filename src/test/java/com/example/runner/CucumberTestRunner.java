package com.example.runner;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;
import org.testng.annotations.DataProvider;

@CucumberOptions(
        features = "src/test/resources/features",
        tags = "not @ignore",
        glue = {
                "com.example.steps",
                "com.example.hooks" // includes Hooks for setup/teardown
        },
        plugin = {
                "pretty",
                "html:target/cucumber-reports.html",
                "json:target/cucumber-report.json",
                "io.qameta.allure.cucumber7jvm.AllureCucumber7Jvm"
        },
        monochrome = true
)
public class CucumberTestRunner extends AbstractTestNGCucumberTests {

    /** Run scenarios in parallel (each in its own thread with its own WebDriver). */
    @Override
    @DataProvider(parallel = true)
    public Object[][] scenarios() {
        return super.scenarios();
    }
}
