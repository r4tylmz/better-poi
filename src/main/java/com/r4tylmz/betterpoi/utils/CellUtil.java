package com.r4tylmz.betterpoi.utils;

import org.apache.commons.beanutils.ConvertUtilsBean2;
import org.apache.commons.beanutils.converters.DateConverter;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Utility class for working with cells in Excel workbooks.
 */
public class CellUtil {
    // Static list holding commonly used date patterns worldwide
    public static final List<String> DATE_PATTERNS = new ArrayList<>();
    private static final Logger logger = LoggerFactory.getLogger(CellUtil.class);
    private static final ConvertUtilsBean2 converter = new ConvertUtilsBean2();

    static {
        DATE_PATTERNS.add("dd.MM.yyyy"); // Turkey
        DATE_PATTERNS.add("yyyy-MM-dd"); // ISO 8601 standard
        DATE_PATTERNS.add("MM/dd/yyyy"); // USA
        DATE_PATTERNS.add("dd/MM/yyyy"); // Many European countries
        DATE_PATTERNS.add("yyyy.MM.dd G 'at' HH:mm:ss z"); // Japan
        DATE_PATTERNS.add("EEE, MMM d, ''yy"); // USA - short format
        DATE_PATTERNS.add("h:mm a"); // USA - short time format
        DATE_PATTERNS.add("hh 'o''clock' a, zzzz"); // USA - full time format
        DATE_PATTERNS.add("K:mm a, z"); // USA - short time with zone
        DATE_PATTERNS.add("yyyyy.MMMMM.dd GGG hh:mm a"); // Special format
        DATE_PATTERNS.add("EEE, d MMM yyyy HH:mm:ss Z"); // RFC 1123 format
        DATE_PATTERNS.add("yyyy-MM-dd'T'HH:mm:ss.SSSXXX"); // ISO 8601 full format
        DATE_PATTERNS.add("dd-MM-yyyy"); // UK, India, and similar

        DateConverter dateConverter = new DateConverter();
        dateConverter.setPatterns(DATE_PATTERNS.toArray(new String[0]));
        converter.register(dateConverter, java.util.Date.class);
        converter.register(dateConverter, java.sql.Date.class);
        converter.register(dateConverter, java.sql.Timestamp.class);
        converter.register(dateConverter, java.time.LocalDate.class);
        converter.register(dateConverter, java.time.LocalDateTime.class);
    }

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


    /**
     * Parses an integer from a string. If the direct parsing fails, it attempts to parse the string
     * using the default number format of the locale.
     *
     * @param value the string to parse as an integer
     * @return the parsed integer value or null if parsing fails
     */
    public static Integer parseInteger(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            try {
                Number number = NumberFormat.getInstance(Locale.getDefault()).parse(value.trim());
                return number.intValue();
            } catch (ParseException parseException) {
                logger.error("Failed to parse Integer: {}", value, parseException);
                return null;
            }
        }
    }

    /**
     * Parses a double from a string. If the direct parsing fails, it attempts to parse the string
     * using the default number format of the locale.
     *
     * @param value the string to parse as a double
     * @return the parsed double value or null if parsing fails
     */
    public static Double parseDouble(String value) {
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            try {
                Number number = NumberFormat.getInstance(Locale.getDefault()).parse(value.trim());
                return number.doubleValue();
            } catch (ParseException parseException) {
                logger.error("Failed to parse Double: {}", value, parseException);
                return null;
            }
        }
    }

    /**
     * Parses a BigDecimal from a string. If the direct parsing fails, it attempts to parse the string
     * using the default number format of the locale.
     *
     * @param value the string to parse as a BigDecimal
     * @return the parsed BigDecimal value or null if parsing fails
     */
    public static BigDecimal parseBigDecimal(String value) {
        try {
            return new BigDecimal(value);
        } catch (NumberFormatException e) {
            try {
                Number number = NumberFormat.getInstance(Locale.getDefault()).parse(value.trim());
                return BigDecimal.valueOf(number.doubleValue());
            } catch (ParseException parseException) {
                logger.error("Failed to parse BigDecimal: {}", value, parseException);
                return null;
            }
        }
    }

    /**
     * Parses a long from a string. If the direct parsing fails, it attempts to parse the string
     * using the default number format of the locale.
     *
     * @param value the string to parse as a long
     * @return the parsed long value or null if parsing fails
     */
    public static Long parseLong(String value) {
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            try {
                Number number = NumberFormat.getInstance(Locale.getDefault()).parse(value.trim());
                return number.longValue();
            } catch (ParseException parseException) {
                logger.error("Failed to parse Long: {}", value, parseException);
                return null;
            }
        }
    }

    /**
     * Parses a float from a string. If the direct parsing fails, it attempts to parse the string
     * using the default number format of the locale.
     *
     * @param value the string to parse as a float
     * @return the parsed float value or null if parsing fails
     */
    public static Float parseFloat(String value) {
        try {
            return Float.parseFloat(value);
        } catch (NumberFormatException e) {
            try {
                Number number = NumberFormat.getInstance(Locale.getDefault()).parse(value.trim());
                return number.floatValue();
            } catch (ParseException parseException) {
                logger.error("Failed to parse Float: {}", value, parseException);
                return null;
            }
        }
    }

    /**
     * Parses a date from a string. If the direct parsing fails, it attempts to parse the string
     * using the default date patterns.
     *
     * @param value the string to parse as a date
     * @param field the class of the field to parse the date into
     * @return the parsed date value or null if parsing fails
     */
    public static Object parseDate(String value, Class<?> field) {
        for (String pattern : DATE_PATTERNS) {
            try {
                SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
                dateFormat.setLenient(false);
                Date date = dateFormat.parse(value.trim());

                if (field.equals(Date.class)) {
                    return date;
                } else if (field.equals(LocalDate.class)) {
                    return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                } else if (field.equals(java.sql.Date.class)) {
                    return new java.sql.Date(date.getTime());
                } else if (field.equals(LocalDateTime.class)) {
                    return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
                }
            } catch (ParseException ignored) {
            }
        }

        logger.error("Failed to parse Date: {}", value);
        return converter.convert(value, field);
    }

    /**
     * Checks if the field type is a date type.
     *
     * @param field the class of the field to check
     * @return true if the field is a date type; false otherwise
     */
    public static boolean isDate(Class<?> field) {
        return field.equals(Date.class) || field.equals(LocalDate.class) ||
                field.equals(java.sql.Date.class) || field.equals(LocalDateTime.class);
    }


}
