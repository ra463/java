package com.emi.pages;

import java.util.List;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CarEmiCalculatorPage {
	private static final Logger logger = LogManager.getLogger(CarEmiCalculatorPage.class);
	private WebDriver driver;
	private WebDriverWait wait;

	@FindBy(xpath = "//*[@id='emicalculatordashboard']/ul/li[3]")
	private WebElement tab;

	@FindBy(id = "loanamount")
	private WebElement loanAmountField;

	@FindBy(id = "loaninterest")
	private WebElement interestRateField;

	@FindBy(id = "loanterm")
	private WebElement loanTermField;

	@FindBy(className = "page-header")
	private WebElement pageHead;

	@FindBy(xpath = "//*[@id='emiamount']/p/span")
	private WebElement laonemi;

	@FindBy(id = "emipaymenttable")
	private WebElement emiTable;

	@FindBy(xpath = "//table/tbody/tr[2]")
	private WebElement rowFirst;

	@FindBy(xpath = "//*[@id='monthyear2025']/td/div/table/tbody")
	private WebElement subTable;

	// ====================== Locators ===========================
	By td = By.tagName("td");
	By tr = By.tagName("tr");

	public CarEmiCalculatorPage(WebDriver driver, WebDriverWait wait) {
		this.driver = driver;
		this.wait = wait;
		PageFactory.initElements(driver, this);
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

	public void calculateCarEmi(String loanAmount, String interestRate, String tenure) throws InterruptedException {
		tab.click();
		Thread.sleep(2000);

		clearAndType(loanAmountField, loanAmount);
		clearAndType(interestRateField, interestRate);
		clearAndType(loanTermField, tenure);

		pageHead.click();
	}

	public void getEmi() {
		((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(false);", laonemi);
		
		String emi = laonemi.getText();
		logger.info("{} is the loan emi of car", emi);
	}

	public void scrollToResults() {
		((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(false);", emiTable);
	}

	public void getTheResults() {
		wait.until(ExpectedConditions.visibilityOf(rowFirst));
		List<WebElement> cells = rowFirst.findElements(td);
		cells.get(0).click();

		List<WebElement> subTrList = subTable.findElements(tr);
		List<WebElement> subTdList = subTrList.get(0).findElements(td);

		String principle = subTdList.get(1).getText();
		String interest = subTdList.get(2).getText();

		logger.info("Principle: {} Interest: {}", principle, interest);
	}
}