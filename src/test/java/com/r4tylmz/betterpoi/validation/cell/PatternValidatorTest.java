package com.r4tylmz.betterpoi.validation.cell;

import com.r4tylmz.betterpoi.annotation.BPColumn;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.*;

public class PatternValidatorTest {
    private static final String REQUIRED_MSG = "Cell value [%s] is not valid. Cell value must match with the pattern : %s";
    public PatternValidator patternValidator;

    @Before
    public void createPatternValidator() throws Exception {
        patternValidator = new PatternValidator();
    }

    private CellHolder getCellHolder(String value, String pattern) {
        CellHolder cellHolder = new CellHolder();
        cellHolder.setCellValue(value);
        BPColumn bpColumn = Mockito.mock(BPColumn.class);
        cellHolder.setBpColumn(bpColumn);
        Mockito.when(cellHolder.getBpColumn().pattern()).thenReturn(pattern);
        return cellHolder;
    }

    @Test
    public void testValidate() {
        CellHolder cellHolder = getCellHolder("123456", "[0-9]*");
        final String msg = patternValidator.validate(cellHolder);
        assertNull(msg);
    }

    @Test
    public void testValidate_emptyPattern() {
        CellHolder cellHolder = getCellHolder("test value", "");
        final String msg = patternValidator.validate(cellHolder);
        assertNull(msg);
    }

    @Test
    public void testValidate_emptyValue() {
        CellHolder cellHolder = getCellHolder("", null);
        final String msg = patternValidator.validate(cellHolder);
        assertNull(msg);
    }

    @Test
    public void testValidate_notMatch() {
        CellHolder cellHolder = getCellHolder("test value", "xxxx");
        final String msg = patternValidator.validate(cellHolder);
        assertNotNull(msg);
        assertEquals(String.format(REQUIRED_MSG, cellHolder.getCellValue(), cellHolder.getBpColumn().pattern()), msg);
    }

    @Test
    public void testValidate_nullPattern() {
        CellHolder cellHolder = getCellHolder("test value", null);
        final String msg = patternValidator.validate(cellHolder);
        assertNull(msg);
    }

    @Test
    public void testValidate_nullValue() {
        CellHolder cellHolder = getCellHolder(null, null);
        final String msg = patternValidator.validate(cellHolder);
        assertNull(msg);
    }
}