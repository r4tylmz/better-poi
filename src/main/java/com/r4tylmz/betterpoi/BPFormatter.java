package com.r4tylmz.betterpoi;

import com.r4tylmz.betterpoi.annotation.BPColumn;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.Color;
import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.Date;

public class BPFormatter {

    public static final XSSFColor RED = new XSSFColor(Color.red);
    public static final short RED_INDEX = HSSFColor.RED.index;
    private static final Logger logger = LoggerFactory.getLogger(BPFormatter.class);
    /**
     * default cell formating for data
     */
    private final DataFormat dataFormat;
    /**
     * default cell formating
     */
    private final CellStyle defaultCellStyle;
    /**
     * error cell formating
     */
    private final CellStyle errorStyle;
    private final Workbook workbook;

    public BPFormatter(Workbook workbook) {
        this.workbook = workbook;
        defaultCellStyle = workbook.createCellStyle();
        dataFormat = workbook.createDataFormat();
        errorStyle = initErrorStyle();
    }

    public static XSSFColor toColor(String hex) {
        int red = Integer.valueOf(hex.substring(0, 2), 16);
        int green = Integer.valueOf(hex.substring(2, 4), 16);
        int blue = Integer.valueOf(hex.substring(4, 6), 16);
        return new XSSFColor(new Color(red, green, blue));
    }

    private void addCommentCell(String message, final int commentIndex, final Row row) {
        final Cell commentCell = row.getCell(commentIndex);
        if (commentCell == null) {
            // create the cell
            final Cell comment = row.createCell(commentIndex);
            comment.setCellStyle(errorStyle);
            comment.setCellValue(message);
        } else {
            // append value to existing
            String newValue = String.format("%s\n%s", commentCell.getStringCellValue(), message);
            commentCell.setCellValue(newValue);
        }
    }

    /**
     * Set errorStyle to cell + add error message at the end of the row. <br/>
     * Use in case of cell validation error.
     */
    public void addErrorMessage(Cell cell, String message) {
        final Row row = cell.getRow();
        final int commentIndex = getErrorCommentIndex(row);
        cell.setCellStyle(errorStyle);
        // add a 'comment' cell to end of row for each cell in error
        addCommentCell(message, commentIndex, row);
    }

    /**
     * Add error message at the end of the row. <br/>
     * Use in case of row validation error.
     */
    public void addErrorMessage(Row row, String message) {
        final int commentIndex = getErrorCommentIndex(row);
        if (commentIndex < 0) {
            final String msg = "Unable to add error comment to current line. The current row {} of sheet {} doesn't seems to have any cell (lastCellNum {}).";
            logger.error(msg, row.getRowNum(), row.getSheet().getSheetName(), commentIndex);
            return;
        }
        addCommentCell(message, commentIndex, row);
    }

    /**
     * Resize column based on content.
     */
    public void autoSizing(SXSSFSheet sheet, int length) {
        sheet.trackAllColumnsForAutoSizing();
        for (int k = 0; k < length; k++) {
            sheet.autoSizeColumn(k);
        }
    }

    public void formatCell(Field field, BPColumn bpColumn, Cell cell, Object value) {
        if (value != null) {
            if (field.getType().isAssignableFrom(Boolean.class)) {
                cell.setCellValue(value.toString());
                cell.setCellType(CellType.BOOLEAN);
            } else if (isNumeric(field)) {
                cell.setCellValue(Double.parseDouble(value.toString()));
                cell.setCellType(CellType.NUMERIC);
            } else if (isDate(field)) {
                if (field.getType().isAssignableFrom(Date.class)) {
                    cell.setCellValue((Date) value);
                } else {
                    cell.setCellValue(value.toString());
                }
            } else {
                cell.setCellValue(value.toString());
                cell.setCellType(CellType.STRING);
            }

            // set cell style
            if (isDate(field)) {
                final CellStyle dateCellStyle = workbook.createCellStyle();
                dateCellStyle.setDataFormat(dataFormat.getFormat(bpColumn.datePattern()));
                cell.setCellStyle(dateCellStyle);
            } else {
                cell.setCellStyle(defaultCellStyle);
            }
        }
    }

    public void formatHeader(final BPColumn bpColumn, final Cell cell) {
    }

    public DataFormat getDataFormat() {
        return dataFormat;
    }

    public CellStyle getDefaultCellStyle() {
        return defaultCellStyle;
    }

    /**
     * @return headers last column + 1
     */
    private int getErrorCommentIndex(Row row) {
        final Row headers = row.getSheet().getRow(0);
        final int commentIndex = headers.getLastCellNum(); // see javadoc
        // returns PLUS ONE
        return commentIndex;
    }

    /**
     * Create the style for further usage.
     */
    private CellStyle initErrorStyle() {
        final CellStyle style = workbook.createCellStyle();
        // add foreground red
        style.setFillForegroundColor(RED_INDEX);
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setFillBackgroundColor(RED_INDEX);
        // add font white
        final Font font = workbook.createFont();
        font.setColor(IndexedColors.WHITE.getIndex());
        style.setFont(font);
        return style;
    }

    private boolean isDate(Field field) {
        return field.getType().isAssignableFrom(Date.class) || field.getType().isAssignableFrom(LocalDate.class);
    }

    private boolean isNumeric(Field field) {
        return field.getType().isAssignableFrom(Double.class) || field.getType().isAssignableFrom(Integer.class)
                || field.getType().isAssignableFrom(Long.class);
    }

}
