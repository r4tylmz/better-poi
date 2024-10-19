package com.r4tylmz.betterpoi.utils;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Workbook;

/**
 * Utility class for working with cells in Excel workbooks.
 */
public class CellUtil {
    /**
     * Copies the content and style of a cell to a new cell.
     *
     * @param oldCell the cell to be copied
     * @param newCell the cell to copy the content and style to
     */
    public static void copyCell(Cell oldCell, Cell newCell) {
        Workbook newWorkbook = newCell.getSheet().getWorkbook();
        CellStyle newCellStyle = newWorkbook.createCellStyle();
        copyCellStyle(oldCell.getCellStyle(), newCellStyle);
        newCell.setCellStyle(newCellStyle);

        switch (oldCell.getCellType()) {
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
                newCell.setBlank();
                break;
            default:
                break;
        }
    }

    /**
     * Copies the style of a cell to a new cell style.
     *
     * @param oldStyle the cell style to be copied
     * @param newStyle the cell style to copy the properties to
     */
    public static void copyCellStyle(CellStyle oldStyle, CellStyle newStyle) {
        newStyle.setDataFormat(oldStyle.getDataFormat());
        newStyle.setWrapText(oldStyle.getWrapText());
    }

}
