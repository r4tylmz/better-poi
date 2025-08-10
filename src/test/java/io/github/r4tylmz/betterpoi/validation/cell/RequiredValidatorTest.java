package io.github.r4tylmz.betterpoi.validation.cell;

import io.github.r4tylmz.betterpoi.BPOptions;
import io.github.r4tylmz.betterpoi.annotation.BPColumn;
import io.github.r4tylmz.betterpoi.i18n.MessageSourceService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.*;

public class RequiredValidatorTest {
    public RequiredValidator requiredValidator;
    public MessageSourceService messageSourceService;
    private CellHolder getCellHolder(String value, boolean isRequired) {
        CellHolder cellHolder = new CellHolder();
        cellHolder.setCellValue(value);
        BPColumn bpColumn = Mockito.mock(BPColumn.class);
        Mockito.when(bpColumn.required()).thenReturn(isRequired);
        cellHolder.setBpColumn(bpColumn);
        Mockito.when(cellHolder.getBpColumn().headerTitle()).thenReturn("headerTitle");
        return cellHolder;
    }

    @Before
    public void setUp() throws Exception {
        this.messageSourceService = new MessageSourceService(BPOptions.createDefault());
        requiredValidator = new RequiredValidator(this.messageSourceService);
    }

    @Test
    public void testValidate_emptyValue() {
        CellHolder cellHolder = getCellHolder("", true);
        final String msg = requiredValidator.validate(cellHolder);
        assertNotNull(msg);
        assertEquals(messageSourceService.getMessage("required.validation.error", cellHolder.getBpColumn().headerTitle()), msg);
    }

    @Test
    public void testValidate_notRequired() {
        CellHolder cellHolder = getCellHolder("test value", false);
        final String msg = requiredValidator.validate(cellHolder);
        assertNull(msg);
    }

    @Test
    public void testValidate_notRequired_emptyValue() {
        CellHolder cellHolder = getCellHolder("", false);
        final String msg = requiredValidator.validate(cellHolder);
        assertNull(msg);
    }

    @Test
    public void testValidate_notRequired_nullValue() {
        CellHolder cellHolder = getCellHolder(null, false);
        final String msg = requiredValidator.validate(cellHolder);
        assertNull(msg);
    }

    @Test
    public void testValidate_nullValue() {
        CellHolder cellHolder = getCellHolder(null, true);
        final String msg = requiredValidator.validate(cellHolder);
        assertNotNull(msg);
        assertEquals(messageSourceService.getMessage("required.validation.error", cellHolder.getBpColumn().headerTitle()), msg);
    }
}