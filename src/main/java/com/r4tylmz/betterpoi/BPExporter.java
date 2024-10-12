package com.r4tylmz.betterpoi;

import com.r4tylmz.betterpoi.annotation.BPColumn;
import com.r4tylmz.betterpoi.annotation.BPSheet;
import com.r4tylmz.betterpoi.utils.ColUtil;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

/**
 * BPExporter is responsible for exporting data to an Excel file.
 * It uses Apache POI to create and format the Excel workbook and sheets.
 */
public class BPExporter {
    private static final Logger logger = LoggerFactory.getLogger(BPExporter.class);
    private final Object bpWorkbook;
    private Workbook workbook;
    private BPFormatter bpFormatter;
    private BPMetadataHandler bpMetadataHandler;

    /**
     * Constructor for BPExporter.
     *
     * @param bpWorkbook the @BPWorkbook annotated object containing the data to be exported
     */
    public BPExporter(Object bpWorkbook) {
        this.bpWorkbook = bpWorkbook;
    }

    /**
     * Creates rows in the given sheet based on the provided values.
     *
     * @param sheet   the sheet where rows will be created
     * @param bpSheet the BPSheet annotation containing metadata for the sheet
     * @param values  the list of values to be written to the sheet
     */
    private void createRows(Sheet sheet, BPSheet bpSheet, List<?> values) {
        Map<String, Field> fieldMap = bpMetadataHandler.getDataFields(bpSheet);
        for (int rowIndex = 0; rowIndex < values.size(); rowIndex++) {
            Row row = sheet.createRow(rowIndex + 1);
            Object value = values.get(rowIndex);
            for (int cellIndex = 0; cellIndex < bpSheet.columns().length; cellIndex++) {
                BPColumn bpColumn = bpSheet.columns()[cellIndex];
                Object cellValue = getProperty(value, bpColumn);
                Cell cell = row.createCell(cellIndex);
                bpFormatter.formatCell(fieldMap.get(bpColumn.fieldName()), bpColumn, cell, cellValue);
            }
        }
    }

    /**
     * Creates a new sheet in the workbook and sets up the header row.
     *
     * @param bpSheet the BPSheet annotation containing metadata for the sheet
     * @return the created sheet
     */
    private Sheet createSheet(BPSheet bpSheet) {
        Sheet sheet = workbook.createSheet("Sheet1");
        Row rowHeader = sheet.createRow(0);
        for (int i = 0; i < bpSheet.columns().length; i++) {
            BPColumn bpColumn = bpSheet.columns()[i];
            rowHeader.createCell(i).setCellValue(ColUtil.getHeaderTitle(bpColumn));
            bpFormatter.formatHeader(rowHeader.getCell(i));
        }
        bpFormatter.setAutoResizing(sheet, bpSheet.columns().length);
        return sheet;
    }

    /**
     * Exports the workbook to an Excel file at the specified path.
     *
     * @param file the file to which the workbook will be written
     */
    public void exportExcel(File file) {
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            exportExcel(fileOutputStream);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Exports the workbook to an Excel file at the specified path.
     *
     * @param path the path where the Excel file will be written
     */
    public void exportExcel(String path) {
        try {
            File file = new File(path);
            exportExcel(file);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Exports the workbook to an OutputStream.
     *
     * @param outputStream the OutputStream to which the workbook will be written
     */
    public void exportExcel(OutputStream outputStream) {
        try {
            workbook = new XSSFWorkbook();
            bpMetadataHandler = new BPMetadataHandler(bpWorkbook);
            bpFormatter = new BPFormatter(workbook);
            List<BPSheet> bpSheets = bpMetadataHandler.getSheets();
            for (BPSheet bpSheet : bpSheets) {
                Sheet sheet = createSheet(bpSheet);
                List<?> values = bpMetadataHandler.getValues(bpWorkbook, bpSheet);
                createRows(sheet, bpSheet, values);
            }
            workbook.write(outputStream);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (workbook != null) {
                    workbook.close();
                }
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
    }

    /**
     * Retrieves the property value from the given bean based on the BPColumn annotation.
     *
     * @param bean the object from which the property value will be retrieved
     * @param bpColumn the BPColumn annotation containing metadata for the property
     * @return the property value
     */
    private Object getProperty(Object bean, BPColumn bpColumn) {
        try {
            return PropertyUtils.getProperty(bean, bpColumn.fieldName());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}