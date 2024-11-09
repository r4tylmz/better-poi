package com.r4tylmz.betterpoi;

import com.r4tylmz.betterpoi.annotation.BPColumn;
import com.r4tylmz.betterpoi.annotation.BPExcelWorkbook;
import com.r4tylmz.betterpoi.annotation.BPSheet;
import com.r4tylmz.betterpoi.enums.ExcelType;
import com.r4tylmz.betterpoi.utils.CellUtil;
import com.r4tylmz.betterpoi.utils.ColUtil;
import com.r4tylmz.betterpoi.utils.ExcelUtils;
import org.apache.commons.beanutils.ConvertUtilsBean2;
import org.apache.commons.beanutils.Converter;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * BPImporter is responsible for importing data from an Excel file.
 * It uses Apache POI to read the Excel workbook and sheets.
 *
 * @param <T> class that extends BPExcelWorkbook
 */
public class BPImporter<T extends BPExcelWorkbook> {
    private static final Logger logger = LoggerFactory.getLogger(BPImporter.class);
    private final ConvertUtilsBean2 converter = new ConvertUtilsBean2();
    private Class<T> workbookClass;
    private BPMetadataHandler metadataHandler;
    private BPValidator bpValidator;
    private Workbook workbook;
    private ExcelType excelType = ExcelType.XLSX;

    public BPImporter() {
    }


    /**
     * Constructor for BPImporter.
     *
     * @param workbookClass the class that extends BPExcelWorkbook
     * @param excelType     the type of Excel file to import
     */
    public BPImporter(Class<T> workbookClass, ExcelType excelType) {
        this.excelType = excelType;
        this.workbookClass = workbookClass;
    }


