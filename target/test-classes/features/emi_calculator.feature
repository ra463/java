@loan @emi
Feature: EMI, Loan Amount, and Loan Tenure Calculator Validations

  Background:
    Given I am on the EMI Calculator page

  # ================= Functional Level Scenarios (TC-FN) =================

  @functional @emi_calculator @smoke @regression @tc_fn_03
  Scenario: TC-FN-03 Navigate to EMI Calculator tab and verify UI elements
    When I navigate to the EMI Calculator tab
    And I enter loan details in emi calculator
    Then all UI elements such as text boxes and controls should be visible and functional

  @functional @emi_calculator @smoke @regression @tc_fn_04
  Scenario: TC-FN-04 Change Loan Tenure from years to months and verify scale adjustment
    When I toggle the loan tenure type between years and months
    Then the tenure type should be updated accordingly

  @functional @loan_amount @regression @tc_fn_05
  Scenario: TC-FN-05 Navigate to Loan Amount Calculator tab and verify UI elements
    When I navigate to the Loan Amount Calculator tab
    And I enter EMI details in loan amount
    Then all UI elements such as text boxes and controls should be visible and functional

  @functional @loan_amount @regression @tc_fn_06
  Scenario: TC-FN-06 Change Loan Tenure from years to months and verify scale adjustment
    When I toggle the loan tenure type between years and months
    Then the tenure type should be updated accordingly

  @functional @loan_tenure @regression @tc_fn_07
  Scenario: TC-FN-07 Navigate to Loan Tenure Calculator tab and verify UI elements
    When I navigate to the Loan Tenure Calculator tab
    And I enter EMI details in loan tenure
    Then all UI elements such as text boxes and controls should be visible and functional

  # ================= Field Level Scenarios (TC-FL) =================
	
  @regression @tc_fl_01 @positive
  Scenario: TC-FL-01 Validate EMI text boxes with valid inputs
    When I enter valid text box values in emi calculator
    Then all EMI input fields should be valid and functional
    
  @field @negative @regression @tc_fl_02
  Scenario: TC-FL-02 Validate that all input fields reset on non-numeric input
    When I enter non-numeric values in emi calculator
    Then all fields should reject non-numeric input and reset to default values

  @field @negative @regression @tc_fl_03
  Scenario: TC-FL-03 Validate that all input fields reset on negative input
    When I enter negative values in emi calculator
    Then all fields should reset to the equivalent positive values or default

  @field @negative @regression @tc_fl_04
  Scenario: TC-FL-04 Validate that all input fields reset when left blank
    When I leave all input fields empty in emi calculator
    Then all fields should reset to default values (zero or placeholders)

  @field @positive @regression @tc_fl_05
  Scenario: TC-FL-05 Validate that decimal values are accepted in the Interest Rate field
    When I enter interest rate value in emi calculator
    Then the Interest Rate field should accept and retain the decimal value

  @field @tenure @regression @tc_fl_06
  Scenario: TC-FL-06 Validate that Loan Tenure supports both year and month formats
    When I toggle the loan tenure type between years and months
    Then the selected tenure type should update correctly with respect to year in month

  @field @dynamic @regression @tc_fl_07
  Scenario: TC-FL-07 Validate that EMI result updates dynamically on input change
    When I get the initial value of EMI result and i enter all values in emi calculator
    Then the EMI result should update dynamically as the input values change and differ from the previous EMI value
