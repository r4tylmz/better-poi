package com.github.betterpoi;

import com.github.betterpoi.annotation.BPColumn;
import com.github.betterpoi.annotation.BPSheet;
import com.github.betterpoi.validation.CellValidatorManager;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class BPValidator {
    private static final Logger logger = LoggerFactory.getLogger(BPValidator.class);
    private CellValidatorManager cellValidatorManager;
    private Class<?> workBookClass;
    private BPMetadataHandler bpMetadataHandler;

    private void validateSheets(XSSFWorkbook workbook, final BPSheet bpSheet) {
        final StringBuilder sb = new StringBuilder();
        for (Sheet sheet : workbook) {
            if (sheet.getSheetName() != null && !sheet.getSheetName().equals(bpSheet.sheetName())) {
                sb.append(sheet.getSheetName());
                sb.append(", ");
            }
        }
        logger.error("Unable to find sheet with name: {} during excel import (excel sheetNames: {});", bpSheet.sheetName(),
                sb);
        throw new RuntimeException("Unable to find sheet with name: " + bpSheet.sheetName());
    }

    /**
     * @param inputStream the .xlsx file
     * @return an empty set if no error otherwise a list of error messages
     */
    public Set<String> validate(InputStream inputStream) {
        if (workBookClass == null) {
            throw new IllegalArgumentException("WorkBookClass must not be null");
        }

        final Set<String> violations = new HashSet<>();
        bpMetadataHandler = new BPMetadataHandler(workBookClass);
        XSSFWorkbook workbook = null;
        try {
            workbook = new XSSFWorkbook(inputStream);
            final BPFormatter bpFormatter = new BPFormatter(workbook);
            cellValidatorManager = new CellValidatorManager(bpFormatter);
            final List<BPSheet> bpSheets = bpMetadataHandler.getSheets();
            for (final BPSheet bpSheet : bpSheets) {
                if (bpSheet.toImport()) {
                    final XSSFSheet sheet = workbook.getSheet(bpSheet.sheetName());
                    if (sheet == null) {
                        validateSheets(workbook, bpSheet);
                    }

                    violations.addAll(validateSheet(sheet, bpSheet));
                }
            }
        } catch (IOException e) {
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
        return violations;
    }

    /**
     * For each sheet rows run cells validations then row validations. <br/>
     * <b>Gotcha</b>: if row.getCell() is null we create the cell so that we can
     * add a error color to it later with
     *
     * @return
     */
    private Set<String> validateSheet(XSSFSheet sheet, BPSheet bpSheet) {
        final Set<String> sheetViolations = new HashSet<>();
        final Iterator<Row> rowIterator = sheet.iterator();
        Row row = rowIterator.next(); // skip headers
        while (rowIterator.hasNext()) {
            row = rowIterator.next();
            final BPColumn[] bpColumns = bpSheet.columns();
            for (int column = 0; column < bpColumns.length; column++) {
                final BPColumn bpColumn = bpColumns[column];
                Cell cell = row.getCell(column);
                if (cell == null) {
                    cell = row.createCell(column);
                }
                final Field field = bpMetadataHandler.getField(bpSheet);
                sheetViolations.addAll(cellValidatorManager.validate(cell, bpColumn, field));
            }
        }
        if (sheetViolations.isEmpty()) {
            // do the rows validation only if no cell errors
            // avoid iterating over all rows one more time, anyway this sheet is
            // already not valid
            // the end user will on start to see those errors once he has fix
            // cells errors
            // deliberately choose performance improvement over user experience
            //sheetViolations.addAll(rowValidatorManager.validateRows(sheet, bpSheet));
        }
        return sheetViolations;
    }

    public boolean isValid(final InputStream inputStream) {
        final Set<String> messages = validate(inputStream);
        if (!messages.isEmpty()) {
            logger.info("Excel validation errors: {}", String.join(",", messages));
        }
        return messages.isEmpty();
    }

    public void setWorkBookClass(Class<?> workBookClass) {
        this.workBookClass = workBookClass;
    }
}
