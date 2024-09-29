package com.github.betterpoi.validation;

import com.github.betterpoi.annotation.BPSheet;
import com.github.betterpoi.constraint.ColConstraint;
import com.github.betterpoi.constraint.ConstraintFactory;
import org.apache.poi.ss.usermodel.Sheet;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ColValidatorManager implements ValidatorManager {
    private final List<ColConstraint> colValidators;

    public ColValidatorManager() {
        this.colValidators = new ArrayList<>();
        this.colValidators.add(new ColHeaderMismatchConstraint());
    }

    @Override
    public String getErrorMessage(Map<Integer, String> colViolationMap) {
        final StringBuilder errorMessage = new StringBuilder();
        for (Map.Entry<Integer, String> entry : colViolationMap.entrySet()) {
            errorMessage.append("Column No: ").append(entry.getKey() + 1).append(" - Error: ").append(entry.getValue()).append("\n");
        }
        return errorMessage.toString();
    }

    @Override
    public List<String> validate(Sheet sheet, BPSheet bpSheet) {
        final List<String> violations = new ArrayList<>();
        this.colValidators.addAll(ConstraintFactory.getInstance().getColumnConstraints(bpSheet.colValidators()));
        for (ColConstraint validator : colValidators) {
            final Map<Integer, String> colViolations = validator.validate(sheet, bpSheet);
            if (!colViolations.isEmpty()) {
                violations.add(getErrorMessage(colViolations));
            }
        }
        return violations;
    }
}
