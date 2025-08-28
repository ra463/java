@home @regression
Feature: Home Loan EMI calculator

  Background:
    Given I am on the Home Loan EMI Calculator page

  Scenario: Extract year-wise amortization data and export to Excel
    When I fill home loan values with by reading from file
    Then I export the year-wise amortization to Excel