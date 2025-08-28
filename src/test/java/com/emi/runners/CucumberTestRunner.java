package com.emi.runners;

import org.testng.annotations.BeforeSuite;

import com.emi.utilities.AllureReportOpener;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;

@CucumberOptions(
		features = {"src/test/resources/features/"},
		glue = {"com.emi.stepdefinitions", "com.emi.hooks"},
//		tags="@tc_fn_03",
		plugin = {
			"pretty",
			"html:test-output/cucumber-reports/cucumber.html",
			"io.qameta.allure.cucumber7jvm.AllureCucumber7Jvm",
			"rerun:target/rerun.txt"
		}
)
public class CucumberTestRunner extends AbstractTestNGCucumberTests {
	@BeforeSuite
    public void beforeSuite() {
        // Clean previous Allure results before test execution
        AllureReportOpener.cleanAllureResults();
    }
}