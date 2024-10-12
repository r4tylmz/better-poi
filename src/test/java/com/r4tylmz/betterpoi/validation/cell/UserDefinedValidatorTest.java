package com.r4tylmz.betterpoi.validation.cell;

import com.r4tylmz.betterpoi.annotation.BPColumn;
import com.r4tylmz.betterpoi.constraint.DefaultConstraint;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserDefinedValidatorTest {

    private UserDefinedValidator userDefinedValidator;
    private CellHolder cellHolder;
    private BPColumn bpColumn;

    @Before
    public void setUp() {
        userDefinedValidator = new UserDefinedValidator();
        cellHolder = mock(CellHolder.class);
        bpColumn = mock(BPColumn.class);
    }

    @Test
    public void testValidateWithDefaultConstraint() {
        when(cellHolder.getBpColumn()).thenReturn(bpColumn);
        when(bpColumn.cellValidator()).thenReturn((Class) DefaultConstraint.class);

        String result = userDefinedValidator.validate(cellHolder);

        assertNull(result);
    }
}