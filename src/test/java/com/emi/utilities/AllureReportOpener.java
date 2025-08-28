package com.emi.utilities;

import java.io.File;
import java.io.IOException;

public class AllureReportOpener {
	// clearing the previous allure reports
	public static void cleanAllureResults() {
		File resultsDir = new File("target/allure-results");
		if (resultsDir.exists() && resultsDir.isDirectory()) {
			for (File file : resultsDir.listFiles()) {
				file.delete();
			}
		}
	}

	public static void openAllureReport() {
		try {
			// Step 1: Generate Allure report
			ProcessBuilder generate = new ProcessBuilder(
					"C:\\Users\\2421305\\Downloads\\allure-2.29.0\\bin\\allure.bat", "generate",
					"target/allure-results", "-o", "target/allure-report", "--clean");
			generate.inheritIO(); // Optional: shows output in console
			Process genProcess = generate.start();
			genProcess.waitFor();

			// Step 2: Open Allure report in browser
			ProcessBuilder open = new ProcessBuilder("C:\\Users\\2421305\\Downloads\\allure-2.29.0\\bin\\allure.bat",
					"open", "target/allure-report");
			open.inheritIO();
			Process openProcess = open.start();
			openProcess.waitFor();

		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
	}

}
