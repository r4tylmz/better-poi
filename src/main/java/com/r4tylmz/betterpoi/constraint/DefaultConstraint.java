package com.r4tylmz.betterpoi.constraint;

import com.r4tylmz.betterpoi.annotation.BPSheet;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;

import java.util.Collections;
import java.util.Map;

/**
 * Default implementation of the Constraint, ColConstraint, and RowConstraint interfaces.
 * This class provides default validation logic for cells, columns, and rows in an Excel sheet.
 */
public class DefaultConstraint implements Constraint, ColConstraint, RowConstraint {

    /**
     * Validates the given cell.
     *
     * @param cell the Excel cell to be validated
     * @return a validation error message if the cell is invalid, or null if the cell is valid
     */
    @Override
    public String validate(Cell cell) {
        return null;
    }

    /**
     * Validates the columns in the given sheet based on the specified BPSheet annotation.
     *
     * @param sheet   the Excel sheet to be validated
     * @param bpSheet the BPSheet annotation containing validation rules
     * @return a map where the key is the column index and the value is the validation error message
     */
    @Override
    public Map<Integer, String> validate(Sheet sheet, BPSheet bpSheet) {
        return Collections.emptyMap();
    }
}