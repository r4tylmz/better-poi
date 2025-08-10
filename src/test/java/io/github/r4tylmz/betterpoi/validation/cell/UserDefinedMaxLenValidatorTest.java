package io.github.r4tylmz.betterpoi.validation.cell;

import io.github.r4tylmz.betterpoi.BPOptions;
import io.github.r4tylmz.betterpoi.annotation.BPColumn;
import io.github.r4tylmz.betterpoi.i18n.MessageSourceService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserDefinedMaxLenValidatorTest {

    public MessageSourceService messageSourceService;
    private UserDefinedMaxLenValidator userDefinedValidator;
    private CellHolder cellHolder;
    private BPColumn bpColumn;

    @Before
    public void setUp() {
        this.messageSourceService = new MessageSourceService(BPOptions.createDefault());
        userDefinedValidator = new UserDefinedMaxLenValidator(this.messageSourceService);
        cellHolder = mock(CellHolder.class);
        bpColumn = mock(BPColumn.class);
    }

    @Test
    public void testValidate_9Chars() {
        when(cellHolder.getBpColumn()).thenReturn(bpColumn);
        when(bpColumn.headerTitle()).thenReturn("Employee Name");
        when(cellHolder.getCellValue()).thenReturn("123456789");
        String result = userDefinedValidator.validate(cellHolder);

        Assert.assertNull(result);
    }

    @Test
    public void testValidate_EmptyValue() {
        when(cellHolder.getBpColumn()).thenReturn(bpColumn);
        when(bpColumn.headerTitle()).thenReturn("Employee Name");
        when(cellHolder.getCellValue()).thenReturn("");
        String result = userDefinedValidator.validate(cellHolder);

        Assert.assertNull(result);
    }

    @Test
    public void testValidate_LongValue() {
        when(cellHolder.getBpColumn()).thenReturn(bpColumn);
        when(bpColumn.headerTitle()).thenReturn("Employee Name");
        when(cellHolder.getCellValue()).thenReturn("12345678910");
        String result = userDefinedValidator.validate(cellHolder);

        Assert.assertNotNull(result);
    }

    @Test
    public void testValidate_NullColumn() {
        when(cellHolder.getBpColumn()).thenReturn(null);
        when(cellHolder.getCellValue()).thenReturn("123");
        String result = userDefinedValidator.validate(cellHolder);

        Assert.assertNull(result);
    }

    @Test
    public void testValidate_NullValue() {
        when(cellHolder.getBpColumn()).thenReturn(bpColumn);
        when(bpColumn.headerTitle()).thenReturn("Employee Name");
        when(cellHolder.getCellValue()).thenReturn(null);
        String result = userDefinedValidator.validate(cellHolder);

        Assert.assertNull(result);
    }
}