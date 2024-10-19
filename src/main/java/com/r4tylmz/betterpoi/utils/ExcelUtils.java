package com.r4tylmz.betterpoi.utils;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.io.InputStream;

public class ExcelUtils {

    /**
     * Converts an Excel file from .xls format to .xlsx format.
     *
     * @param inputStream the input stream of the .xls file
     * @return the converted XSSFWorkbook object
     * @throws IOException if an I/O error occurs during conversion
     */
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
                                CellUtil.copyCell(oldCell, newCell);
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
}