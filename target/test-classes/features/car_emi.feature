@car @smoke @regression
Feature: Car EMI calculation
    
  Scenario: Calculate Car EMI for 1 year loan and verify first month breakdown
    Given I launch the Car EMI Calculator application
    When I calculate Car EMI by reading date from file
    Then I should see the EMI amount displayed
    And I should see the first month principal and interest breakdown
  