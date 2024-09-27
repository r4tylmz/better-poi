package com.github.betterpoi.constraint;

import org.apache.poi.ss.usermodel.Cell;

public interface Constraint {
    String validate(Cell cell);
}
