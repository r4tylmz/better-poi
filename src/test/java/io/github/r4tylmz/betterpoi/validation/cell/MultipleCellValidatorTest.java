package io.github.r4tylmz.betterpoi.validation.cell;

import io.github.r4tylmz.betterpoi.annotation.BPColumn;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class MultipleCellValidatorTest {
    List<CellValidator> cellValidators = new ArrayList<>();

    private CellHolder getCellHolder(String value, boolean isRequired) {
        CellHolder cellHolder = new CellHolder();
        cellHolder.setCellValue(value);
        BPColumn bpColumn = Mockito.mock(BPColumn.class);
        Mockito.when(bpColumn.required()).thenReturn(isRequired);
        cellHolder.setBpColumn(bpColumn);
        Mockito.when(cellHolder.getBpColumn().pattern()).thenReturn("[0-9]+");
        Mockito.when(cellHolder.getBpColumn().headerTitle()).thenReturn("headerTitle");
        return cellHolder;
    }

    @Before
    public void setUp() {
        UserDefinedMaxLenValidator userDefinedMaxLenValidator = new UserDefinedMaxLenValidator();
        RequiredValidator requiredValidator = new RequiredValidator();
        PatternValidator patternValidator = new PatternValidator();
        cellValidators.add(userDefinedMaxLenValidator);
        cellValidators.add(requiredValidator);
        cellValidators.add(patternValidator);
    }

    @Test
    public void testValidate_notRequired_10Chars() {
        CellHolder cellHolder = getCellHolder("12345678910", false);
        List<String> msgs = new ArrayList<>();
        for (CellValidator cellValidator : cellValidators) {
            final String msg = cellValidator.validate(cellHolder);
            if (msg != null) msgs.add(msg);
        }
        assertEquals(1, msgs.size());
    }

    @Test
    public void testValidate_pattern_NotMatch_9Chars() {
        CellHolder cellHolder = getCellHolder("xxxxxxxxx", false);
        List<String> msgs = new ArrayList<>();
        for (CellValidator cellValidator : cellValidators) {
            final String msg = cellValidator.validate(cellHolder);
            if (msg != null) msgs.add(msg);
        }
        assertEquals(1, msgs.size());
    }

    @Test
    public void testValidate_pattern_match_9Chars() {
        CellHolder cellHolder = getCellHolder("123456789", false);
        List<String> msgs = new ArrayList<>();
        for (CellValidator cellValidator : cellValidators) {
            final String msg = cellValidator.validate(cellHolder);
            if (msg != null) msgs.add(msg);
        }
        assertEquals(0, msgs.size());
    }

    @Test
    public void testValidate_pattern_notMatch_10Chars() {
        CellHolder cellHolder = getCellHolder("12345678910", false);
        List<String> msgs = new ArrayList<>();
        for (CellValidator cellValidator : cellValidators) {
            final String msg = cellValidator.validate(cellHolder);
            if (msg != null) msgs.add(msg);
        }
        assertEquals(1, msgs.size());
    }

    @Test
    public void testValidate_pattern_notMatch_10Chars_required() {
        CellHolder cellHolder = getCellHolder("           ", true);
        List<String> msgs = new ArrayList<>();
        for (CellValidator cellValidator : cellValidators) {
            final String msg = cellValidator.validate(cellHolder);
            if (msg != null) msgs.add(msg);
        }
        assertEquals(2, msgs.size());
    }

    @Test
    public void testValidate_required_10Chars() {
        CellHolder cellHolder = getCellHolder("12345678910", true);
        List<String> msgs = new ArrayList<>();
        for (CellValidator cellValidator : cellValidators) {
            final String msg = cellValidator.validate(cellHolder);
            if (msg != null) msgs.add(msg);
        }
        assertEquals(1, msgs.size());
    }

    @Test
    public void testValidate_required_emptyValue() {
        CellHolder cellHolder = getCellHolder("", true);
        List<String> msgs = new ArrayList<>();
        for (CellValidator cellValidator : cellValidators) {
            final String msg = cellValidator.validate(cellHolder);
            if (msg != null) msgs.add(msg);
        }
        assertEquals(2, msgs.size());
    }
}
