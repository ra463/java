package com.emi.stepdefinitions;

import com.emi.base.DriverSetup;
import com.emi.pages.CarEmiCalculatorPage;
import com.emi.utils.ConfigReader;
import com.emi.utils.TestDataProvider;

import io.cucumber.java.en.*;

public class CarEmiSteps {
	private CarEmiCalculatorPage carEmiPage;
	private String fn_02_amount;
	private String fn_02_rate;
	private String fn_02_term;

	public CarEmiSteps() {
		TestDataProvider data = new TestDataProvider(true, "CarEmi");
		String source = ConfigReader.getProperty("test.data.source");
		int rowIndex = (source != null && (source.equalsIgnoreCase("json") || source.equalsIgnoreCase("xml"))) ? 0 : 1;
		int colIndex = (source != null && (source.equalsIgnoreCase("json") || source.equalsIgnoreCase("xml"))) ? 29 : 0;

		fn_02_amount = data.getCellString(rowIndex, 0 + colIndex);
		fn_02_rate = data.getCellString(rowIndex, 1 + colIndex);
		fn_02_term = data.getCellString(rowIndex, 2 + colIndex);
	}

	@Given("I launch the Car EMI Calculator application")
	public void i_launch_car_emi_app() {
		carEmiPage = new CarEmiCalculatorPage(DriverSetup.getDriver(), DriverSetup.getWait());
	}

	@When("I calculate Car EMI by reading date from file")
	public void i_calculate_car_emi() throws InterruptedException {
		carEmiPage.calculateCarEmi(fn_02_amount, fn_02_rate, fn_02_term);
	}

	@Then("I should see the EMI amount displayed")
	public void i_get_emi_result() {
		carEmiPage.getEmi();
	}

	@Then("I should see the first month principal and interest breakdown")
	public void i_should_see_the_first_month_principal_and_interest_breakdown() {
		carEmiPage.scrollToResults();
		carEmiPage.getTheResults();
	}
}