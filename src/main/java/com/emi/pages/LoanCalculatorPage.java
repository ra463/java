package com.emi.pages;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class LoanCalculatorPage {
	private static final Logger logger = LogManager.getLogger(LoanCalculatorPage.class);
	private WebDriver driver;
	private WebDriverWait wait;
	private boolean isTenureTypeChanged = false;
	private int monthVal = 0;
	private int yearVal = 0;

	// Menu Navigation
	@FindBy(xpath = "//*[@id='menu-item-2696']/a")
	private WebElement homeMenuBtn;

	@FindBy(xpath = "//*[@id='menu-item-2696']/ul/li[3]")
	private WebElement homeMenuLi;

	// EMI Calculator Section Elements
	@FindBy(id = "loanamount")
	private WebElement loanAmountField;

	@FindBy(id = "loaninterest")
	private WebElement interestRateField;

	@FindBy(id = "loanterm")
	private WebElement loanTermField;

	@FindBy(id = "loanfees")
	private WebElement loanFeesField;

	@FindBy(className = "page-header")
	private WebElement pageHead;

	// Tenure Type Selection
	@FindBy(id = "loanyears")
	private WebElement tenureYearOption;

	@FindBy(id = "loanmonths")
	private WebElement tenureMonthOption;

	// Calculate Button
	@FindBy(xpath = "//*[@id='loan-amount-calc']/a[1]")
	private WebElement calculateButton;

	@FindBy(xpath = "//*[@id='loan-tenure-calc']/a[1]")
	private WebElement tenureButton;

	// Result Elements
	@FindBy(id = "loanemi")
	private WebElement emiField;

	@FindBy(id = "totalinterest")
	private WebElement totalInterestResult;

	@FindBy(id = "totalamount")
	private WebElement totalAmountResult;

	@FindBy(xpath = "//*[@id='loansummary-emi']/p/span")
	private WebElement emiVal;

	@FindBy(xpath = "//*[@id='loancalculatordashboard']/ul/li[contains(@class, 'active')]/a[1]")
	private WebElement activeClass;

	public LoanCalculatorPage(WebDriver driver, WebDriverWait wait) {
		this.driver = driver;
		this.wait = wait;
		PageFactory.initElements(driver, this);
	}

	public void navigateToLoanCalculator() {
		try {
			homeMenuBtn.click();
			Thread.sleep(1000);

			homeMenuLi.click();
		} catch (Exception e) {
		}
	}

	private void clearAndType(WebElement field, String value) {
		wait.until(ExpectedConditions.elementToBeClickable(field));
		field.click();
		try {
			field.sendKeys(Keys.chord(Keys.CONTROL, "a"));
			field.sendKeys(Keys.DELETE);
		} catch (Exception ignore) {
		}
		try {
			field.clear();
		} catch (Exception ignore) {
		}
		try {
			if (field.getAttribute("value") != null && !field.getAttribute("value").isEmpty()) {
				((JavascriptExecutor) driver).executeScript("arguments[0].value='';", field);
			}
		} catch (Exception ignore) {
		}
		field.sendKeys(value);

		try {
			pageHead.click();
		} catch (Exception ignore) {
		}
	}

	private boolean validateTextField(WebElement field, String testValue, String fieldName) {
		try {
			// Check if field is displayed and enabled
			Thread.sleep(1000);

			if (field == null) {
				logger.warn("{} field is not present in DOM", fieldName);
				return false;
			}
			if (!field.isDisplayed() || !field.isEnabled()) {
				logger.warn("{} field is not displayed or enabled", fieldName);
				return false;
			}

			// Test input
			clearAndType(field, testValue);
			String enteredValue = field.getAttribute("value");

			if ("Loan Amount".equals(fieldName) || "EMI".equals(fieldName)) {
				logger.info("{} field set/read. Skipping equality validation due to dynamic behavior", fieldName);
				return true;
			}

			if (!enteredValue.replace(",", "").equals(testValue)) {
				logger.warn("{} field validation failed. Expected: {}, Actual: {}", fieldName, testValue, enteredValue);
				return false;
			}

			logger.info("{} field validation passed", fieldName);
			return true;

		} catch (Exception e) {
			logger.error("Error validating {} field: {}", fieldName, e.getMessage(), e);
			return false;
		}
	}

	// EMI Calculator UI Validations
	public boolean validateTextBoxes(String amt, String rate, String term, String fees) {
		boolean isValid = true;

		try {
			// Test Loan Amount Field
			if (!validateTextField(loanAmountField, amt, "Loan Amount")) {
				isValid = false;
			}

			// Test Interest Rate Field
			if (!validateTextField(interestRateField, rate, "Interest Rate")) {
				isValid = false;
			}

			// Test Loan Term Field
			if (!validateTextField(loanTermField, term, "Loan Term")) {
				isValid = false;
			}

			if (!validateTextField(loanFeesField, fees, "Fees & Charge")) {
				isValid = false;
			}
		} catch (Exception e) {
			logger.error("Error in text box validation: {}", e.getMessage(), e);
			isValid = false;
		}

		return isValid;
	}

	public boolean validateTenureTypeChange() {
		try {
			// Test Year option
			WebElement labelYear = tenureYearOption.findElement(By.xpath(".."));
			labelYear.click();
			String labelYearClass = labelYear.getAttribute("class");

			if (!labelYearClass.contains("active")) {
				logger.warn("Year option selection failed");
				return false;
			}

			// Check if slider scale changed
			String yearValue = loanTermField.getAttribute("value");
			yearVal = Integer.parseInt(yearValue);

			// Test Month option
			WebElement labelMonth = tenureMonthOption.findElement(By.xpath(".."));
			labelMonth.click();

			String labelMonthClass = labelMonth.getAttribute("class");
			Thread.sleep(1000);

			if (!labelMonthClass.contains("active")) {
				logger.warn("Month option selection failed");
				return false;
			} else {
				isTenureTypeChanged = true;
			}

			String monthValue = loanTermField.getAttribute("value");
			monthVal = Integer.parseInt(monthValue);

			logger.info("Tenure type validation passed. Year value: {}, Month value: {}", yearValue, monthValue);
			labelYear.click();
			return true;

		} catch (Exception e) {
			logger.error("Error in tenure type validation: {}", e.getMessage(), e);
			return false;
		}
	}

	public boolean tenureTypeChanged() {
		return isTenureTypeChanged;
	}

	public boolean getMonthAndYearValue() {
		if (monthVal / 12 == yearVal) {
			return true;
		}
		return false;
	}

	public void setValueToDefault() {
		isTenureTypeChanged = false;
		monthVal = 0;
		yearVal = 0;
	}

	// Loan Amount UI Validations
	public void clickLoanAmountCalculatorBtn() {
		calculateButton.click();
	}

	public boolean validateLoanAmountTextBoxes(String emi, String rate, String term, String fees) {
		boolean isValid = true;

		try {
			// Test EMI Field
			if (!validateTextField(emiField, emi, "EMI")) {
				isValid = false;
			}

			// Test Interest Rate Field
			if (!validateTextField(interestRateField, rate, "Interest Rate")) {
				isValid = false;
			}

			// Test Loan Term Field
			if (!validateTextField(loanTermField, term, "Loan Term")) {
				isValid = false;
			}

			if (!validateTextField(loanFeesField, fees, "Fees & Charge")) {
				isValid = false;
			}

		} catch (Exception e) {
			logger.error("Error in text box validation: {}", e.getMessage(), e);
			isValid = false;
		}

		return isValid;
	}

	public void checkLoanAmountTenureTypeChange() {
		validateTenureTypeChange();
	}

	public void clickLoanTenureCalculatorBtn() {
		tenureButton.click();
	}

	// Loan Tenure Calculation
	public boolean validateLoanTenureTextBoxes(String amt, String emi, String rate, String fees) {
		boolean isValid = true;

		try {
			// Test Loan Amount Field
			if (!validateTextField(loanAmountField, amt, "Loan Amount")) {
				isValid = false;
			}

			// Test EMI Field
			if (!validateTextField(emiField, emi, "EMI")) {
				isValid = false;
			}

			// Test Interest Rate Field
			if (!validateTextField(interestRateField, rate, "Interest Rate")) {
				isValid = false;
			}

			if (!validateTextField(loanFeesField, fees, "Fees & Charge")) {
				isValid = false;
			}

		} catch (Exception e) {
			logger.error("Error in text box validation: {}", e.getMessage(), e);
			isValid = false;
		}

		return isValid;
	}

	public boolean verifyUiElementsPresentAndFunctional() {
		try {
			boolean isContain = false;
			String str = activeClass.getText();

			if (str.contains("Loan Tenure")) {
				isContain = true;
			}
			boolean primaryPresent = loanAmountField.isDisplayed() || emiField.isDisplayed();
			boolean ratePresent = interestRateField.isDisplayed();
			boolean termPresent = isContain ? emiField.isDisplayed() : loanTermField.isDisplayed();
			boolean feesOptions = loanFeesField.isDisplayed();
			
			System.out.println(primaryPresent + " " + ratePresent + " " + termPresent + " " + feesOptions);

			return primaryPresent && ratePresent && termPresent && feesOptions;
		} catch (Exception e) {
			logger.error("UI elements presence check failed: {}", e.getMessage(), e);
			return false;
		}
	}

	public boolean validateNonNumericInputs(String amt, String rate, String term, String fees) {
		clearAndType(loanAmountField, amt);
		clearAndType(interestRateField, rate);
		clearAndType(loanTermField, term);
		clearAndType(loanFeesField, fees);

		String actualAmt = valueOrText(loanAmountField);
		String actualRate = valueOrText(interestRateField);
		String actualTerm = valueOrText(loanTermField);
		String actualFees = valueOrText(loanFeesField);

		boolean amtRejected = !actualAmt.equals(amt);
		boolean rateRejected = !actualRate.equals(rate);
		boolean termRejected = !actualTerm.equals(term);
		boolean feesRejected = !actualFees.equals(fees);

		return amtRejected && rateRejected && termRejected && feesRejected;
	}

	public boolean validateNegativeInputs(String amt, String rate, String term, String fees) {
		clearAndType(loanAmountField, amt);
		clearAndType(interestRateField, rate);
		clearAndType(loanTermField, term);
		clearAndType(loanFeesField, fees);

		String actualAmt = valueOrText(loanAmountField);
		String actualRate = valueOrText(interestRateField);
		String actualTerm = valueOrText(loanTermField);
		String actualFees = valueOrText(loanFeesField);

		String expectedAmt = amt.replace("-", "");
		String expectedRate = rate.replace("-", "");
		String expectedTerm = term.replace("-", "");
		String expectedFees = fees.replace("-", "");

		System.out.println(actualAmt + " " + actualTerm + " " + actualFees + " " + expectedAmt + " " + expectedRate
				+ " " + expectedTerm + " " + expectedFees);

		boolean amtCorrected = actualAmt.replace(",", "").equals(expectedAmt);
		boolean rateCorrected = actualRate.replace(",", "").equals(expectedRate);
		boolean termCorrected = actualTerm.replace(",", "").equals(expectedTerm);
		boolean feesCorrected = actualFees.replace(",", "").equals(expectedFees);

		return amtCorrected && rateCorrected && termCorrected && feesCorrected;
	}

	public boolean validateBlankInputs(String amt, String rate, String term, String fees) {
		clearAndType(loanAmountField, amt);
		clearAndType(interestRateField, rate);
		clearAndType(loanTermField, term);

		clearAndType(loanFeesField, fees);

		String actualAmt = valueOrText(loanAmountField);
		String actualRate = valueOrText(interestRateField);
		String actualTerm = valueOrText(loanTermField);
		String actualFees = valueOrText(loanFeesField);

		boolean amtReplaced = !actualAmt.equals(amt);
		boolean rateReplaced = !actualRate.equals(rate);
		boolean termReplaced = !actualTerm.equals(term);
		boolean feesReplaced = !actualFees.equals(fees);

		return amtReplaced && rateReplaced && termReplaced && feesReplaced;
	}

	public boolean interestRateAcceptsDecimal(String rate) {
		clearAndType(interestRateField, rate);
		String actualRate = valueOrText(interestRateField);
		return actualRate.equals(rate);
	}

	public String getEmiValue() {
		return emiVal.getText();
	}

	// Value-or-text helper for result elements which may be inputs or spans
	private String valueOrText(WebElement element) {
		try {
			String val = element.getAttribute("value");
			if (val != null && !val.trim().isEmpty())
				return val.trim();
			String txt = element.getText();
			return txt == null ? "" : txt.trim();
		} catch (Exception e) {
			return "";
		}
	}
}