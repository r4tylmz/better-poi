package com.r4tylmz.betterpoi.validation;

import com.r4tylmz.betterpoi.constraint.DefaultConstraint;
import com.r4tylmz.betterpoi.test.EmployeeWorkbookTest;
import org.apache.poi.ss.usermodel.Cell;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class DefaultConstraintTest extends EmployeeWorkbookTest {
    private DefaultConstraint defaultConstraint;

    @Before
    public void setDefaultConstraint() {
        defaultConstraint = new DefaultConstraint();
    }

    @Test
    public void testValidate_cell() {
        Cell cell = getCell("test value");
        String result = defaultConstraint.validate(cell);
        assertNull(result);
    }

    @Test
    public void testValidate_cellEmpty() {
        Cell emptyCell = getCell("");
        String result = defaultConstraint.validate(emptyCell);
        assertNull(result);
    }

    @Test
    public void testValidate_cellNull() {
        Cell cell = null;
        String result = defaultConstraint.validate(cell);
        assertNull(result);
    }

    @Test
    public void testValidate_mapEmpty() {
        Map<Integer, String> map = defaultConstraint.validate(null, null);
        assertEquals(Collections.emptyMap(), map);
    }
}
