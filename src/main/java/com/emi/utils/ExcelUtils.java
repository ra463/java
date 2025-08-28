package com.emi.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelUtils {
    
    private Workbook workbook;
    private Sheet sheet;
    private String filePath;
    
    public ExcelUtils(String filePath) {
        this.filePath = filePath;
    }
    
    public void openExcel(String sheetName) throws IOException {
        FileInputStream fis = new FileInputStream(filePath);
        workbook = new XSSFWorkbook(fis);
        sheet = workbook.getSheet(sheetName);
        if (sheet == null) {
            sheet = workbook.createSheet(sheetName);
        }
        fis.close();
    }
    
    public void createNewExcel(String sheetName) throws IOException {
        workbook = new XSSFWorkbook();
        sheet = workbook.createSheet(sheetName);
    }
    
    public void writeData(int rowNum, int colNum, String data) {
        Row row = sheet.getRow(rowNum);
        if (row == null) {
            row = sheet.createRow(rowNum);
        }
        Cell cell = row.getCell(colNum);
        if (cell == null) {
            cell = row.createCell(colNum);
        }
        cell.setCellValue(data);
    }
    
    public String readData(int rowNum, int colNum) {
        Row row = sheet.getRow(rowNum);
        if (row == null) return "";
        
        Cell cell = row.getCell(colNum);
        if (cell == null) return "";
        
        return getCellValueAsString(cell);
    }
    
    public void writeHeaders(String[] headers) {
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            
            // Style the header
            CellStyle headerStyle = workbook.createCellStyle();
            Font font = workbook.createFont();
            font.setBold(true);
            headerStyle.setFont(font);
            cell.setCellStyle(headerStyle);
        }
    }
    
    public void writeRowData(int rowNum, List<String> data) {
        Row row = sheet.createRow(rowNum);
        for (int i = 0; i < data.size(); i++) {
            Cell cell = row.createCell(i);
            cell.setCellValue(data.get(i));
        }
    }
    
    public List<List<String>> readAllData() {
        List<List<String>> allData = new ArrayList<>();
        
        for (Row row : sheet) {
            List<String> rowData = new ArrayList<>();
            for (Cell cell : row) {
                rowData.add(getCellValueAsString(cell));
            }
            allData.add(rowData);
        }
        
        return allData;
    }
    
    private String getCellValueAsString(Cell cell) {
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                } else {
                    return String.valueOf(cell.getNumericCellValue());
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            default:
                return "";
        }
    }
    
    public void saveAndClose() throws IOException {
        FileOutputStream fos = new FileOutputStream(filePath);
        workbook.write(fos);
        fos.close();
        workbook.close();
    }
    
    public int getRowCount() {
        return sheet.getPhysicalNumberOfRows();
    }
    
    public int getColumnCount(int rowNum) {
        Row row = sheet.getRow(rowNum);
        return row == null ? 0 : row.getLastCellNum();
    }
    
    public void autoSizeColumns() {
        if (sheet.getLastRowNum() > 0) {
            Row firstRow = sheet.getRow(0);
            for (int i = 0; i < firstRow.getLastCellNum(); i++) {
                sheet.autoSizeColumn(i);
            }
        }
    }

    // ================= Additional helpers for this task ==================
    
    /**
     * Ensures an Excel file exists at filePath, opening it if present or
     * creating a new workbook and sheet if missing.
     */
    public void openOrCreate(String sheetName) throws IOException {
        File file = new File(filePath);
        if (file.exists()) {
            openExcel(sheetName);
        } else {
            // Ensure parent directories exist
            File parent = file.getParentFile();
            if (parent != null && !parent.exists()) {
                parent.mkdirs();
            }
            createNewExcel(sheetName);
        }
    }

    /**
     * Appends rows to the end of the current sheet.
     */
    public void appendRows(List<List<String>> rows) {
        int start = sheet.getLastRowNum() + 1;
        // If the sheet is completely empty (no rows), start from row 1 to keep row 0 for headers
        if (sheet.getPhysicalNumberOfRows() == 0) {
            start = 1;
        }
        for (int i = 0; i < rows.size(); i++) {
            writeRowData(start + i, rows.get(i));
        }
    }

    /**
     * Insert or update headers on row 0 without shifting rows. Always writes headers to row 0.
     */
    public void upsertHeaders(String[] headers) {
        // Ensure row 0 exists and then overwrite with headers
        if (sheet.getRow(0) == null) {
            sheet.createRow(0);
        }
        writeHeaders(headers);
    }

    /**
     * Clears all data rows while preserving the header at row 0.
     */
    public void clearDataRowsPreserveHeader() {
        int last = sheet.getLastRowNum();
        if (last >= 1) {
            // Shift all rows [1..last] up by 'last' positions, effectively deleting them
            sheet.shiftRows(1, last, -last);
        }
    }
} 