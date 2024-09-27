package com.github.betterpoi.constraint;

import org.apache.poi.ss.usermodel.Cell;

public interface ColConstraint {
    String validate(Cell cell);
}
