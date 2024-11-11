package io.github.r4tylmz.betterpoi.test;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.After;
import org.junit.Before;

public abstract class EmployeeWorkbookTest {

    public XSSFWorkbook workbook;

    public Cell getCell(String value) {
        Cell cell = workbook.createSheet("testSheet").createRow(0).createCell(0);
        cell.setCellValue(value);
        return cell;
    }

    public Row getRow(int colSize) {
        Row row = workbook.createSheet("testSheet").createRow(0);
        for (int i = 0; i < colSize; i++) {
            row.createCell(i);
        }
        return row;
    }

    @Before
    public void setUp() throws Exception {
        workbook = new XSSFWorkbook();
    }

    @After
    public void tearDown() throws Exception {
        workbook.close();
    }
}