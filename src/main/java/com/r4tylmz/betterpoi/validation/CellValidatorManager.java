package com.r4tylmz.betterpoi.validation;

import com.r4tylmz.betterpoi.BPFormatter;
import com.r4tylmz.betterpoi.annotation.BPColumn;
import com.r4tylmz.betterpoi.constraint.ConstraintFactory;
import com.r4tylmz.betterpoi.validation.cell.CellHolder;
import com.r4tylmz.betterpoi.validation.cell.CellValidator;
import com.r4tylmz.betterpoi.validation.cell.PatternValidator;
import com.r4tylmz.betterpoi.validation.cell.RequiredValidator;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Manager class to handle cell validation.
 * This class aggregates multiple cell validators and applies them to cells to check for validation errors.
 */
public class CellValidatorManager {
    private final List<CellValidator> cellValidators = new ArrayList<>();
    private final BPFormatter formatter;

    /**
     * Constructor to initialize the CellValidatorManager with a formatter.
     * Adds default validators to the list.
     *
     * @param formatter the formatter to use for cell value formatting
     */
    public CellValidatorManager(BPFormatter formatter) {
        this.formatter = formatter;
        cellValidators.add(new RequiredValidator());
        cellValidators.add(new PatternValidator());
    }

    /**
     * Retrieves the formatted value of a cell.
     *
     * @param cell the cell to format
     * @return the formatted cell value as a string
     */
    public String getValue(Cell cell) {
        final DataFormatter dataFormatter = new DataFormatter();
        return dataFormatter.formatCellValue(cell).trim();
    }

    /**
     * Runs all cell validators on the specified cell.
     * Collects and returns any validation error messages.
     *
     * @param cell the cell to validate
     * @param bpColumn the BPColumn annotation containing metadata for the cell
     * @param field the field in the class corresponding to the cell
     * @return a set of validation error messages, if any
     */
    public Set<String> validate(Cell cell, BPColumn bpColumn, Field field) {
        final Set<String> violations = new HashSet<>();
        final String value = getValue(cell);
        this.cellValidators.addAll(ConstraintFactory.getInstance().getCellValidators(bpColumn.cellValidators()));
        for (CellValidator cellValidator : cellValidators) {
            final CellHolder cellHolder = new CellHolder(cell, value, field, bpColumn);
            final String errorMessage = cellValidator.validate(cellHolder);
            if (errorMessage != null) {
                String violation = String.format("Row No: %d - Column No: %d - Error: %s", cell.getRowIndex() + 1, cell.getColumnIndex() + 1, errorMessage);
                violations.add(violation);
            }
        }
        return violations;
    }
}