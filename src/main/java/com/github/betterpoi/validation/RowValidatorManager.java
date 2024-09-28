package com.github.betterpoi.validation;

import com.github.betterpoi.annotation.BPSheet;
import com.github.betterpoi.constraint.ConstraintFactory;
import com.github.betterpoi.constraint.RowConstraint;
import org.apache.poi.ss.usermodel.Sheet;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RowValidatorManager implements ValidatorManager {

    @Override
    public String getErrorMessage(Map<Integer, String> rowViolationMap) {
        final StringBuilder errorMessage = new StringBuilder();
        for (Map.Entry<Integer, String> entry : rowViolationMap.entrySet()) {
            errorMessage.append("Row No: ").append(entry.getKey() + 1).append(" - Error: ").append(entry.getValue());
        }
        return errorMessage.toString();
    }

    @Override
    public List<String> validate(Sheet sheet, BPSheet bpSheet) {
        final List<String> violations = new ArrayList<>();
        final Class<? extends RowConstraint>[] validators = bpSheet.rowValidators();
        for (Class<? extends RowConstraint> validatorClass : validators) {
            final RowConstraint validator = ConstraintFactory.getInstance().getRowConstraint(validatorClass);
            final Map<Integer, String> rowViolations = validator.validate(sheet, bpSheet);
            if (!rowViolations.isEmpty()) {
                violations.add(getErrorMessage(rowViolations));
            }
        }
        return violations;
    }
}
