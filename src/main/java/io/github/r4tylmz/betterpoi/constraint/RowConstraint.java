package io.github.r4tylmz.betterpoi.constraint;

import io.github.r4tylmz.betterpoi.annotation.BPSheet;
import org.apache.poi.ss.usermodel.Sheet;

import java.util.Map;

/**
 * Constraint that is applied to a row.
 * Implementations of this interface should provide validation logic for rows in a sheet.
 */
public interface RowConstraint {

    /**
     * Validates the row in the given sheet based on the specified BPSheet annotation.
     *
     * @param sheet the Excel sheet to be validated
     * @param bpSheet the BPSheet annotation containing validation rules
     * @return a map where the key is the column index and the value is the validation error message
     */
    Map<Integer, String> validate(Sheet sheet, BPSheet bpSheet);
}