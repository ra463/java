package com.emi.stepdefinitions;

import com.emi.base.DriverSetup;
import com.emi.pages.HomeLoanEmiCalculatorPage;
import com.emi.utils.ConfigReader;
import com.emi.utils.TestDataProvider;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class HomeLoanSteps {
	private HomeLoanEmiCalculatorPage homeLoanPage;
	private String fn_01_price;
	private String fn_01_down;
	private String fn_01_insurance;
	private String fn_01_interest;
	private String fn_01_term;
	private String fn_01_fees;

	public HomeLoanSteps() {
		TestDataProvider data = new TestDataProvider(true, "HomeLoan");
		String source = ConfigReader.getProperty("test.data.source");
		int rowIndex = (source != null && (source.equalsIgnoreCase("json") || source.equalsIgnoreCase("xml"))) ? 0 : 1;
		int colIndex = (source != null && (source.equalsIgnoreCase("json") || source.equalsIgnoreCase("xml"))) ? 32 : 0;

		fn_01_price = data.getCellString(rowIndex, 0 + colIndex);
		fn_01_down = data.getCellString(rowIndex, 1 + colIndex);
		fn_01_insurance = data.getCellString(rowIndex, 2 + colIndex);
		fn_01_interest = data.getCellString(rowIndex, 3 + colIndex);
		fn_01_term = data.getCellString(rowIndex, 4 + colIndex);
		fn_01_fees = data.getCellString(rowIndex, 5 + colIndex);
	}

	@Given("I am on the Home Loan EMI Calculator page")
	public void i_am_on_the_home_loan_emi_calculator_page() {
		homeLoanPage = new HomeLoanEmiCalculatorPage(DriverSetup.getDriver(), DriverSetup.getWait());
		homeLoanPage.navigateToHomeLoanCalculator();
	}

	@When("I fill home loan values with by reading from file")
	public void i_fill_home_loan_values() {
		homeLoanPage.fillValues(fn_01_price, fn_01_down, fn_01_insurance, fn_01_interest, fn_01_term, fn_01_fees);
	}

	@Then("I export the year-wise amortization to Excel")
	public void i_export_the_year_wise_amortization_to_excel() throws InterruptedException {
//		homeLoanPage.emiResultSave();
	}
}