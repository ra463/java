package com.emi.pages;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.emi.utils.ConfigReader;
import com.emi.utils.ExcelUtils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class HomeLoanEmiCalculatorPage {
	private static final Logger logger = LogManager.getLogger(HomeLoanEmiCalculatorPage.class);
	private WebDriver driver;
	private WebDriverWait wait;

	@FindBy(xpath = "//*[@id='menu-item-2696']/a")
	private WebElement homeMenuBtn;

	@FindBy(xpath = "//*[@id='menu-item-2696']/ul/li[2]")
	private WebElement homeMenuLi;

	@FindBy(xpath = "//*[@id='paymentschedule']/table")
	private WebElement emiPaymentTable;

	@FindBy(xpath = "//*[@id='paymentschedule']/table/tbody/tr[not(starts-with(@id,'monthyear'))]")
	private List<WebElement> yearRows;

	@FindBy(xpath = "//*[@id='paymentschedule']//tr[starts-with(@id,'monthyear')]/td/div/table/tbody")
	private List<WebElement> monthBodies;

	@FindBy(className = "page-header")
	private WebElement pageHead;

	@FindBy(id = "homeprice")
	private WebElement homePrice;

	@FindBy(id = "downpayment")
	private WebElement downPayment;

	@FindBy(id = "homeloaninsuranceamount")
	private WebElement inAmount;

	@FindBy(id = "homeloaninterest")
	private WebElement loanInterest;

	@FindBy(id = "homeloanterm")
	private WebElement loanTerm;

	@FindBy(id = "loanfees")
	private WebElement loanFees;

	public HomeLoanEmiCalculatorPage(WebDriver driver, WebDriverWait wait) {
		this.driver = driver;
		this.wait = wait;
		PageFactory.initElements(driver, this);
	}

	public void navigateToHomeLoanCalculator() {
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

	public void fillValues(String price, String pay, String amount, String interest, String term, String fees) {
		clearAndType(homePrice, price);
		clearAndType(downPayment, pay);
		clearAndType(inAmount, amount);
		clearAndType(loanInterest, interest);
		clearAndType(loanTerm, term);
		clearAndType(loanFees, fees);
	}

	public void emiResultSave() throws InterruptedException {
		try {
			logger.info("Year rows count: {}", yearRows.size());

			for (int i = 1; i < yearRows.size(); i++) {
				yearRows.get(i).findElements(By.tagName("td")).get(0).click();
				Thread.sleep(500);
			}

			List<List<String>> records = new ArrayList<>();
			int year = 2025;

			for (WebElement tbody : monthBodies) {
				List<WebElement> rows = tbody.findElements(By.tagName("tr"));
				for (WebElement r : rows) {
					List<WebElement> tds = r.findElements(By.tagName("td"));
					if (tds == null || tds.isEmpty()) {
						continue; // skip header rows
					}

					List<String> rowData = new ArrayList<>();
					for (int i = 0; i < tds.size(); i++) {
						if (i == 0) {
							rowData.add(tds.get(i).getText().trim() + " - " + year);
						} else {
							rowData.add(tds.get(i).getText().trim());
						}
					}
					records.add(rowData);
				}
				year++;
			}

			// Prepare Excel writing
			String savePath = ConfigReader.getSaveExcelPath();
			String sheetName = ConfigReader.getSaveExcelSheetName();
			ExcelUtils excel = new ExcelUtils(savePath);
			excel.openOrCreate(sheetName);

			String[] headers = new String[] { "Month & Year", "Principal (A)", "Interest (B)",
					"Taxes, Home Insurance, HOA (C)", "Total Monthly Payment (A+B+C)", "Outstanding Principal (D)",
					"Loan Paid to Date" };
			excel.upsertHeaders(headers);
			// Clear old data rows (keep header) before appending fresh data
			excel.clearDataRowsPreserveHeader();
			excel.appendRows(records);
			excel.autoSizeColumns();
			excel.saveAndClose();
		} catch (IOException e) {
			throw new RuntimeException("Failed to write EMI results to Excel: " + e.getMessage(), e);
		}
	}

}