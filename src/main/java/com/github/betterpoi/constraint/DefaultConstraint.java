package com.github.betterpoi.constraint;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class DefaultConstraint implements Constraint, ColConstraint, RowConstraint {
    @Override
    public String validate(Cell cell) {
        return null;
    }

    @Override
    public Set<String> columnsHeaders() {
        return Collections.emptySet();
    }

    @Override
    public Map<Integer, String> validate(Iterator<Row> rowIterator) {
        return Collections.emptyMap();
    }
}
