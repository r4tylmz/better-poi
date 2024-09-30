package com.r4tylmz.betterpoi.utils;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.io.InputStream;

public class ExcelUtils {
    public static XSSFWorkbook convertXlsToXlsx(InputStream inputStream) throws IOException {
        try {
            Workbook xlsWorkbook = new HSSFWorkbook(inputStream);
            XSSFWorkbook xlsxWorkbook = new XSSFWorkbook();

            for (int i = 0; i < xlsWorkbook.getNumberOfSheets(); i++) {
                Sheet oldSheet = xlsWorkbook.getSheetAt(i);
                Sheet newSheet = xlsxWorkbook.createSheet(oldSheet.getSheetName());

                for (int j = 0; j <= oldSheet.getLastRowNum(); j++) {
                    Row oldRow = oldSheet.getRow(j);
                    Row newRow = newSheet.createRow(j);

                    if (oldRow != null) {
                        for (int k = 0; k < oldRow.getLastCellNum(); k++) {
                            Cell oldCell = oldRow.getCell(k);
                            if (oldCell != null) {
                                Cell newCell = newRow.createCell(k);
                                copyCell(oldCell, newCell);
                            }
                        }
                    }
                }
            }

            return xlsxWorkbook;
        } catch (IOException e) {
            throw new IOException("Error converting .xls to .xlsx", e);
        }
    }

    private static void copyCell(Cell oldCell, Cell newCell) {
        Workbook newWorkbook = newCell.getSheet().getWorkbook();
        CellStyle newCellStyle = newWorkbook.createCellStyle();
        copyCellStyle(oldCell.getCellStyle(), newCellStyle);
        newCell.setCellStyle(newCellStyle);

        switch (oldCell.getCellTypeEnum()) {
            case STRING:
                newCell.setCellValue(oldCell.getStringCellValue());
                break;
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(oldCell)) {
                    newCell.setCellValue(oldCell.getDateCellValue());
                } else {
                    newCell.setCellValue(oldCell.getNumericCellValue());
                }
                break;
            case BOOLEAN:
                newCell.setCellValue(oldCell.getBooleanCellValue());
                break;
            case FORMULA:
                newCell.setCellFormula(oldCell.getCellFormula());
                break;
            case BLANK:
                newCell.setCellType(CellType.BLANK);
                break;
            default:
                break;
        }
    }

    private static void copyCellStyle(CellStyle oldStyle, CellStyle newStyle) {
        newStyle.setDataFormat(oldStyle.getDataFormat());
        newStyle.setWrapText(oldStyle.getWrapText());
    }
}
