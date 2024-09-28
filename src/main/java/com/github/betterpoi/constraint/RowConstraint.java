package com.github.betterpoi.constraint;

import com.github.betterpoi.annotation.BPSheet;
import org.apache.poi.ss.usermodel.Sheet;

import java.util.Map;

/**
 * Constraint that is applied to a row.
 */
public interface RowConstraint {

    /**
     * Validate the row.
     *
     * @param bpSheet the sheet to validate
     * @return Map of column index to error message.
     */
    Map<Integer, String> validate(Sheet sheet, BPSheet bpSheet);
}
