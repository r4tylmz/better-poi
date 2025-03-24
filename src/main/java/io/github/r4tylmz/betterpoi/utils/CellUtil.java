package io.github.r4tylmz.betterpoi.utils;

import org.apache.commons.beanutils.ConvertUtilsBean2;
import org.apache.commons.beanutils.converters.DateConverter;
import org.apache.poi.ss.usermodel.*;
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

    /**
     * Retrieves the value of a cell based on its type.
     *
     * @param cell     the cell to retrieve the value from
     * @param cellType the type of the cell
     * @return the value of the cell
     */
    private static Object getCellValue(final Cell cell, final CellType cellType) {
        final Object value;
        switch (cellType) {
            case NUMERIC:
                final CellStyle style = cell.getCellStyle();
                final int formatNo = style.getDataFormat();
                final String formatString = style.getDataFormatString();
                if (DateUtil.isADateFormat(formatNo, formatString)) {
                    value = cell.getDateCellValue();
                } else {
                    value = cell.getNumericCellValue();
                }
                break;
            case BOOLEAN:
                value = cell.getBooleanCellValue();
                break;
            case STRING:
                value = cell.getStringCellValue();
                break;
            case BLANK:
                value = "";
                break;
            default:
                final String msg = String.format("Cannot handle cell type: %s for cell: %s", cellType.name(), cell.getAddress());
                logger.error(msg);
                throw new IllegalStateException(msg);
        }
        return value;
    }

    /**
     * Retrieves the value of a cell based on its type and the field type.
     *
     * @param cell  the cell to retrieve the value from
     * @param field the field type to convert the cell value to
     * @return the value of the cell converted to the appropriate type
     */
    public static Object getCellValue(final Cell cell, final Class<?> field) {
        if (cell == null) {
            return null;
        }

        // Get the effective cell type (resolving formula if needed)
        CellType cellType = cell.getCellType();
        if (cellType == CellType.FORMULA) {
            cellType = cell.getCachedFormulaResultType();
        }

        // If target field is String, return string representation regardless of cell type
        if (field == String.class) {
            switch (cellType) {
                case NUMERIC:
                    if (DateUtil.isCellDateFormatted(cell)) {
                        SimpleDateFormat sdf = new SimpleDateFormat(DATE_PATTERNS.get(0));
                        return sdf.format(cell.getDateCellValue());
                    }
                    cell.setCellType(CellType.STRING);
                    return cell.getStringCellValue();
                case BOOLEAN:
                    cell.setCellType(CellType.STRING);
                    return cell.getStringCellValue();
                case STRING:
                    return cell.getStringCellValue();
                case ERROR:
                    return "ERROR: " + cell.getErrorCellValue();
                case BLANK:
                default:
                    return "";
            }
        }

        // For non-String fields, handle based on cell type
        switch (cellType) {
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell) && isDate(field)) {
                    Date dateValue = cell.getDateCellValue();
                    if (field == LocalDate.class) {
                        return dateValue.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                    } else if (field == LocalDateTime.class) {
                        return dateValue.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
                    } else if (field == java.sql.Date.class) {
                        return new java.sql.Date(dateValue.getTime());
                    }
                    return dateValue;
                } else {
                    double numericValue = cell.getNumericCellValue();
                    if (field == Integer.class || field == int.class) {
                        return (int) numericValue;
                    } else if (field == Long.class || field == long.class) {
                        return (long) numericValue;
                    } else if (field == Float.class || field == float.class) {
                        return (float) numericValue;
                    } else if (field == BigDecimal.class) {
                        return BigDecimal.valueOf(numericValue);
                    } else if (field == Double.class || field == double.class) {
                        return numericValue;
                    } else if (field == Short.class || field == short.class) {
                        return (short) numericValue;
                    } else if (field == Byte.class || field == byte.class) {
                        return (byte) numericValue;
                    }
                    return numericValue;
                }
            case BOOLEAN:
                return cell.getBooleanCellValue();
            case STRING:
                String stringValue = cell.getStringCellValue();
                if (field == Integer.class || field == int.class) {
                    return parseInteger(stringValue);
                } else if (field == Double.class || field == double.class) {
                    return parseDouble(stringValue);
                } else if (field == Long.class || field == long.class) {
                    return parseLong(stringValue);
                } else if (field == Float.class || field == float.class) {
                    return parseFloat(stringValue);
                } else if (field == BigDecimal.class) {
                    return parseBigDecimal(stringValue);
                } else if (isDate(field)) {
                    return parseDate(stringValue, field);
                } else if (field == Boolean.class || field == boolean.class) {
                    return Boolean.valueOf(stringValue);
                }
                return stringValue;
            case BLANK:
                return null;
            case ERROR:
                logger.warn("Cell contains error: {}", cell.getErrorCellValue());
                return null;
            default:
                String msg = String.format("Cannot handle cell type: %s for cell: %s", cellType.name(), cell.getAddress());
                logger.error(msg);
                throw new IllegalStateException(msg);
        }
    }

}
