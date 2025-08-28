package com.emi.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ConfigReader {
	private static final Logger logger = LogManager.getLogger(ConfigReader.class);
	private static Properties properties;
	private static final String CONFIG_FILE_PATH = "src/main/resources/config.properties";

	static {
		loadProperties();
	}

	/**
	 * Load properties from config file
	 */
	private static void loadProperties() {
		try {
			properties = new Properties();
			FileInputStream fileInputStream = new FileInputStream(CONFIG_FILE_PATH);
			properties.load(fileInputStream);
			fileInputStream.close();
			logger.info("Configuration properties loaded successfully");
		} catch (IOException e) {
			logger.error("Failed to load configuration properties: {}", e.getMessage(), e);
			throw new RuntimeException("Configuration file not found", e);
		}
	}

	/**
	 * Get property value by key
	 */
	public static String getProperty(String key) {
		String value = properties.getProperty(key);
		if (value == null) {
			logger.warn("Property not found for key: {}", key);
		}
		return value;
	}

	public static String getProperty(String key, String defaultValue) {
		String value = properties.getProperty(key);
		return (value == null || value.trim().isEmpty()) ? defaultValue : value;
	}

	private static int getIntPropertyWithFallback(String primaryKey, String fallbackKey, int defaultValue) {
		String val = properties.getProperty(primaryKey);
		if (val == null || val.trim().isEmpty()) {
			val = properties.getProperty(fallbackKey);
		}
		if (val == null || val.trim().isEmpty()) {
			return defaultValue;
		}
		try {
			return Integer.parseInt(val.trim());
		} catch (NumberFormatException ex) {
			return defaultValue;
		}
	}

	public static String getBrowser() {
		return getProperty("browser");
	}

	public static String getAppUrl() {
		return getProperty("url");
	}

	public static int getImplicitWait() {
		return getIntPropertyWithFallback("implicit.wait", "timeout", 10);
	}

	public static int getExplicitWait() {
		return getIntPropertyWithFallback("explicit.wait", "timeout", 10);
	}

	public static int getPageLoadTimeout() {
		return getIntPropertyWithFallback("page.load.timeout", "timeout", 30);
	}

	public static String getTestDataFile() {
		return getProperty("test.data.file");
	}

	public static String getExtentReportPath() {
		return getProperty("extent.report.path");
	}

	public static String getScreenshotPath() {
		return getProperty("screenshot.path");
	}

	public static String getExtentReportPathScreenshot() {
		return getProperty("base.path.extent.screenshot");
	}

	public static boolean isGridEnabled() {
		return Boolean.parseBoolean(getProperty("grid.enabled", "false"));
	}

	public static String getGridUrl() {
		return getProperty("grid.url", "");
	}

	public static String getSaveExcelPath() {
		String path = properties.getProperty("save.excel.path");
		return (path == null || path.trim().isEmpty()) ? "src/test/resources/testdata/emi_home_result.xlsx" : path;
	}

	public static String getSaveExcelSheetName() {
		String sheet = properties.getProperty("save.excel.sheet");
		return (sheet == null || sheet.trim().isEmpty()) ? "Sheet1" : sheet.trim();
	}
}