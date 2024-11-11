package io.github.r4tylmz.betterpoi.validation;

import io.github.r4tylmz.betterpoi.annotation.BPSheet;
import io.github.r4tylmz.betterpoi.constraint.ColConstraint;
import io.github.r4tylmz.betterpoi.constraint.ConstraintFactory;
import io.github.r4tylmz.betterpoi.validation.col.ColHeaderMismatchConstraint;
import org.apache.poi.ss.usermodel.Sheet;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Manager class to handle column validation.
 * This class aggregates multiple column validators and applies them to columns to check for validation errors.
 */
public class ColValidatorManager implements ValidatorManager {
    private final List<ColConstraint> colValidators;

    public ColValidatorManager() {
        this.colValidators = new ArrayList<>();
        this.colValidators.add(new ColHeaderMismatchConstraint());
    }

    /**
     * Retrieves the error message for the specified column violations.
     *
     * @param colViolationMap the map of column numbers to error messages
     * @return the error message as a string
     */
    @Override
    public String getErrorMessage(Map<Integer, String> colViolationMap) {
        final StringBuilder errorMessage = new StringBuilder();
        for (Map.Entry<Integer, String> entry : colViolationMap.entrySet()) {
            errorMessage.append("Column No: ").append(entry.getKey() + 1).append(" - Error: ").append(entry.getValue()).append("\n");
        }
        return errorMessage.toString();
    }


    /**
     * Runs all column validators on the specified sheet.
     * Collects and returns any validation error messages.
     *
     * @param sheet   the sheet to validate
     * @param bpSheet the BPSheet annotation containing metadata for the sheet
     * @return a list of validation error messages, if any
     */
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
