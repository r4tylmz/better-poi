package com.r4tylmz.betterpoi;

import com.r4tylmz.betterpoi.annotation.BPColumn;
import org.apache.poi.hssf.util.HSSFColor;
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

public class BPFormatter {
    private static final Logger logger = LoggerFactory.getLogger(BPFormatter.class);
    private final DataFormat dataFormat;
    private final CellStyle defaultCellStyle;
    private final CellStyle errorStyle;
    private final Workbook workbook;

    public BPFormatter(Workbook workbook) {
        this.workbook = workbook;
        defaultCellStyle = workbook.createCellStyle();
        dataFormat = workbook.createDataFormat();
        errorStyle = createErrorCellStyle();
    }

    public static XSSFColor hex2Color(String colorStr) {
        Color color = Color.decode(colorStr);
        return new XSSFColor(new Color(color.getRed(), color.getGreen(), color.getBlue()));
    }

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

    public void addErrorMessage(Cell cell, String message) {
        final Row row = cell.getRow();
        final int commentIndex = getErrorCommentIndex(row);
        cell.setCellStyle(errorStyle);
        // add a 'comment' cell to end of row for each cell in error
        addCommentCell(message, commentIndex, row);
    }

    public void addErrorMessage(Row row, String message) {
        final int commentIndex = getErrorCommentIndex(row);
        if (commentIndex < 0) {
            final String msg = "Unable to add error comment to current line. The current row {} of sheet {} doesn't seems to have any cell (lastCellNum {}).";
            logger.error(msg, row.getRowNum(), row.getSheet().getSheetName(), commentIndex);
            return;
        }
        addCommentCell(message, commentIndex, row);
    }

    private CellStyle createErrorCellStyle() {
        final CellStyle style = workbook.createCellStyle();
        style.setFillForegroundColor(HSSFColor.RED.index);
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setFillBackgroundColor(HSSFColor.RED.index);
        final Font font = workbook.createFont();
        font.setColor(IndexedColors.WHITE.getIndex());
        style.setFont(font);
        return style;
    }

    public void formatHeader(final Cell cell) {
        final CellStyle headerStyle = workbook.createCellStyle();
        final Font font = workbook.createFont();
        font.setBold(true);
        headerStyle.setFont(font);
        cell.setCellStyle(headerStyle);
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

            if (isDate(field)) {
                final CellStyle dateCellStyle = workbook.createCellStyle();
                dateCellStyle.setDataFormat(dataFormat.getFormat(bpColumn.datePattern()));
                cell.setCellStyle(dateCellStyle);
            } else {
                cell.setCellStyle(defaultCellStyle);
            }
        }
    }

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

    private boolean isDate(Field field) {
        return field.getType().isAssignableFrom(Date.class) || field.getType().isAssignableFrom(LocalDate.class)
                || field.getType().isAssignableFrom(java.sql.Date.class)
                || field.getType().isAssignableFrom(LocalDateTime.class);
    }

    private boolean isNumeric(Field field) {
        return field.getType().isAssignableFrom(Double.class) || field.getType().isAssignableFrom(Integer.class)
                || field.getType().isAssignableFrom(Long.class) || field.getType().isAssignableFrom(Float.class)
                || field.getType().isAssignableFrom(Short.class) || field.getType().isAssignableFrom(BigDecimal.class);
    }

    public void setAutoResizing(Sheet sheet, int length) {
        for (int k = 0; k < length; k++) {
            sheet.autoSizeColumn(k);
        }
    }

}
