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

public class BPExporter {
    private static final Logger logger = LoggerFactory.getLogger(BPExporter.class);
    private final Object bpWorkbook;
    private Workbook workbook;
    private BPFormatter bpFormatter;
    private BPMetadataHandler bpMetadataHandler;

    public BPExporter(Object bpWorkbook) {
        this.bpWorkbook = bpWorkbook;
    }

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

    public void exportExcel(File file) {
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            exportExcel(fileOutputStream);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public void exportExcel(String path) {
        try {
            File file = new File(path);
            exportExcel(file);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

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

    private Object getProperty(Object bean, BPColumn bpColumn) {
        try {
            return PropertyUtils.getProperty(bean, bpColumn.fieldName());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new RuntimeException(e.getMessage(), e);
        }
    }

}
