package com.emi.utils;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

public class ScreenshotUtils {
	private static final Logger logger = LogManager.getLogger(ScreenshotUtils.class);

	/**
	 * Take screenshot and save to specified path
	 * 
	 * @param driver WebDriver instance
	 * @param name   Name for screenshot filename
	 * @return Screenshot file path
	 */
	public static String getScreenshot(WebDriver driver, String name) {
		try {
			File screenshotDir = new File(ConfigReader.getScreenshotPath());
			if (!screenshotDir.exists()) {
				screenshotDir.mkdirs();
			}
			String filePath = ConfigReader.getScreenshotPath() + getScreenshotName(name);
			TakesScreenshot takesScreenshot = (TakesScreenshot) driver;
			File sourceFile = takesScreenshot.getScreenshotAs(OutputType.FILE);
			File destFile = new File(filePath);
			FileUtils.copyFile(sourceFile, destFile);

			logger.info("Screenshot saved: {}", filePath);
			return filePath;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	private static String getScreenshotName(String name) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy_MM_dd_HH_mm_ss");
		return name + "_" + LocalDateTime.now().format(formatter) + ".png";
	}
}