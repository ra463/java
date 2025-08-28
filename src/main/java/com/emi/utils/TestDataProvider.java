package com.emi.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class TestDataProvider {
	private final String source;
	private final String excelPath;
	private final String excelSheet;
	private final String jsonPath;
	private final String xmlPath;

	private String sheetName = "";
	private boolean isBDDkeys = false;
	private List<List<String>> cachedRows;

	// JSON and XML key order to match column indices
	private static final List<String> JSON_KEYS_ORDER = Arrays.asList("loan_amount", "rate", "term", "fees",
			"emi_amount", "lamount_rate", "lamount_term", "lamount_fees", "ltenure_amount", "ltenure_emi",
			"ltenure_rate", "ltenure_fees", "car_amount", "car_rate", "car_term", "home_price", "home_downpayment",
			"home_insurance_amount", "home_interest", "home_term", "home_fees");

	private static final List<String> JSON_BDD_KEYS_ORDER = Arrays.asList("fn_03_amount", "fn_03_rate", "fn_03_term",
			"fn_03_fees", "fn_05_emi", "fn_05_rate", "fn_05_term", "fn_05_fees", "fn_07_amount", "fn_07_emi",
			"fn_07_rate", "fn_07_fees", "fl_01_amount", "fl_01_rate", "fl_01_term", "fl_01_fees", "fl_02_amount",
			"fl_02_rate", "fl_02_term", "fl_02_fees", "fl_03_amount", "fl_03_rate", "fl_03_term", "fl_03_fees",
			"fl_05_rate", "fl_07_amount", "fl_07_rate", "fl_07_term", "fl_07_fees", "fn_02_amount", "fn_02_rate",
			"fn_02_term", "fn_01_price", "fn_01_down", "fn_01_insurance", "fn_01_interest", "fn_01_term", "fn_01_fees");

	public TestDataProvider(boolean isBDDTest, String name) {
		this.source = ConfigReader.getProperty("test.data.source", "excel").toLowerCase();
		this.excelPath = ConfigReader.getProperty("test.data.file");
		this.excelSheet = ConfigReader.getProperty("test.data.sheet");
		this.jsonPath = ConfigReader.getProperty("test.data.file.json");
		this.xmlPath = ConfigReader.getProperty("test.data.file.xml");

		if (isBDDTest == true) {
			sheetName = name;
			isBDDkeys = true;
		} else {
			sheetName = excelSheet;
			isBDDkeys = false;
		}

		loadData();
	}

	public String getCellString(int row, int col) {
		if (cachedRows == null || row < 0 || row >= cachedRows.size())
			return "";
		List<String> dataRow = cachedRows.get(row);
		return (col >= 0 && col < dataRow.size()) ? dataRow.get(col) : "";
	}

	private void loadData() {
		switch (source) {
		case "xml":
			cachedRows = loadFromXml();
			break;
		case "json":
			cachedRows = loadFromJson();
			break;
		case "excel":
			cachedRows = loadFromExcel();
			break;
		default:
			cachedRows = loadFromExcel();
		}
	}

	private List<List<String>> loadFromExcel() {
		List<List<String>> rows = new ArrayList<>();
		try (FileInputStream fis = new FileInputStream(excelPath); Workbook wb = new XSSFWorkbook(fis)) {

			Sheet sheet = wb.getSheet(sheetName);
			if (sheet != null) {
				for (int r = 0; r <= sheet.getLastRowNum(); r++) {
					Row row = sheet.getRow(r);
					if (row != null) {
						List<String> rowData = new ArrayList<>();
						for (int c = 0; c < row.getLastCellNum(); c++) {
							rowData.add(getCellValue(row.getCell(c)));
						}
						rows.add(rowData);
					}
				}
			}
		} catch (IOException e) {
			throw new RuntimeException("Failed to read Excel: " + e.getMessage(), e);
		}
		return rows;
	}

	private List<List<String>> loadFromJson() {
		List<List<String>> rows = new ArrayList<>();
		try (FileReader fr = new FileReader(jsonPath)) {
			JsonElement root = JsonParser.parseReader(fr);
			JsonArray arr = root.isJsonArray() ? root.getAsJsonArray() : root.getAsJsonObject().getAsJsonArray("rows");
			for (JsonElement element : arr) {
				JsonObject obj = element.getAsJsonObject();
				List<String> rowData = new ArrayList<>();
				List<String> keyOrder = isBDDkeys ? JSON_BDD_KEYS_ORDER : JSON_KEYS_ORDER;
				for (String key : keyOrder) {
					JsonElement cell = obj.get(key);
					rowData.add(cell == null ? "" : cell.getAsString());
				}
				rows.add(rowData);
			}
		} catch (IOException e) {
			throw new RuntimeException("Failed to read JSON: " + e.getMessage(), e);
		}
		return rows;
	}

	private List<List<String>> loadFromXml() {
		List<List<String>> rows = new ArrayList<>();
		try {
			if (xmlPath == null || xmlPath.trim().isEmpty()) {
				throw new IOException(
						"XML path not configured. Set 'test.data.file.xml' in config.properties or via -Dtest.data.file.xml");
			}
			File file = new File(xmlPath);
			Document doc;
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setNamespaceAware(false);
			DocumentBuilder builder = factory.newDocumentBuilder();

			if (file.exists()) {
				doc = builder.parse(file);
			} else {
				ClassLoader cl = Thread.currentThread().getContextClassLoader();
				java.io.InputStream is = cl.getResourceAsStream(xmlPath);
				if (is == null) {
					throw new IOException("XML file not found at '" + xmlPath + "' or on classpath");
				}
				doc = builder.parse(is);
			}

			doc.getDocumentElement().normalize();

			NodeList rowNodes = doc.getElementsByTagName("row");
			for (int i = 0; i < rowNodes.getLength(); i++) {
				Element rowEl = (Element) rowNodes.item(i);
				List<String> rowData = new ArrayList<>();
				List<String> keyOrder = isBDDkeys ? JSON_BDD_KEYS_ORDER : JSON_KEYS_ORDER;
				for (String key : keyOrder) {
					String text = "";
					NodeList nodes = rowEl.getElementsByTagName(key);
					if (nodes.getLength() > 0) {
						text = nodes.item(0).getTextContent();
						if (text != null) {
							text = text.trim();
						} else {
							text = "";
						}
					}
					rowData.add(text);
				}
				rows.add(rowData);
			}
		} catch (Exception e) {
			throw new RuntimeException("Failed to read XML: " + e.getMessage(), e);
		}
		return rows;
	}

	private String getCellValue(Cell cell) {
		return cell == null ? "" : cell.toString().trim();
	}
}
