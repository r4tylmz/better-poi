package com.r4tylmz.betterpoi.validation;

import com.r4tylmz.betterpoi.BPFormatter;
import com.r4tylmz.betterpoi.annotation.BPColumn;
import com.r4tylmz.betterpoi.validation.cell.*;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CellValidatorManager {
    private final List<CellValidator> cellValidators = new ArrayList<>();

    public CellValidatorManager(BPFormatter formatter) {
        cellValidators.add(new RequiredValidator());
        cellValidators.add(new UserDefinedValidator());
        cellValidators.add(new PatternValidator());
    }

    public String getValue(Cell cell) {
        final DataFormatter dataFormatter = new DataFormatter();
        return dataFormatter.formatCellValue(cell).trim();
    }

    /**
     * Run all CellValidator on the specific cell.
     */
    public Set<String> validate(Cell cell, BPColumn bpColumn, Field field) {
        final Set<String> violations = new HashSet<>();
        final String value = getValue(cell);
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
