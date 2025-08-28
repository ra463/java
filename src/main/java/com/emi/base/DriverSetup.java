package com.emi.base;

import java.time.Duration;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.emi.utils.ConfigReader;

import io.github.bonigarcia.wdm.WebDriverManager;

public class DriverSetup {
	private static final Logger logger = LogManager.getLogger(DriverSetup.class);

	// ThreadLocal for thread-safe parallel execution
	private static final ThreadLocal<WebDriver> driver = new ThreadLocal<>();
	private static final ThreadLocal<WebDriverWait> wait = new ThreadLocal<>();

	public static void initializeDriver(String browserName) {
		try {
			logger.info("Initializing WebDriver for browser: {}", browserName);

			WebDriver localDriver;

			switch (browserName.toLowerCase()) {
			case "chrome":
				ChromeOptions chromeOptions = new ChromeOptions();
				chromeOptions.addArguments("--disable-notifications");
//				chromeOptions.addArguments("--disable-popup-blocking");
//				chromeOptions.addArguments("--start-maximized");
				chromeOptions.setPageLoadStrategy(PageLoadStrategy.EAGER);

				WebDriverManager.chromedriver().setup();
				localDriver = new ChromeDriver(chromeOptions);
				break;

			case "firefox":
				FirefoxOptions firefoxOptions = new FirefoxOptions();
				firefoxOptions.addArguments("--disable-notifications");
				firefoxOptions.setPageLoadStrategy(PageLoadStrategy.EAGER);

				WebDriverManager.firefoxdriver().setup();
				localDriver = new FirefoxDriver(firefoxOptions);
				localDriver.manage().window().maximize();
				break;

			case "edge":
				EdgeOptions edgeOptions = new EdgeOptions();
				edgeOptions.addArguments("--disable-notifications");
				edgeOptions.setPageLoadStrategy(PageLoadStrategy.EAGER);

				// ✅ Manually setting EdgeDriver to avoid WebDriverManager download
				localDriver = new EdgeDriver(edgeOptions);
				localDriver.manage().window().maximize();
				break;

			default:
				throw new IllegalArgumentException("Browser not supported: " + browserName);
			}

			// Timeouts
			localDriver.manage().timeouts().implicitlyWait(Duration.ofSeconds(ConfigReader.getImplicitWait()));
			localDriver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(ConfigReader.getPageLoadTimeout()));

			// Set driver and wait in ThreadLocal
			driver.set(localDriver);
			wait.set(new WebDriverWait(localDriver, Duration.ofSeconds(ConfigReader.getExplicitWait())));

			logger.info("WebDriver initialized successfully");

		} catch (Exception e) {
			logger.error("Failed to initialize WebDriver: {}", e.getMessage(), e);
			throw new RuntimeException("Driver initialization failed", e);
		}
	}

	public static void navigateToApplication() {
		String url = ConfigReader.getAppUrl();
		logger.info("Navigating to application URL: {}", url);
		try {
			getDriver().get(url);
			Thread.sleep(500);
			logger.info("Successfully navigated to application");
		} catch (Exception e) {
			logger.warn("Navigation issue: {}", e.getMessage());
			try {
				String readyState = (String) ((JavascriptExecutor) getDriver())
						.executeScript("return document.readyState");
				if ("complete".equalsIgnoreCase(readyState) || "interactive".equalsIgnoreCase(readyState)) {
					logger.warn("Document readyState is '{}'. Proceeding despite timeout.", readyState);
					return;
				}
			} catch (Exception ignored) {
			}
			throw new RuntimeException("Navigation failed", e);
		}
	}

	public static void tearDown() {
		try {
			if (driver.get() != null) {
				logger.info("Closing browser and cleaning up resources");
				driver.get().quit();
				driver.remove();
				wait.remove();
				logger.info("Browser closed successfully");
			}
		} catch (Exception e) {
			logger.error("Error during teardown: {}", e.getMessage(), e);
		}
	}

	public static WebDriver getDriver() {
		return driver.get();
	}

	public static WebDriverWait getWait() {
		return wait.get();
	}
}
