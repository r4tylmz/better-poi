package com.r4tylmz.betterpoi.validation.cell;

import com.r4tylmz.betterpoi.annotation.BPColumn;
import com.r4tylmz.betterpoi.constraint.Constraint;
import com.r4tylmz.betterpoi.constraint.ConstraintFactory;
import com.r4tylmz.betterpoi.constraint.DefaultConstraint;

public class UserDefinedValidator implements CellValidator {
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
