package com.r4tylmz.betterpoi.validation.row;

import com.r4tylmz.betterpoi.annotation.BPSheet;
import com.r4tylmz.betterpoi.constraint.RowConstraint;
import org.apache.poi.ss.usermodel.Sheet;

import java.util.Collections;
import java.util.Map;

public class DuplicateRowConstraint implements RowConstraint {
    @Override
    public Map<Integer, String> validate(Sheet sheet, BPSheet bpSheet) {
        // TODO: Implement this method to validate the row.
        return Collections.emptyMap();
    }
}
