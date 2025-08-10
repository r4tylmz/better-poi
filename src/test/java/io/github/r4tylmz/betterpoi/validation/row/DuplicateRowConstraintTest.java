package io.github.r4tylmz.betterpoi.validation.row;

import io.github.r4tylmz.betterpoi.BPOptions;
import io.github.r4tylmz.betterpoi.annotation.BPColumn;
import io.github.r4tylmz.betterpoi.annotation.BPSheet;
import io.github.r4tylmz.betterpoi.i18n.MessageSourceService;
import io.github.r4tylmz.betterpoi.test.EmployeeWorkbookTest;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Map;

import static org.junit.Assert.assertEquals;

public class DuplicateRowConstraintTest extends EmployeeWorkbookTest {
    public MessageSourceService messageSourceService;
    private DuplicateRowConstraint duplicateRowConstraint;

    @Before
    public void createRowDuplicateConstraint() throws Exception {
        this.messageSourceService = new MessageSourceService(BPOptions.createDefault());
        duplicateRowConstraint = new DuplicateRowConstraint(this.messageSourceService);
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
    public void validate_duplicateRow() {
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

        Row row1 = sheet.createRow(1);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = row1.createCell(i);
            cell.setCellValue("random value" + i);
        }
        Row row2 = sheet.createRow(2);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = row2.createCell(i);
            cell.setCellValue("random value" + i);
        }

        Map<Integer, String> errorMap = duplicateRowConstraint.validate(sheet, bpSheet);
        assertEquals(1, errorMap.size());
        assertEquals(messageSourceService.getMessage("duplicate.row.error"), errorMap.get(2));
    }

    @Test
    public void validate_emptySheet() {
        Sheet sheet = workbook.createSheet("testSheet");
        BPSheet bpSheet = Mockito.mock(BPSheet.class);
        BPColumn bpColumn1 = Mockito.mock(BPColumn.class);
        Mockito.when(bpColumn1.headerTitle()).thenReturn("header1");
        BPColumn bpColumn2 = Mockito.mock(BPColumn.class);
        Mockito.when(bpColumn2.headerTitle()).thenReturn("header2");
        BPColumn bpColumn3 = Mockito.mock(BPColumn.class);
        Mockito.when(bpColumn3.headerTitle()).thenReturn("header3");

        BPColumn[] bpColumns = {bpColumn1, bpColumn2, bpColumn3};
        Mockito.when(bpSheet.columns()).thenReturn(bpColumns);

        Map<Integer, String> errorMap = duplicateRowConstraint.validate(sheet, bpSheet);
        assertEquals(0, errorMap.size());
    }

    @Test
    public void validate_noDuplicateRow() {
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

        Row row1 = sheet.createRow(1);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = row1.createCell(i);
            cell.setCellValue("random value" + i);
        }
        Row row2 = sheet.createRow(2);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = row2.createCell(i);
            cell.setCellValue("value" + i);
        }

        Map<Integer, String> errorMap = duplicateRowConstraint.validate(sheet, bpSheet);
        assertEquals(0, errorMap.size());
    }

    @Test
    public void validate_singleRow() {
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

        Row row1 = sheet.createRow(1);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = row1.createCell(i);
            cell.setCellValue("random value" + i);
        }

        Map<Integer, String> errorMap = duplicateRowConstraint.validate(sheet, bpSheet);
        assertEquals(0, errorMap.size());
    }
}