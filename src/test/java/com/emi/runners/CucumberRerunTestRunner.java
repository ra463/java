package com.emi.runners;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;

@CucumberOptions(features = { "@target/rerun.txt" }, glue = { "com.emi.stepdefinitions", "com.emi.hooks" }, plugin = {
		"pretty", "summary", "io.qameta.allure.cucumber7jvm.AllureCucumber7Jvm" }, monochrome = true)
public class CucumberRerunTestRunner extends AbstractTestNGCucumberTests {}