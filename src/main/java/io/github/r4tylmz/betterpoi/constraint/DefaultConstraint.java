package io.github.r4tylmz.betterpoi.constraint;

import io.github.r4tylmz.betterpoi.annotation.BPSheet;
import io.github.r4tylmz.betterpoi.validation.cell.CellHolder;
import io.github.r4tylmz.betterpoi.validation.cell.CellValidator;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;

import java.util.Collections;
import java.util.Map;

/**
 * Default implementation of the Constraint, ColConstraint, RowConstraint and CellValidator interfaces.
 * This class provides default validation logic for cells, columns, and rows in an Excel sheet.
 */
public class DefaultConstraint implements Constraint, ColConstraint, RowConstraint, CellValidator {

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

    /**
     * Validates the given cell holder.
     *
     * @param cellHolder the cell holder containing the cell and its metadata
     * @return a validation error message if the cell is invalid, otherwise null
     */
    @Override
    public String validate(CellHolder cellHolder) {
        return null;
    }
}