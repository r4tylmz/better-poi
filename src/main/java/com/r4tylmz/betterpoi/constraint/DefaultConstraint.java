package com.r4tylmz.betterpoi.constraint;

import com.r4tylmz.betterpoi.annotation.BPSheet;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;

import java.util.Collections;
import java.util.Map;

public class DefaultConstraint implements Constraint, ColConstraint, RowConstraint {
    @Override
    public String validate(Cell cell) {
        return null;
    }


    @Override
    public Map<Integer, String> validate(Sheet sheet, BPSheet bpSheet) {
        return Collections.emptyMap();
    }
}
