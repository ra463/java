package com.emi.stepdefinitions;

import org.testng.Assert;

import com.emi.base.DriverSetup;
import com.emi.pages.LoanCalculatorPage;
import com.emi.utils.ConfigReader;
import com.emi.utils.TestDataProvider;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class LoanCalculatorSteps {
	private LoanCalculatorPage loanCalculatorPage;

	private String fn_03_amount;
	private String fn_03_rate;
	private String fn_03_term;
	private String fn_03_fees;

	private String fn_05_emi;
	private String fn_05_rate;
	private String fn_05_term;
	private String fn_05_fees;

	private String fn_07_amount;
	private String fn_07_emi;
	private String fn_07_rate;
	private String fn_07_fees;

	private String fl_01_amount;
	private String fl_01_rate;
	private String fl_01_term;
	private String fl_01_fees;

	private String fl_02_amount;
	private String fl_02_rate;
	private String fl_02_term;
	private String fl_02_fees;

	private String fl_03_amount;
	private String fl_03_rate;
	private String fl_03_term;
	private String fl_03_fees;

	private String fl_05_rate;

	private String fl_07_amount;
	private String fl_07_rate;
	private String fl_07_term;
	private String fl_07_fees;

	public LoanCalculatorSteps() {
		TestDataProvider data = new TestDataProvider(true, "GeneralEMI");
		String source = ConfigReader.getProperty("test.data.source");
		int rowIndex = (source != null && (source.equalsIgnoreCase("json") || source.equalsIgnoreCase("xml"))) ? 0 : 1;

		fn_03_amount = data.getCellString(rowIndex, 0);
		fn_03_rate = data.getCellString(rowIndex, 1);
		fn_03_term = data.getCellString(rowIndex, 2);
		fn_03_fees = data.getCellString(rowIndex, 3);

		fn_05_emi = data.getCellString(rowIndex, 4);
		fn_05_rate = data.getCellString(rowIndex, 5);
		fn_05_term = data.getCellString(rowIndex, 6);
		fn_05_fees = data.getCellString(rowIndex, 7);

		fn_07_amount = data.getCellString(rowIndex, 8);
		fn_07_emi = data.getCellString(rowIndex, 9);
		fn_07_rate = data.getCellString(rowIndex, 10);
		fn_07_fees = data.getCellString(rowIndex, 11);

		fl_01_amount = data.getCellString(rowIndex, 12);
		fl_01_rate = data.getCellString(rowIndex, 13);
		fl_01_term = data.getCellString(rowIndex, 14);
		fl_01_fees = data.getCellString(rowIndex, 15);

		fl_02_amount = data.getCellString(rowIndex, 16);
		fl_02_rate = data.getCellString(rowIndex, 17);
		fl_02_term = data.getCellString(rowIndex, 18);
		fl_02_fees = data.getCellString(rowIndex, 19);

		fl_03_amount = data.getCellString(rowIndex, 20);
		fl_03_rate = data.getCellString(rowIndex, 21);
		fl_03_term = data.getCellString(rowIndex, 22);
		fl_03_fees = data.getCellString(rowIndex, 23);

		fl_05_rate = data.getCellString(rowIndex, 24);

		fl_07_amount = data.getCellString(rowIndex, 25);
		fl_07_rate = data.getCellString(rowIndex, 26);
		fl_07_term = data.getCellString(rowIndex, 27);
		fl_07_fees = data.getCellString(rowIndex, 28);
	}

	// ========== Background ==========
	@Given("I am on the EMI Calculator page")
	public void i_am_on_the_emi_calculator_page() {
		loanCalculatorPage = new LoanCalculatorPage(DriverSetup.getDriver(), DriverSetup.getWait());
		loanCalculatorPage.navigateToLoanCalculator();
	}

	// ========== Functional Scenarios ==========

	@When("I navigate to the EMI Calculator tab")
	public void i_navigate_to_the_emi_calculator_tab() {
		loanCalculatorPage.navigateToLoanCalculator();
	}

	@When("I enter loan details in emi calculator")
	public void i_enter_loan_details() {
		loanCalculatorPage.validateTextBoxes(fn_03_amount, fn_03_rate, fn_03_term, fn_03_fees);
//		Assert.assertTrue(result, "EMI input field validation failed");
	}

	@Then("all UI elements such as text boxes and controls should be visible and functional")
	public void all_ui_elements_visible_and_functional() {
		Assert.assertTrue(loanCalculatorPage.verifyUiElementsPresentAndFunctional(),
				"UI elements are missing or not functional");
	}

	@When("I toggle the loan tenure type between years and months")
	public void i_toggle_the_loan_tenure_type_between_years_and_months() {
		boolean result = loanCalculatorPage.validateTenureTypeChange();
		Assert.assertTrue(result, "Loan tenure toggle failed");
	}

	@Then("the tenure type should be updated accordingly")
	public void the_tenure_type_should_be_updated_accordingly() {
		boolean result = loanCalculatorPage.tenureTypeChanged();
		Assert.assertTrue(result, "Tenure type change validation failed");
	}

	@When("I navigate to the Loan Amount Calculator tab")
	public void i_navigate_to_the_loan_amount_calculator_tab() {
		loanCalculatorPage.clickLoanAmountCalculatorBtn();
	}

	@When("I enter EMI details in loan amount")
	public void i_enter_emi_details() {
		boolean result = loanCalculatorPage.validateLoanAmountTextBoxes(fn_05_emi, fn_05_rate, fn_05_term, fn_05_fees);
		Assert.assertTrue(result, "Loan Amount input validation failed");
	}

	@When("I navigate to the Loan Tenure Calculator tab")
	public void i_navigate_to_the_loan_tenure_calculator_tab() {
		loanCalculatorPage.clickLoanTenureCalculatorBtn();
	}

	@When("I enter EMI details in loan tenure")
	public void i_enter_loan_tenure_calculator_details() {
		boolean result = loanCalculatorPage.validateLoanTenureTextBoxes(fn_07_amount, fn_07_emi, fn_07_rate,
				fn_07_fees);
		Assert.assertTrue(result, "Loan Tenure input validation failed");
	}

	// ========== Field-Level Scenarios ==========

	@When("I enter valid text box values in emi calculator")
	public void i_enter_valid_text_values() {
		boolean result = loanCalculatorPage.validateTextBoxes(fl_01_amount, fl_01_rate, fl_01_term, fl_01_fees);
		Assert.assertTrue(result, "all emi text box fields are valid");
	}

	@Then("all EMI input fields should be valid and functional")
	public void all_emi_input_fields_valid() {
		Assert.assertTrue(loanCalculatorPage.verifyUiElementsPresentAndFunctional(),
				"EMI input fields are not valid or functional");
	}

	@When("I enter non-numeric values in emi calculator")
	public void i_enter_non_numeric_values() {
		boolean result = loanCalculatorPage.validateNonNumericInputs(fl_02_amount, fl_02_rate, fl_02_term, fl_02_fees);
		Assert.assertTrue(result, "Non-numeric input was not rejected properly");
	}

	@Then("all fields should reject non-numeric input and reset to default values")
	public void fields_should_reject_non_numeric() {
		// Already validated in When step
	}

	@When("I enter negative values in emi calculator")
	public void i_enter_negative_values() {
		boolean result = loanCalculatorPage.validateNegativeInputs(fl_03_amount, fl_03_rate, fl_03_term, fl_03_fees);
		Assert.assertTrue(result, "Negative values were not reset correctly");
	}

	@Then("all fields should reset to the equivalent positive values or default")
	public void fields_should_reset_to_positive_or_default() {
		// Already validated in When step
	}

	@When("I leave all input fields empty in emi calculator")
	public void i_leave_all_input_fields_empty() {
		boolean result = loanCalculatorPage.validateBlankInputs("", "", "", "");
		Assert.assertTrue(result, "Empty fields did not reset to default");
	}

	@Then("all fields should reset to default values \\(zero or placeholders)")
	public void fields_should_reset_to_default_values() {
		// Already validated
	}

	@When("I enter interest rate value in emi calculator")
	public void i_enter_decimal_in_interest_field() {
		Assert.assertTrue(loanCalculatorPage.interestRateAcceptsDecimal(fl_05_rate),
				"Interest Rate did not accept decimal value");
	}

	@Then("the Interest Rate field should accept and retain the decimal value")
	public void interest_rate_field_should_accept_decimal() {
		// Already asserted in When step
	}

	@Then("the selected tenure type should update correctly with respect to year in month")
	public void selected_tenure_type_should_update_correctly() {
		boolean result = loanCalculatorPage.getMonthAndYearValue();
		Assert.assertTrue(result, "Tenure type was not updated correctly");
	}

	@When("I get the initial value of EMI result and i enter all values in emi calculator")
	public void i_get_initial_emi_value_and_enter_loan_details() {
		boolean isNotEqual = false;
		String prevEmiVal = loanCalculatorPage.getEmiValue();
		loanCalculatorPage.validateTextBoxes(fl_07_amount, fl_07_rate, fl_07_term, fl_07_fees);

		String finalEmiVal = loanCalculatorPage.getEmiValue();

		if (!prevEmiVal.equals(finalEmiVal)) {
			isNotEqual = true;
		}
		Assert.assertTrue(isNotEqual, "EMI value does not changes dynamically");
	}

	@Then("the EMI result should update dynamically as the input values change and differ from the previous EMI value")
	public void emi_result_should_update_on_input_change() {
		// Already done is previous step
	}
}
