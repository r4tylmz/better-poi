package io.github.r4tylmz.betterpoi;

import io.github.r4tylmz.betterpoi.annotation.BPColumn;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.Color;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * BPFormatter is responsible for formatting cells in an Excel workbook.
 * It uses Apache POI to create and format the cells.
 */
public class BPFormatter {
    private static final Logger logger = LoggerFactory.getLogger(BPFormatter.class);
    private final DataFormat dataFormat;
    private final CellStyle defaultCellStyle;
    private final CellStyle errorStyle;
    private final Workbook workbook;

    /**
     * Constructor for BPFormatter.
     *
     * @param workbook the workbook to format
     */
    public BPFormatter(Workbook workbook) {
        this.workbook = workbook;
        defaultCellStyle = workbook.createCellStyle();
        dataFormat = workbook.createDataFormat();
        errorStyle = createErrorCellStyle();
    }

    /**
     * Converts a hex color string to an XSSFColor.
     *
     * @param colorStr the hex color string to convert
     * @return the XSSFColor object representing the hex color
     */
    public static XSSFColor hex2Color(String colorStr) {
        Color color = Color.decode(colorStr);
        return new XSSFColor(new Color(color.getRed(), color.getGreen(), color.getBlue()));
    }

    /**
     * Adds a comment to a cell with an error message.
     *
     * @param message      the error message to add
     * @param commentIndex the index of the comment cell
     * @param row          the row to add the comment to
     */
    private void addCommentCell(String message, final int commentIndex, final Row row) {
        final Cell commentCell = row.getCell(commentIndex);
        if (commentCell == null) {
            final Cell comment = row.createCell(commentIndex);
            comment.setCellStyle(errorStyle);
            comment.setCellValue(message);
        } else {
            String newValue = String.format("%s\n%s", commentCell.getStringCellValue(), message);
            commentCell.setCellValue(newValue);
        }
    }

    /**
     * Adds an error message to a cell.
     *
     * @param cell the cell to add the error message to
     * @param message the error message to add
     */
    public void addErrorMessage(Cell cell, String message) {
        final Row row = cell.getRow();
        final int commentIndex = getErrorCommentIndex(row);
        cell.setCellStyle(errorStyle);
        // add a 'comment' cell to end of row for each cell in error
        addCommentCell(message, commentIndex, row);
    }

    /**
     * Adds an error message to a row.
     *
     * @param row the row to add the error message to
     * @param message the error message to add
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
     * Creates a cell style for error messages.
     *
     * @return the created cell style
     */
    private CellStyle createErrorCellStyle() {
        final CellStyle style = workbook.createCellStyle();
        style.setFillForegroundColor(IndexedColors.RED.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setFillBackgroundColor(IndexedColors.RED.getIndex());
        final Font font = workbook.createFont();
        font.setColor(IndexedColors.WHITE.getIndex());
        style.setFont(font);
        return style;
    }

    /**
     * Formats a cell based on the field type.
     *
     * @param field the field to format
     * @param bpColumn the BPColumn annotation containing metadata for the cell
     * @param cell the cell to format
     * @param value the value to set in the cell
     */
    public void formatCell(Field field, BPColumn bpColumn, Cell cell, Object value) {
        if (value != null) {
            if (field.getType().isAssignableFrom(Boolean.class)) {
                boolean boolValue = (boolean) value;
                cell.setCellValue(boolValue);
            } else if (isNumeric(field)) {
                cell.setCellValue(Double.parseDouble(value.toString()));
            } else if (isDate(field)) {
                if (field.getType().isAssignableFrom(Date.class)) {
                    cell.setCellValue((Date) value);
                } else if (field.getType().isAssignableFrom(LocalDate.class)) {
                    cell.setCellValue((LocalDate) value);
                } else if (field.getType().isAssignableFrom(java.sql.Date.class)) {
                    cell.setCellValue((java.sql.Date) value);
                } else if (field.getType().isAssignableFrom(LocalDateTime.class)) {
                    cell.setCellValue((LocalDateTime) value);
                } else {
                    cell.setCellValue(value.toString());
                }
            } else {
                cell.setCellValue(value.toString());
            }

            if (isDate(field)) {
                final CellStyle dateCellStyle = workbook.createCellStyle();
                dateCellStyle.setDataFormat(dataFormat.getFormat(bpColumn.datePattern()));
                cell.setCellStyle(dateCellStyle);
            } else {
                cell.setCellStyle(defaultCellStyle);
            }
        }
    }

    /**
     * Formats the header of a cell.
     *
     * @param cell the cell to format
     */
    public void formatHeader(final Cell cell) {
        final CellStyle headerStyle = workbook.createCellStyle();
        final Font font = workbook.createFont();
        font.setBold(true);
        headerStyle.setFont(font);
        cell.setCellStyle(headerStyle);
    }

    /**
     * Gets the index of the error comment cell in a row.
     *
     * @param row the row to check
     * @return the index of the error comment cell
     */
    private int getErrorCommentIndex(Row row) {
        final Row headers = row.getSheet().getRow(0);
        return headers.getLastCellNum();
    }

    public DataFormat getDataFormat() {
        return dataFormat;
    }

    public CellStyle getDefaultCellStyle() {
        return defaultCellStyle;
    }

    /**
     * Checks if a field is a date type.
     *
     * @param field the field to check
     * @return true if the field is a date type, false otherwise
     */
    private boolean isDate(Field field) {
        return field.getType().isAssignableFrom(Date.class) || field.getType().isAssignableFrom(LocalDate.class)
                || field.getType().isAssignableFrom(java.sql.Date.class)
                || field.getType().isAssignableFrom(LocalDateTime.class);
    }

    /**
     * Checks if a field is a numeric type.
     *
     * @param field the field to check
     * @return true if the field is a numeric type, false otherwise
     */
    private boolean isNumeric(Field field) {
        return field.getType().isAssignableFrom(Double.class) || field.getType().isAssignableFrom(Integer.class)
                || field.getType().isAssignableFrom(Long.class) || field.getType().isAssignableFrom(Float.class)
                || field.getType().isAssignableFrom(Short.class) || field.getType().isAssignableFrom(BigDecimal.class);
    }

    /**
     * Automatically resizes the columns in a sheet.
     *
     * @param sheet the sheet to resize
     * @param length the length of the sheet
     */
    public void setAutoResizing(Sheet sheet, int length) {
        for (int k = 0; k < length; k++) {
            sheet.autoSizeColumn(k);
        }
    }

}
