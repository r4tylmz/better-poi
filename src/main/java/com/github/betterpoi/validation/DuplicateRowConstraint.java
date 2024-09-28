package com.github.betterpoi.validation;

import com.github.betterpoi.annotation.BPSheet;
import com.github.betterpoi.constraint.RowConstraint;
import org.apache.poi.ss.usermodel.Sheet;

import java.util.Collections;
import java.util.Map;

public class DuplicateRowConstraint implements RowConstraint {


    @Override
    public Map<Integer, String> validate(Sheet sheet, BPSheet bpSheet) {
        return Collections.emptyMap();
    }
}
