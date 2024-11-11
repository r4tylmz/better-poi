package io.github.r4tylmz.betterpoi.test;

import io.github.r4tylmz.betterpoi.constraint.Constraint;
import org.apache.poi.ss.usermodel.Cell;

public class CustomConstraint implements Constraint {
    @Override
    public String validate(Cell cell) {
        return "Custom constraint message";
    }
}
