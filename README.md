### EMI Calculator Automation Suite

## Overview
Automated UI tests for the EMI calculators on `https://emicalculator.net` using Selenium 4, Cucumber 7, and TestNG, with Allure reporting. The suite supports parallel execution across browsers via TestNG.

- Validates EMI, Loan Amount, and Loan Tenure calculators
- Home Loan amortization export to Excel
- Parallel runs (Chrome + Edge) from `testng.xml`
- Allure HTML reports with screenshots and environment info

## Tech Stack
- Java 21
- Maven (Surefire + Compiler + Allure Maven)
- Selenium WebDriver 4.35.0, WebDriverManager
- Cucumber 7 + TestNG 7.11
- Apache POI for Excel, Gson for JSON, XML support (DOM)
- Log4j2

## Project Structure (key files)
```
src
├─ main
│  ├─ java/com/emi
│  │  ├─ base/DriverSetup.java              # ThreadLocal WebDriver (parallel-safe)
│  │  ├─ pages/                             # Page Objects
│  │  │  ├─ LoanCalculatorPage.java
│  │  │  ├─ CarEmiCalculatorPage.java
│  │  │  └─ HomeLoanEmiCalculatorPage.java
│  │  └─ utils/                             # Utilities
│  │     ├─ ConfigReader.java               # Config + sysprop overrides
│  │     ├─ ExcelUtils.java                 # Excel read/write
│  │     ├─ TestDataProvider.java           # Excel/CSV/JSON/XML data loader
│  │     └─ ScreenshotUtils.java            # Local screenshot persistence
│  └─ resources/
│     ├─ config.properties                  # Project configuration
│     └─ log4j2.xml
└─ test
   ├─ java/com/emi
   │  ├─ hooks/CucumberHooks.java           # Screenshots + Allure env + cleanup
   │  ├─ runners/CucumberTestRunner.java    # Main runner
   │  └─ runners/CucumberRerunTestRunner.java
   └─ resources/
      ├─ features/
      │  ├─ emi_calculator.feature
      │  ├─ home_loan.feature
      │  └─ car_emi.feature
      ├─ allure.properties                  # Allure output directories
      └─ testdata/
         ├─ test_data.xlsx
         ├─ test_data_csv.csv
         ├─ test_data_json.json
         └─ test_data.xml
```

## Parallel Execution (default)
- `testng.xml` runs two tests in parallel (`parallel="tests"`), one for Chrome and one for Edge:
```xml
<suite name="Cucumber Parallel Browser Suite" parallel="tests" thread-count="2">
  <test name="Run Cucumber - Chrome">
    <parameter name="browser" value="chrome" />
    <classes>
      <class name="com.emi.runners.CucumberTestRunner" />
    </classes>
  </test>
  <test name="Run Cucumber - Edge">
    <parameter name="browser" value="edge" />
    <classes>
      <class name="com.emi.runners.CucumberTestRunner" />
    </classes>
  </test>
</suite>
```
- `DriverSetup` uses ThreadLocal `WebDriver` and `WebDriverWait`, so sessions are isolated per thread.

Note: The framework reads the browser from the `browser` system property or `config.properties`. If you rely on `<parameter name="browser" .../>` in TestNG, ensure you map it to a system property in a suite hook (e.g., set `System.setProperty("browser", param)`), or set `-Dbrowser` at runtime.

## How to Run
### CLI (Maven) – default parallel suite
```powershell
cd C:\Users\<you>\eclipse-workspace\Emi
mvn clean test
```
- Surefire uses `testng.xml`. Two browser tests run in parallel by default.

### Run specific tag(s)
```powershell
mvn -Dcucumber.filter.tags="@smoke" clean test
mvn -Dcucumber.filter.tags="@regression and not @negative" clean test
```

### Override browser/URL at runtime
```powershell
# single-browser override for both tests if you’re not using TestNG param mapping
mvn -Dbrowser=chrome -Durl=https://emicalculator.net/ clean test
```

## Data Sources (Excel/CSV/JSON/XML)
Configure in `src/main/resources/config.properties`:
```
# which source to use: excel | csv | json | xml
test.data.source=excel

# file locations
test.data.file=src/test/resources/testdata/test_data.xlsx
test.data.file.csv=src/test/resources/testdata/test_data_csv.csv
test.data.file.json=src/test/resources/testdata/test_data_json.json
test.data.file.xml=src/test/resources/testdata/test_data.xml

test.data.sheet=Sheet1
```
- Schema is unified across sources via column/key order in `TestDataProvider`:
  - loan_amount, rate, term, fees, emi_amount, lamount_rate, lamount_term, lamount_fees, ltenure_amount, ltenure_emi, ltenure_rate, ltenure_fees, car_amount, car_rate, car_term, home_price, home_downpayment, home_insurance_amount, home_interest, home_term, home_fees