    /**
     * Creates objects from the rows in the given sheet based on the provided values.
     *
     * @param sheet   the sheet where rows will be created
     * @param bpSheet the BPSheet annotation containing metadata for the sheet
     * @return a list of objects created from the rows in the sheet
     */
    private List<?> createObjects(Sheet sheet, BPSheet bpSheet) {
        final Map<String, Class<?>> columnsTypes = metadataHandler.getColumnTypes(bpSheet);
        final Map<String, Integer> headerMap = getHeaderMap(sheet.getRow(0));
        final List<Object> beans = new ArrayList<>();
        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (isRowCompletelyEmpty(row, bpSheet.columns().length)) continue;
            try {
                final Object bean = bpSheet.type().newInstance();
                final BPColumn[] bpColumns = bpSheet.columns();
                for (BPColumn bpColumn : bpColumns) {
                    final String header = ColUtil.getHeaderTitle(bpColumn);
                    final Cell cell = row.getCell(headerMap.get(header));
                    if (cell != null) {
                        final Class<?> type = columnsTypes.get(bpColumn.fieldName());
                        final Object value = getCellValue(cell, type);
                        final Object converted = converter.convert(value, type);
                        PropertyUtils.setProperty(bean, bpColumn.fieldName(), converted);
                    }
                }
                beans.add(bean);
            } catch (ReflectiveOperationException e) {
                logger.error(e.getMessage(), e);
            }
        }
        return beans;
    }

    /**
     * Retrieves the value of a cell based on its type.
     *
     * @param cell     the cell to retrieve the value from
     * @param cellType the type of the cell
     * @return the value of the cell
     */
    private Object getCellValue(final Cell cell, final CellType cellType) {
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
     * @param field the field type
     * @return the value of the cell
     */
    private Object getCellValue(final Cell cell, final Class<?> field) {
        if (field.equals(Integer.class) && cell.getCellType() == CellType.STRING) {
            return CellUtil.parseInteger(cell.getStringCellValue());
        } else if (field.equals(Double.class) && cell.getCellType() == CellType.STRING) {
            return CellUtil.parseDouble(cell.getStringCellValue());
        } else if (field.equals(BigDecimal.class) && cell.getCellType() == CellType.STRING) {
            return CellUtil.parseBigDecimal(cell.getStringCellValue());
        } else if (field.equals(Long.class) && cell.getCellType() == CellType.STRING) {
            return CellUtil.parseLong(cell.getStringCellValue());
        } else if (field.equals(Float.class) && cell.getCellType() == CellType.STRING) {
            return CellUtil.parseFloat(cell.getStringCellValue());
        } else if (field.equals(Boolean.class) && cell.getCellType() == CellType.STRING) {
            return converter.convert(cell.getStringCellValue(), Boolean.class);
        } else if (CellUtil.isDate(field) && cell.getCellType() == CellType.STRING) {
            return CellUtil.parseDate(cell.getStringCellValue(), field);
        }
        return getCellValue(cell, cell.getCellType());
    }

    public List<String> getErrorMessageList() {
        return bpValidator.getErrorMessages();
    }

    /**
     * Retrieves the formatted error message.
     *
     * @return the formatted error message with new lines separating each error message
     */
    private String getFormattedErrorMessage() {
        return String.join("\n", bpValidator.getErrorMessages());
    }

    /**
     * Retrieves the header map from the given first row.
     *
     * @param row the row to retrieve the header map from
     * @return a map where the key is the header name and the value is the column index
     */
    private Map<String, Integer> getHeaderMap(Row row) {
        final HashMap<String, Integer> headerMap = new HashMap<>();
        for (int i = 0; i < row.getLastCellNum(); i++) {
            final Cell cell = row.getCell(i);
            if (cell != null) {
                final String header = cell.getStringCellValue();
                if (header != null) {
                    headerMap.put(header, i);
                }
            }
        }
        return headerMap;
    }

    /**
     * Retrieves the workbook object as define in
     * {@link BPImporter#(Class)}
     *
     * @param inputStream the input stream of the Excel file
     * @return the workbook object as define in
     * {@link BPImporter#(Class)}
     */
    private Workbook getWorkbook(InputStream inputStream) {
        try {
            if (excelType == null) {
                throw new IllegalArgumentException("ExcelType must not be null");
            }
            if (excelType == ExcelType.XLS) {
                logger.info("XLS file is not supported and will be converted to XLSX before processing");
                return ExcelUtils.convertXlsToXlsx(inputStream);
            }
            if (excelType == ExcelType.XLSX) {
                return new XSSFWorkbook(inputStream);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public Class<T> getWorkbookClass() {
        return workbookClass;
    }

    public void setWorkbookClass(Class<T> workbookClass) {
        this.workbookClass = workbookClass;
    }

    /**
     * Imports the workbook from the specified file.
     *
     * @param file the file to import
     * @return the workbook object as define in
     * {@link BPImporter#(Class)}
     */
    public T importExcel(File file) {
        try (InputStream inputStream = Files.newInputStream(file.toPath())) {
            return importExcel(inputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Imports the workbook from the specified path.
     *
     * @param path the path to the file to import
     * @return the workbook object as define in
     * {@link BPImporter#(Class)}
     */
    public T importExcel(String path) {
        try (InputStream inputStream = Files.newInputStream(Paths.get(path))) {
            return importExcel(inputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Imports the workbook from the specified input stream.
     *
     * @param inputStream the input stream of the Excel file
     * @return the workbook object as define in
     * {@link BPImporter#(Class)}
     */
    public T importExcel(InputStream inputStream) {
        if (inputStream == null || workbookClass == null) {
            throw new IllegalArgumentException("inputStream or workbookClass must not be null");
        }
        try {
            final T bpWorkBook = workbookClass.newInstance();
            workbook = getWorkbook(inputStream);
            bpValidator = new BPValidator(bpWorkBook);
            metadataHandler = new BPMetadataHandler(bpWorkBook);
            final List<BPSheet> bpSheets = metadataHandler.getSheets();
            for (final BPSheet bpSheet : bpSheets) {
                if (bpSheet.toImport()) {
                    if (bpSheet.validate()) {
                        boolean isValid = bpValidator.validate(workbook);
                        if (!isValid) {
                            logger.error("Errors found in the workbook: \n{}", getFormattedErrorMessage());
                            return null;
                        }
                    }
                    final Sheet sheet = workbook.getSheet(bpSheet.sheetName());
                    final List<?> beans = createObjects(sheet, bpSheet);
                    final Field field = metadataHandler.getField(bpSheet);
                    PropertyUtils.setProperty(bpWorkBook, field.getName(), beans);
                }
            }
            return bpWorkBook;
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (workbook != null) {
                    workbook.close();
                }
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            }
        }
    }

    /**
     * Imports the workbook from the specified base64 encoded string.
     *
     * @param fileAsBase64 the base64 encoded string of the Excel file
     * @return the workbook object as define in
     * {@link BPImporter#(Class)}
     */
    public T importExcelBase64(String fileAsBase64) {
        try {
            byte[] fileAsByteArray = java.util.Base64.getDecoder().decode(fileAsBase64);
            return importExcel(new java.io.ByteArrayInputStream(fileAsByteArray));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Checks if a row is completely empty.
     *
     * @param row     the row to check
     * @param colSize the number of columns in the row
     * @return true if the row is completely empty, false otherwise
     */
    private boolean isRowCompletelyEmpty(Row row, int colSize) {
        DataFormatter dataFormatter = new DataFormatter();
        for (int i = 0; i < colSize; i++) {
            if (row.getCell(i) != null || dataFormatter.formatCellValue(row.getCell(i)).trim().isEmpty()) {
                return false;
            }
        }
        return true;
    }

    public void register(Converter converter, Class<?> clazz) {
        this.converter.register(converter, clazz);
    }
}
