package com.emi.hooks;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Reporter;

import com.emi.base.DriverSetup;
import com.emi.utils.ConfigReader;

import io.cucumber.java.After;
import io.cucumber.java.AfterStep;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;

public class CucumberHooks {
	private WebDriver driver;
	private WebDriverWait wait;
	public String browser;

	@Before
	public void setUp() {
		browser = Reporter.getCurrentTestResult().getTestContext().getCurrentXmlTest().getParameter("browser");
		
		DriverSetup.initializeDriver(browser);
		driver = DriverSetup.getDriver();
		wait = DriverSetup.getWait();
		DriverSetup.navigateToApplication();
		writeAllureEnvironment();
	}

	private void writeAllureEnvironment() {
		try {
			String resultsDir = System.getProperty("allure.results.directory", "target/allure-results");
			Path dir = Paths.get(resultsDir);
			Files.createDirectories(dir);
			Properties props = new Properties();
			props.setProperty("environment", ConfigReader.getProperty("environment", "local"));
			props.setProperty("browser", ConfigReader.getBrowser());
			props.setProperty("baseUrl", ConfigReader.getAppUrl());
			Path envFile = dir.resolve("environment.properties");
			try (OutputStream out = Files.newOutputStream(envFile)) {
				props.store(out, "Allure environment");
			}
		} catch (IOException ignored) {
			// ignore environment write errors
		}
	}

	private byte[] captureScreenshotBytes() {
		try {
			return ((TakesScreenshot) DriverSetup.getDriver()).getScreenshotAs(OutputType.BYTES);
		} catch (Exception e) {
			return new byte[0];
		}
	}

	@AfterStep
	public void afterEachStep(Scenario scenario) {
		boolean attachEveryStep = Boolean.parseBoolean(ConfigReader.getProperty("allure.screenshot.every.step"));
		if (scenario.isFailed() || attachEveryStep) {
			byte[] shot = captureScreenshotBytes();
			if (shot.length > 0) {
				scenario.attach(shot, "image/png", scenario.getName());
			}
		}
	}

	@After
	public void tearDown(Scenario scenario) {
		if (scenario.isFailed()) {
			byte[] shot = captureScreenshotBytes();
			if (shot.length > 0) {
				scenario.attach(shot, "image/png", scenario.getName());
			}
		}
		DriverSetup.tearDown();
	}
}