- JSON: either an array `[...]` or `{ "rows": [...] }` with those keys.
- XML structure:
```xml
<data>
  <row>
    <loan_amount>2000500</loan_amount>
    <rate>9.5</rate>
    <term>6</term>
    <fees>10500</fees>
    <emi_amount>50000.00</emi_amount>
    <lamount_rate>9.7</lamount_rate>
    <lamount_term>7</lamount_term>
    <lamount_fees>11500</lamount_fees>
    <ltenure_amount>2500000</ltenure_amount>
    <ltenure_emi>53456.43</ltenure_emi>
    <ltenure_rate>9.63</ltenure_rate>
    <ltenure_fees>11300</ltenure_fees>
    <car_amount>1500000</car_amount>
    <car_rate>9.5</car_rate>
    <car_term>1</car_term>
    <home_price>6000000</home_price>
    <home_downpayment>22</home_downpayment>
    <home_insurance_amount>0</home_insurance_amount>
    <home_interest>8</home_interest>
    <home_term>18</home_term>
    <home_fees>0.47</home_fees>
  </row>
</data>
```
- Row indexing: `MainApp` uses row 0 for JSON and XML, row 1 for Excel/CSV (to skip headers).
- You can override the source at runtime:
```powershell
mvn -Dtest.data.source=xml -Dtest.data.file.xml=src/test/resources/testdata/test_data.xml clean test
```

## Allure Reporting
Configured in `src/test/resources/allure.properties`:
```
allure.results.directory=target/allure-results
allure.report.directory=target/allure-report
```
Maven config (Surefire) forwards `allure.results.directory`. The suite also cleans previous Allure results before execution.

### Quick preview
```powershell
allure serve target\allure-results
```

### Static (shareable)
```powershell
allure generate target\allure-results -o target\allure-report --clean
allure open target\allure-report
```

### Screenshots & Environment
- Screenshots:
  - On failure (always)
  - Per step if `allure.screenshot.every.step=true` in `config.properties`
- Environment is written to `environment.properties` in `target/allure-results` at runtime (includes `environment`, `browser`, `baseUrl`).

## Configuration (config.properties)
```
browser=edge
url=https://emicalculator.net/
# timeouts
implicit.wait / explicit.wait / page.load.timeout (via ConfigReader getters)

# data source
test.data.source=excel
# files
... see Data Sources section ...

# screenshots
screenshot.path=screenshots/
allure.screenshot.every.step=false

# environment label
environment=local

# Selenium Grid (optional)
grid.enabled=false
grid.url=http://localhost:4444/wd/hub
```
Override any property via `-D<key>=<value>` at runtime.

## Rerun of Failures
- Main runner writes failures to `target/rerun.txt` (`rerun:` plugin).
- `CucumberRerunTestRunner` consumes `@target/rerun.txt` as a second TestNG test (configured in `testng.xml` if desired or run separately).

## Troubleshooting
- Allure shows “unknown / 0 test cases”
  - Serve the correct folder from the project root: `allure serve target\allure-results`
  - Ensure results exist: `dir target\allure-results\*.json`
- `target\allure-results does not exist`
  - Run tests first: `mvn clean test`
- SSLHandshakeException (PKIX) at test end
  - Cucumber publish is disabled in POM and runners; ensure network policies don’t block
- `allure` not recognized
  - Add Allure `bin` to PATH and reopen terminal; verify `allure --version`
- Parallel issues
  - Ensure each thread gets its own `WebDriver` (provided by ThreadLocal). Avoid storing driver in static non-ThreadLocal fields elsewhere.
- XML data reads as 0/blank
  - Ensure `test.data.source=xml`; confirm XML tags match the key names above; row index for XML is 0.

## Useful Commands
```powershell
# Default parallel run
mvn clean test

# Run with tags
mvn -Dcucumber.filter.tags="@smoke" clean test

# Override browser/URL
mvn -Dbrowser=chrome -Durl=https://emicalculator.net/ clean test

# Allure quick preview
allure serve target\allure-results

# Generate + open static report
allure generate target\allure-results -o target\allure-report --clean
allure open target\allure-report

# Clean outputs
rmdir /s /q target\allure-results
rmdir /s /q target\allure-report
```

## Notes
- Selenium DevTools version warnings: keep Selenium up to date to match your browser’s CDP version.
- Compiler plugin currently targets Java 8 bytecode; if you want to use Java 21 language features, update the Maven Compiler Plugin `source`/`target` values accordingly across your environment/toolchain. 