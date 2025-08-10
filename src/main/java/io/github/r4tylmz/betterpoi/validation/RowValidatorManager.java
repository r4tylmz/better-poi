package io.github.r4tylmz.betterpoi.validation;

import io.github.r4tylmz.betterpoi.annotation.BPSheet;
import io.github.r4tylmz.betterpoi.constraint.ConstraintFactory;
import io.github.r4tylmz.betterpoi.constraint.RowConstraint;
import io.github.r4tylmz.betterpoi.i18n.MessageSourceService;
import org.apache.poi.ss.usermodel.Sheet;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Manager class to handle row validation.
 * This class aggregates multiple row validators and applies them to rows to check for validation errors.
 */
public class RowValidatorManager implements ValidatorManager {

    private final MessageSourceService messageSourceService;

    public RowValidatorManager(MessageSourceService messageSourceService) {
        this.messageSourceService = messageSourceService;
    }

    /**
     * Retrieves the error message for the specified row violations.
     *
     * @param rowViolationMap the map of row numbers to error messages
     * @return the error message as a string
     */
    @Override
    public String getErrorMessage(Map<Integer, String> rowViolationMap) {
        final StringBuilder errorMessage = new StringBuilder();
        for (Map.Entry<Integer, String> entry : rowViolationMap.entrySet()) {
            errorMessage.append(messageSourceService.getMessage("error.row.violation", entry.getValue()));
        }
        return errorMessage.toString();
    }

    /**
     * Runs all row validators on the specified sheet.
     * Collects and returns any validation error messages.
     *
     * @param sheet   the sheet to validate
     * @param bpSheet the BPSheet annotation containing metadata for the sheet
     * @return a list of validation error messages, if any
     */
    @Override
    public List<String> validate(Sheet sheet, BPSheet bpSheet) {
        final List<String> violations = new ArrayList<>();
        final Class<? extends RowConstraint>[] validators = bpSheet.rowValidators();
        for (Class<? extends RowConstraint> validatorClass : validators) {
            final RowConstraint validator = ConstraintFactory.getInstance(messageSourceService).getRowConstraint(validatorClass);
            final Map<Integer, String> rowViolations = validator.validate(sheet, bpSheet);
            if (!rowViolations.isEmpty()) {
                violations.add(getErrorMessage(rowViolations));
            }
        }
        return violations;
    }
}
