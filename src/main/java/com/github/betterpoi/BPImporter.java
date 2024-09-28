package com.github.betterpoi;

import com.github.betterpoi.annotation.BPColumn;
import com.github.betterpoi.annotation.BPSheet;
import org.apache.commons.beanutils.ConvertUtilsBean;
import org.apache.commons.beanutils.ConvertUtilsBean2;
import org.apache.commons.beanutils.Converter;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BPImporter<T> {
    private static final Logger logger = LoggerFactory.getLogger(BPImporter.class);
    private final ConvertUtilsBean converter = new ConvertUtilsBean2();
    private Class<T> workbookClass;
    private BPMetadataHandler metadataHandler;
    private BPValidator bpValidator;
    private Workbook workbook;

    private List<?> createObjects(Sheet sheet, BPSheet bpSheet) {
        final Map<String, Class<?>> columnsTypes = metadataHandler.getColumnTypes(bpSheet);
        final List<Object> beans = new ArrayList<>();
        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (isRowCompletelyEmpty(row, bpSheet.columns().length)) continue;
            try {
                final Object bean = bpSheet.type().newInstance();
                final BPColumn[] bpColumns = bpSheet.columns();
                for (int j = 0; j < bpColumns.length; j++) {
                    final Cell cell = row.getCell(j);
                    if (cell != null) {
                        final CellType cellType = cell.getCellTypeEnum();
                        final Object value = getCellValue(cell, cellType);
                        final Class<?> type = columnsTypes.get(bpColumns[j].filedName());
                        final Object converted = converter.convert(value, type);
                        PropertyUtils.setProperty(bean, bpColumns[j].filedName(), converted);
                    }
                }
                beans.add(bean);
            } catch (ReflectiveOperationException e) {
                logger.error(e.getMessage(), e);
            }
        }
        return beans;
    }

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

    public List<String> getErrorMessageList() {
        return bpValidator.getErrorMessages();
    }

    private String getFormattedErrorMessage() {
        return String.join("\n", bpValidator.getErrorMessages());
    }

    public Class<T> getWorkbookClass() {
        return workbookClass;
    }

    public void setWorkbookClass(Class<T> workbookClass) {
        this.workbookClass = workbookClass;
    }

    /**
     * @return the workbook object as define in
     * {@link BPImporter#(Class)}
     */
    public T importExcel(InputStream inputStream) {
        if (inputStream == null || workbookClass == null) {
            throw new IllegalArgumentException("inputStream or workbookClass must not be null");
        }
        try {
            workbook = new XSSFWorkbook(inputStream);
            final T bpWorkBook = workbookClass.newInstance();
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
        } catch (final IOException | ReflectiveOperationException e) {
            logger.error(e.getMessage(), e);
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
