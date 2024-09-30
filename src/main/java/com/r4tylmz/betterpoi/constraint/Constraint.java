package com.r4tylmz.betterpoi.constraint;

import org.apache.poi.ss.usermodel.Cell;

public interface Constraint {
    String validate(Cell cell);
}
