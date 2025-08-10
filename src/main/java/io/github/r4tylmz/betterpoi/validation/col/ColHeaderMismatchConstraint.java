package io.github.r4tylmz.betterpoi.validation.col;

import io.github.r4tylmz.betterpoi.annotation.BPColumn;
import io.github.r4tylmz.betterpoi.annotation.BPSheet;
import io.github.r4tylmz.betterpoi.constraint.ColConstraint;
import io.github.r4tylmz.betterpoi.i18n.MessageSourceService;
import io.github.r4tylmz.betterpoi.utils.ColUtil;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import java.util.*;

/**
 * Constraint implementation that checks for header mismatches in columns.
 * This class validates that the headers in an Excel sheet match the expected headers defined in the BPSheet annotation.
 */
public class ColHeaderMismatchConstraint implements ColConstraint {
    private final List<String> colHeaders = new ArrayList<>();
    private final DataFormatter dataFormatter = new DataFormatter();
    private MessageSourceService messageSourceService;

    public ColHeaderMismatchConstraint(MessageSourceService messageSourceService) {
        this.messageSourceService = messageSourceService;
    }
    /**
     * Retrieves the column headers from the given sheet.
     * If the headers have already been retrieved, it returns the cached headers.
     *
     * @param sheet the Excel sheet from which to retrieve the headers
     * @return an unmodifiable list of column headers
     * @throws IllegalArgumentException if the sheet has no header row
     */
    private List<String> getColHeaders(Sheet sheet) {
        if (!colHeaders.isEmpty()) {
            return Collections.unmodifiableList(colHeaders);
        }

        final Row headerRow = sheet.getRow(0);
        if (headerRow == null) {
            throw new IllegalArgumentException("Sheet has no header row.");
        }

        for (int i = 0; i < headerRow.getLastCellNum(); i++) {
            Cell cell = headerRow.getCell(i);
            if (cell != null) {
                colHeaders.add(dataFormatter.formatCellValue(cell));
            }
        }
        return Collections.unmodifiableList(colHeaders);
    }

    @Override
    public void setMessageSourceService(MessageSourceService messageSourceService) {
        this.messageSourceService = messageSourceService;
    }

    /**
     * Validates the columns in the given sheet based on the specified BPSheet annotation.
     * Checks if the actual headers in the sheet match the expected headers defined in the BPSheet annotation.
     *
     * @param sheet   the Excel sheet to be validated
     * @param bpSheet the BPSheet annotation containing validation rules
     * @return a map where the key is the column index and the value is the validation error message
     * @throws IllegalArgumentException if the sheet has no header row
     */
    @Override
    public Map<Integer, String> validate(Sheet sheet, BPSheet bpSheet) {
        final List<String> colHeaders = getColHeaders(sheet);
        Row headerRow = sheet.getRow(0);
        if (headerRow == null) {
            throw new IllegalArgumentException("Sheet has no header row.");
        }

        Map<Integer, String> violationMap = new HashMap<>();

        for (int colNo = 0; colNo < bpSheet.columns().length; colNo++) {
            BPColumn bpColumn = bpSheet.columns()[colNo];
            String expectedHeader = ColUtil.getHeaderTitle(bpColumn);

            if (!colHeaders.contains(expectedHeader)) {
                String violation = messageSourceService.getMessage("header.mismatch.error", expectedHeader, colHeaders);
                violationMap.put(colNo, violation);
            }
        }
        return violationMap;
    }
}