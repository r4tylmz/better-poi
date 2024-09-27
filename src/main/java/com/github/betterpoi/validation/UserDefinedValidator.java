package com.github.betterpoi.validation;

import com.github.betterpoi.annotation.BPColumn;
import com.github.betterpoi.constraint.Constraint;
import com.github.betterpoi.constraint.ConstraintFactory;
import com.github.betterpoi.constraint.DefaultConstraint;

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
