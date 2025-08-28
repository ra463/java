package com.emi;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.emi.base.DriverSetup;
import com.emi.pages.CarEmiCalculatorPage;
import com.emi.pages.HomeLoanEmiCalculatorPage;
import com.emi.pages.LoanCalculatorPage;
import com.emi.utils.ConfigReader;
import com.emi.utils.TestDataProvider;

public class MainApp {
	private static final Logger logger = LogManager.getLogger(MainApp.class);
	private static WebDriver driver;
	private static WebDriverWait wait;

	private static CarEmiCalculatorPage carEmiPage;
	private static HomeLoanEmiCalculatorPage homeLoanPage;
	private static LoanCalculatorPage loanCalculatorPage;

	// =================== Preloaded Test Data (Sheet1, row 1) ==================
	private static String emicalcLoanAmount, emicalcRate, emicalcTerm, emicalcFees;
	private static String amountCalcEmi, amountCalcRate, amountCalcTerm, amountCalcFees;
	private static String tenureCalcAmount, tenureCalcEmi, tenureCalcRate, tenureCalcFees;
	private static String carAmount, carRate, carTerm;
	private static String homePrice, homeDown, homeInsAmt, homeRate, homeTerm, homeFees;
	// ==========================================================================

	public void setup() {
		DriverSetup.initializeDriver(ConfigReader.getBrowser());
		driver = DriverSetup.getDriver();
		wait = DriverSetup.getWait();
		DriverSetup.navigateToApplication();
		loadTestData();
	}

	private static void loadTestData() {
		TestDataProvider data = new TestDataProvider(false, "");
		String source = ConfigReader.getProperty("test.data.source");
		int rowIndex = (source != null && (source.equalsIgnoreCase("json") || source.equalsIgnoreCase("xml"))) ? 0 : 1;
		// Read values from the appropriate starting row depending on source
		emicalcLoanAmount = data.getCellString(rowIndex, 0);
		emicalcRate = data.getCellString(rowIndex, 1);
		emicalcTerm = data.getCellString(rowIndex, 2);
		emicalcFees = data.getCellString(rowIndex, 3);
		amountCalcEmi = data.getCellString(rowIndex, 4);
		amountCalcRate = data.getCellString(rowIndex, 5);
		amountCalcTerm = data.getCellString(rowIndex, 6);
		amountCalcFees = data.getCellString(rowIndex, 7);
		tenureCalcAmount = data.getCellString(rowIndex, 8);
		tenureCalcEmi = data.getCellString(rowIndex, 9);
		tenureCalcRate = data.getCellString(rowIndex, 10);
		tenureCalcFees = data.getCellString(rowIndex, 11);
		carAmount = data.getCellString(rowIndex, 12);
		carRate = data.getCellString(rowIndex, 13);
		carTerm = data.getCellString(rowIndex, 14);
		homePrice = data.getCellString(rowIndex, 15);
		homeDown = data.getCellString(rowIndex, 16);
		homeInsAmt = data.getCellString(rowIndex, 17);
		homeRate = data.getCellString(rowIndex, 18);
		homeTerm = data.getCellString(rowIndex, 19);
		homeFees = data.getCellString(rowIndex, 20);
	}

	public static void carEmiCalculation() {
		logger.info("\n========================================");
		logger.info("STARTING CAR EMI CALCULATION");
		logger.info("========================================");

		try {
			carEmiPage = new CarEmiCalculatorPage(driver, wait);

			// Perform Car EMI calculation
			carEmiPage.calculateCarEmi(carAmount, carRate, carTerm);

			// Scroll to results
			carEmiPage.scrollToResults();

			// Get the Result
			carEmiPage.getTheResults();
		} catch (Exception e) {
			logger.error("Error in Car EMI calculation: {}", e.getMessage(), e);
		}
	}

	public static void homeLoanEmiWithExcelExtraction() {
		logger.info("\n========================================");
		logger.info("STARTING HOME LOAN EMI CALCULATION");
		logger.info("========================================");

		try {
			// Perform Home Loan calculation and extract data to Excel
			homeLoanPage = new HomeLoanEmiCalculatorPage(driver, wait);

			homeLoanPage.navigateToHomeLoanCalculator();

			homeLoanPage.fillValues(homePrice, homeDown, homeInsAmt, homeRate, homeTerm, homeFees);

//			homeLoanPage.emiResultSave();
			logger.info("HOME LOAN EMI CALCULATION TEST COMPLETED SUCCESSFULLY");

		} catch (Exception e) {
			logger.error("Error in Home Loan EMI calculation: {}", e.getMessage(), e);
		}
	}

	public static void testLoanCalculatorUIValidations() {
		logger.info("\n========================================");
		logger.info("STARTING LOAN CALCULATOR UI VALIDATION TEST");
		logger.info("========================================");

		try {
			loanCalculatorPage = new LoanCalculatorPage(driver, wait);

			loanCalculatorPage.navigateToLoanCalculator();
			// EMI Calculator tab inputs (preloaded)
			loanCalculatorPage.validateTextBoxes(emicalcLoanAmount, emicalcRate, emicalcTerm, emicalcFees);
			loanCalculatorPage.validateTenureTypeChange();

			// Loan Amount Calculator (preloaded)
			loanCalculatorPage.clickLoanAmountCalculatorBtn();
			loanCalculatorPage.validateLoanAmountTextBoxes(amountCalcEmi, amountCalcRate, amountCalcTerm,
					amountCalcFees);
			loanCalculatorPage.validateTenureTypeChange();

			// Loan Tenure Calculator (preloaded)
			loanCalculatorPage.clickLoanTenureCalculatorBtn();
			loanCalculatorPage.validateLoanTenureTextBoxes(tenureCalcAmount, tenureCalcEmi, tenureCalcRate,
					tenureCalcFees);

		} catch (Exception e) {
			logger.error("Error in Loan Calculator UI validation: {}", e.getMessage(), e);
		}
	}

	public static void main(String[] args) {
		MainApp app = new MainApp();
		app.setup();

		try {
//			Thread.sleep(20000);

			carEmiCalculation();
			Thread.sleep(3500);
			homeLoanEmiWithExcelExtraction();
			Thread.sleep(3500);
			testLoanCalculatorUIValidations();
		} catch (InterruptedException e) {
			logger.error("Interrupted: {}", e.getMessage(), e);
		} finally {
			DriverSetup.tearDown();
		}
	}
}
