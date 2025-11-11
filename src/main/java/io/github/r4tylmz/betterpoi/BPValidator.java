package io.github.r4tylmz.betterpoi;

import io.github.r4tylmz.betterpoi.annotation.BPColumn;
import io.github.r4tylmz.betterpoi.annotation.BPSheet;
import io.github.r4tylmz.betterpoi.i18n.MessageSourceService;
import io.github.r4tylmz.betterpoi.utils.RowUtil;
import io.github.r4tylmz.betterpoi.validation.CellValidatorManager;
import io.github.r4tylmz.betterpoi.validation.ColValidatorManager;
import io.github.r4tylmz.betterpoi.validation.RowValidatorManager;
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

/**
 * BPValidator is responsible for validating an Excel workbook.
 */
public class BPValidator {
    private static final Logger logger = LoggerFactory.getLogger(BPValidator.class);
    private Class<?> workBookClass;
    private BPMetadataHandler bpMetadataHandler;
    private final ArrayList<String> errorMessages = new ArrayList<>();
    private final RowValidatorManager rowValidatorManager;
    private final ColValidatorManager colValidatorManager;
    private MessageSourceService messageSourceService;
    private CellValidatorManager cellValidatorManager;

    /**
     * Constructor for BPValidator.
     *
     * @param workbook the workbook to validate
     * @param messageSourceService the service for retrieving localized messages
     */
    public BPValidator(Object workbook, MessageSourceService messageSourceService) {
        if (workbook == null) {
            throw new IllegalArgumentException("workbook can't be null");
        }
        if (messageSourceService == null) {
            throw new IllegalArgumentException("messageSourceService must not be null");
        }
        this.messageSourceService = messageSourceService;
        this.workBookClass = workbook.getClass();
        this.rowValidatorManager = new RowValidatorManager(messageSourceService);
        this.colValidatorManager = new ColValidatorManager(messageSourceService);
    }


    /**
     * Retrieves the names of all sheets in the given workbook.
     *
     * @param workbook the workbook from which to retrieve sheet names
     * @return a comma-separated string of all sheet names
     */
    private String getAllSheetNames(Workbook workbook) {
        final StringBuilder sb = new StringBuilder();
        for (Sheet sheet : workbook) {
            sb.append(sheet.getSheetName());
            sb.append(", ");
        }
        return sb.toString();
    }

    /**
     * Retrieves the error messages from the validation process.
     *
     * @return a list of error messages
     * @throws IllegalArgumentException if workBookClass is null
     */
    public List<String> getErrorMessages() {
        if (workBookClass == null) {
            throw new IllegalArgumentException("WorkBookClass must not be null");
        }

        return errorMessages;
    }

    /**
     * Checks if the workbook contains a sheet with the given name.
     *
     * @param workbook the workbook to check
     * @param bpSheet  the BPSheet annotation containing the sheet name
     * @return true if the sheet exists, false otherwise
     */
    private boolean isSheetExist(Workbook workbook, final BPSheet bpSheet) {
        for (Sheet sheet : workbook) {
            if (sheet != null && (workbook.isSheetHidden(workbook.getSheetIndex(sheet.getSheetName()))
                    || workbook.isSheetVeryHidden(workbook.getSheetIndex(sheet.getSheetName())))) {
                continue;
            }

            if (sheet == null || (sheet.getSheetName() != null && !sheet.getSheetName().equals(bpSheet.sheetName()))) {
                errorMessages.add(messageSourceService.getMessage("sheet.not.found.error", bpSheet.sheetName()));
                return false;
            }
        }
        return true;
    }

    /**
     * Validates the given Excel workbook.
     *
     * @param workbook the workbook to validate
     * @param messageSourceService the service for retrieving localized messages
     * @return true if the workbook is valid, false otherwise
     * @throws IllegalArgumentException if workBookClass is null
     * @throws RuntimeException if an exception occurs during validation
     */
    public boolean validate(Workbook workbook, MessageSourceService messageSourceService) {
        if (workBookClass == null) {
            throw new IllegalArgumentException("WorkBookClass must not be null");
        }

        final List<String> violations = new ArrayList<>();
        bpMetadataHandler = new BPMetadataHandler(workBookClass);
        this.messageSourceService = messageSourceService;
        try {
            final BPFormatter bpFormatter = new BPFormatter(workbook);
            cellValidatorManager = new CellValidatorManager(bpFormatter, messageSourceService);
            final List<BPSheet> bpSheets = bpMetadataHandler.getSheets();
            for (final BPSheet bpSheet : bpSheets) {
                if (bpSheet.toImport()) {
                    final int sheetIndex = workbook.getSheetIndex(bpSheet.sheetName());
                    if (sheetIndex == -1 || workbook.isSheetHidden(sheetIndex) || workbook.isSheetVeryHidden(sheetIndex)) {
                        continue;
                    }
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

    /**
     * Validates the given Excel workbook from an InputStream.
     *
     * @param inputStream the InputStream of the Excel workbook to validate
     * @return a set of validation error messages
     * @throws IllegalArgumentException if workBookClass is null
     * @throws RuntimeException if an IOException occurs while reading the workbook
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
            this.cellValidatorManager = new CellValidatorManager(bpFormatter, this.messageSourceService);
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

    /**
     * Validates the columns of the given sheet.
     *
     * @param sheet the sheet to validate
     * @param bpSheet the BPSheet annotation containing metadata for the sheet
     * @return a set of validation error messages
     */
    private Set<String> validateCols(Sheet sheet, BPSheet bpSheet) {
        return new HashSet<>(colValidatorManager.validate(sheet, bpSheet));
    }

    /**
     * Validates the rows of the given sheet.
     *
     * @param sheet the sheet to validate
     * @param bpSheet the BPSheet annotation containing metadata for the sheet
     * @return a set of validation error messages
     */
    private Set<String> validateRows(Sheet sheet, BPSheet bpSheet) {
        return new HashSet<>(rowValidatorManager.validate(sheet, bpSheet));
    }

    /**
     * Validates the given sheet.
     *
     * @param sheet the sheet to validate
     * @param bpSheet the BPSheet annotation containing metadata for the sheet
     * @return a list of validation error messages
     */
    public List<String> validateSheet(Sheet sheet, BPSheet bpSheet) {
        final List<String> sheetViolations = new ArrayList<>();
        sheetViolations.addAll(validateCols(sheet, bpSheet));
        sheetViolations.addAll(validateRows(sheet, bpSheet));
        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (RowUtil.isRowEmpty(row)) {
                continue;
            }
            final BPColumn[] bpColumns = bpSheet.columns();
            for (int column = 0; column < bpColumns.length; column++) {
                final BPColumn bpColumn = bpColumns[column];
                Cell cell = row.getCell(column);
                if (cell == null) {
                    cell = row.createCell(column);
                }
                final Field field = bpMetadataHandler.getField(bpSheet);
                CellValidatorManager cellValidatorManager = new CellValidatorManager(new BPFormatter(sheet.getWorkbook()), this.messageSourceService);
                sheetViolations.addAll(cellValidatorManager.validate(cell, bpColumn, field));
            }
        }
        return sheetViolations;
    }

    public void setWorkBookClass(Class<?> workBookClass) {
        this.workBookClass = workBookClass;
    }
}
