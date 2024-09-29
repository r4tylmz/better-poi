package com.github.betterpoi;

import com.github.betterpoi.annotation.BPColumn;
import com.github.betterpoi.annotation.BPSheet;
import com.github.betterpoi.validation.CellValidatorManager;
import com.github.betterpoi.validation.ColValidatorManager;
import com.github.betterpoi.validation.RowValidatorManager;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BPValidator {
    private static final Logger logger = LoggerFactory.getLogger(BPValidator.class);
    private CellValidatorManager cellValidatorManager;
    private Class<?> workBookClass;
    private BPMetadataHandler bpMetadataHandler;
    private final ArrayList<String> errorMessages = new ArrayList<>();
    private final RowValidatorManager rowValidatorManager = new RowValidatorManager();
    private final ColValidatorManager colValidatorManager = new ColValidatorManager();

    public BPValidator(Object workbook) {
        if (workbook == null) {
            throw new IllegalArgumentException("workbook can't be null");
        }
        this.workBookClass = workbook.getClass();
    }


    private String getAllSheetNames(Workbook workbook) {
        final StringBuilder sb = new StringBuilder();
        for (Sheet sheet : workbook) {
            sb.append(sheet.getSheetName());
            sb.append(", ");
        }
        return sb.toString();
    }


    private boolean isSheetExist(Workbook workbook, final BPSheet bpSheet) {
        for (Sheet sheet : workbook) {
            if (sheet == null || (sheet.getSheetName() != null && !sheet.getSheetName().equals(bpSheet.sheetName()))) {
                errorMessages.add("Unable to find sheet with name: " + bpSheet.sheetName());
                return false;
            }
        }
        return true;
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
                    final Sheet sheet = workbook.getSheet(bpSheet.sheetName());
                    if (!isSheetExist(workbook, bpSheet)) {
                        continue;
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

    public boolean validate(Workbook workbook) {
        if (workBookClass == null) {
            throw new IllegalArgumentException("WorkBookClass must not be null");
        }

        final List<String> violations = new ArrayList<>();
        bpMetadataHandler = new BPMetadataHandler(workBookClass);
        try {
            final BPFormatter bpFormatter = new BPFormatter(workbook);
            cellValidatorManager = new CellValidatorManager(bpFormatter);
            final List<BPSheet> bpSheets = bpMetadataHandler.getSheets();
            for (final BPSheet bpSheet : bpSheets) {
                if (bpSheet.toImport()) {
                    final Sheet sheet = workbook.getSheet(bpSheet.sheetName());
                    if (!isSheetExist(workbook, bpSheet)) {
                        continue;
                    }
                    violations.addAll(validateSheet(sheet, bpSheet));
                }
            }
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
        errorMessages.addAll(violations);
        return errorMessages.isEmpty();
    }

    public List<String> validateSheet(Sheet sheet, BPSheet bpSheet) {
        final List<String> sheetViolations = new ArrayList<>();
        sheetViolations.addAll(validateCols(sheet, bpSheet));
        sheetViolations.addAll(validateRows(sheet, bpSheet));
        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
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
        return sheetViolations;
    }

    private Set<String> validateRows(Sheet sheet, BPSheet bpSheet) {
        return new HashSet<>(rowValidatorManager.validate(sheet, bpSheet));
    }

    private Set<String> validateCols(Sheet sheet, BPSheet bpSheet) {
        return new HashSet<>(colValidatorManager.validate(sheet, bpSheet));
    }


    public List<String> getErrorMessages() {
        if (workBookClass == null) {
            throw new IllegalArgumentException("WorkBookClass must not be null");
        }

        return errorMessages;
    }

    public void setWorkBookClass(Class<?> workBookClass) {
        this.workBookClass = workBookClass;
    }
}
