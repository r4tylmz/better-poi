package io.github.r4tylmz.betterpoi.validation.col;

import io.github.r4tylmz.betterpoi.BPOptions;
import io.github.r4tylmz.betterpoi.annotation.BPColumn;
import io.github.r4tylmz.betterpoi.annotation.BPSheet;
import io.github.r4tylmz.betterpoi.i18n.MessageSourceService;
import io.github.r4tylmz.betterpoi.test.EmployeeWorkbookTest;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Map;

import static org.junit.Assert.assertEquals;

public class ColHeaderMismatchConstraintTest extends EmployeeWorkbookTest {
    public MessageSourceService messageSourceService;
    private ColHeaderMismatchConstraint colHeaderMismatchConstraint;

    @Before
    public void createColHeaderMismatch() throws Exception {
        this.messageSourceService = new MessageSourceService(BPOptions.createDefault());
        colHeaderMismatchConstraint = new ColHeaderMismatchConstraint(this.messageSourceService);
    }

    private void createSheetWithHeaders(String[] headers) {
        Row row = getRow(headers.length);
        for (int i = 0; i < headers.length; i++) {
            row.getCell(i).setCellValue(headers[i]);
        }
    }

    private Sheet getSheetWithHeaders(String[] headers) {
        createSheetWithHeaders(headers);
        return workbook.getSheet("testSheet");
    }

    @Test
    public void testValidate_colHeaderMismatch() {
        String[] headers = {"header1", "header2", "header3"};
        Sheet sheet = getSheetWithHeaders(headers);
        BPSheet bpSheet = Mockito.mock(BPSheet.class);
        BPColumn bpColumn1 = Mockito.mock(BPColumn.class);
        Mockito.when(bpColumn1.headerTitle()).thenReturn("header1");
        BPColumn bpColumn2 = Mockito.mock(BPColumn.class);
        Mockito.when(bpColumn2.headerTitle()).thenReturn("header2");
        BPColumn bpColumn3 = Mockito.mock(BPColumn.class);
        Mockito.when(bpColumn3.headerTitle()).thenReturn("header3");
        BPColumn[] bpColumns = {bpColumn1, bpColumn2, bpColumn3};
        Mockito.when(bpSheet.columns()).thenReturn(bpColumns);
        Map<Integer, String> errorMap = colHeaderMismatchConstraint.validate(sheet, bpSheet);
        assertEquals(0, errorMap.size());
    }

    @Test
    public void testValidate_colHeaderMismatchWithExtraHeader() {
        String[] headers = {"header1", "header2", "header3"};
        Sheet sheet = getSheetWithHeaders(headers);
        BPSheet bpSheet = Mockito.mock(BPSheet.class);
        BPColumn bpColumn1 = Mockito.mock(BPColumn.class);
        Mockito.when(bpColumn1.headerTitle()).thenReturn("header1");
        BPColumn bpColumn2 = Mockito.mock(BPColumn.class);
        Mockito.when(bpColumn2.headerTitle()).thenReturn("header2");
        BPColumn[] bpColumns = {bpColumn1, bpColumn2};
        Mockito.when(bpSheet.columns()).thenReturn(bpColumns);
        Map<Integer, String> errorMap = colHeaderMismatchConstraint.validate(sheet, bpSheet);
        assertEquals(0, errorMap.size());
    }

    @Test
    public void testValidate_colHeaderMismatchWithMissingHeader() {
        String[] headers = {"header1", "header2", "header3"};
        Sheet sheet = getSheetWithHeaders(headers);
        BPSheet bpSheet = Mockito.mock(BPSheet.class);
        BPColumn bpColumn1 = Mockito.mock(BPColumn.class);
        Mockito.when(bpColumn1.headerTitle()).thenReturn("header1");
        BPColumn bpColumn2 = Mockito.mock(BPColumn.class);
        Mockito.when(bpColumn2.headerTitle()).thenReturn("header2");
        BPColumn bpColumn3 = Mockito.mock(BPColumn.class);
        Mockito.when(bpColumn3.headerTitle()).thenReturn("header4");
        BPColumn[] bpColumns = {bpColumn1, bpColumn2, bpColumn3};
        Mockito.when(bpSheet.columns()).thenReturn(bpColumns);
        Map<Integer, String> errorMap = colHeaderMismatchConstraint.validate(sheet, bpSheet);
        assertEquals(1, errorMap.size());
        System.out.println(errorMap);
    }

    @Test
    public void testValidate_colHeaderMismatchWithMissingHeaderAndExtraHeader() {
        String[] headers = {"header1", "header2", "header3"};
        Sheet sheet = getSheetWithHeaders(headers);
        BPSheet bpSheet = Mockito.mock(BPSheet.class);
        BPColumn bpColumn1 = Mockito.mock(BPColumn.class);
        Mockito.when(bpColumn1.headerTitle()).thenReturn("header1");
        BPColumn bpColumn2 = Mockito.mock(BPColumn.class);
        Mockito.when(bpColumn2.headerTitle()).thenReturn("header2");
        BPColumn bpColumn3 = Mockito.mock(BPColumn.class);
        Mockito.when(bpColumn3.headerTitle()).thenReturn("header4");
        BPColumn bpColumn4 = Mockito.mock(BPColumn.class);
        Mockito.when(bpColumn4.headerTitle()).thenReturn("header5");
        BPColumn[] bpColumns = {bpColumn1, bpColumn2, bpColumn3, bpColumn4};
        Mockito.when(bpSheet.columns()).thenReturn(bpColumns);
        Map<Integer, String> errorMap = colHeaderMismatchConstraint.validate(sheet, bpSheet);
        assertEquals(2, errorMap.size());
        System.out.println(errorMap);
    }

}