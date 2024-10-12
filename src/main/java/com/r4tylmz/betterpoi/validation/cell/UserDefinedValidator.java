package com.r4tylmz.betterpoi.validation.cell;

import com.r4tylmz.betterpoi.annotation.BPColumn;
import com.r4tylmz.betterpoi.constraint.Constraint;
import com.r4tylmz.betterpoi.constraint.ConstraintFactory;
import com.r4tylmz.betterpoi.constraint.DefaultConstraint;

/**
 * Validator implementation that uses a user-defined constraint to validate a cell.
 * This class retrieves the custom validator specified in the BPColumn annotation and uses it to validate the cell.
 */
public class UserDefinedValidator implements CellValidator {
    /**
     * Validates the given cell holder.
     * Uses the custom validator specified in the BPColumn annotation to validate the cell.
     *
     * @param cellHolder the cell holder containing the cell and its metadata
     * @return a validation error message if the cell is invalid, otherwise null
     */
    @Override
    public String validate(CellHolder cellHolder) {
        final BPColumn bpColumn = cellHolder.getBpColumn();
        final Class<? extends Constraint> validatorClass = bpColumn.cellValidator();
        if (validatorClass == null || validatorClass.equals(DefaultConstraint.class)) {
            return null;
        }

        final Constraint validator = ConstraintFactory.getInstance().getConstraint(validatorClass);
        return validator.validate(cellHolder.getCell());
    }
}