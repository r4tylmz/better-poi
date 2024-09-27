package com.github.betterpoi.validation;

import com.github.betterpoi.BPFormatter;
import com.github.betterpoi.annotation.BPColumn;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CellValidatorManager {
    private final List<CellValidator> cellValidators = new ArrayList<>();
    private final BPFormatter formatter;

    public CellValidatorManager(BPFormatter formatter) {
        this.formatter = formatter;
        cellValidators.add(new RequiredValidator());
        cellValidators.add(new UserDefinedValidator());
        cellValidators.add(new PatternValidator());
    }

    public String getValue(Cell cell) {
        final DataFormatter dataFormatter = new DataFormatter();
        return dataFormatter.formatCellValue(cell);
    }

    /**
     * Run all CellValidator on the specific cell.
     */
    public Set<String> validate(Cell cell, BPColumn bpColumn, Field field) {
        final Set<String> violations = new HashSet<>();
        final String value = getValue(cell);
        for (CellValidator cellValidator : cellValidators) {
            final CellHolder cellContext = new CellHolder(cell, value, field, bpColumn);
            final String errorMessage = cellValidator.validate(cellContext);
            if (errorMessage != null) {
                violations.add(errorMessage);
                formatter.addErrorMessage(cell, errorMessage);
            }
        }
        return violations;
    }
}
