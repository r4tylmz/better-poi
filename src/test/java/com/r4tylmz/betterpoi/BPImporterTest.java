package com.r4tylmz.betterpoi;

import com.r4tylmz.betterpoi.enums.ExcelType;
import com.r4tylmz.betterpoi.test.EmployeeWorkbook;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.InputStream;

import static org.junit.Assert.*;

public class BPImporterTest {

    public static final String EMPLOYEE_DATA_XLSX_PATH = "src/test/resources/Fake_Employee_Data.xlsx";
    public static final String NON_EXISTENT_FILE_XLSX_PATH = "src/test/resources/Non_Existent_File.xlsx";
    public static final String RESOURCE_FAKE_EMPLOYEE_DATA_XLSX = "Fake_Employee_Data.xlsx";
    public static final String FAKE_EMPLOYEE_DATA_ERROR_XLSX = "Fake_Employee_Data_Error.xlsx";
    private BPImporter<EmployeeWorkbook> bpImporter;

    private InputStream getInputStream(String name) {
        return getClass().getClassLoader().getResourceAsStream(name);
    }

    @Test
    public void getWorkbookClass() {
        assertEquals(EmployeeWorkbook.class, bpImporter.getWorkbookClass());
    }

    @Test
    public void importExcelWithInputStream() {
        try (InputStream inputStream = getInputStream(RESOURCE_FAKE_EMPLOYEE_DATA_XLSX)) {
            EmployeeWorkbook employeeWorkbook = bpImporter.importExcel(inputStream);
            assertNotNull(employeeWorkbook);
            assertEquals(20, employeeWorkbook.getEmployeeRecordList().size());
        } catch (Exception e) {
            fail("Unexpected exception");
        }
    }

    @Test
    public void importExcelWithNonExistentPath() {
        try {
            bpImporter.importExcel(NON_EXISTENT_FILE_XLSX_PATH);
            fail("Expected NoSuchFileException");
        } catch (Exception e) {

        }
    }

    @Test
    public void importExcelWithNullExcelType() {
        try (InputStream inputStream = getInputStream(RESOURCE_FAKE_EMPLOYEE_DATA_XLSX)) {
            bpImporter = new BPImporter<>(EmployeeWorkbook.class, null);
            bpImporter.importExcel(inputStream);
        } catch (Exception e) {
            assertEquals(e.getMessage(), "java.lang.IllegalArgumentException: ExcelType must not be null");
        }
    }

    @Test
    public void importExcelWithNullInputStream() {
        InputStream inputStream = null;
        try {
            bpImporter.importExcel(inputStream);
        } catch (Exception e) {
            assertEquals(e.getMessage(), "inputStream or workbookClass must not be null");
        }
    }

    @Test
    public void importExcelWithNullPath() {
        String path = null;
        try {
            bpImporter.importExcel(path);
            fail("Expected NullPointerException");
        } catch (NullPointerException e) {
            // Expected exception
        }
    }

    @Test
    public void importExcelWithNullWorkbookClass() {
        try (InputStream inputStream = getInputStream(RESOURCE_FAKE_EMPLOYEE_DATA_XLSX)) {
            bpImporter = new BPImporter<>(null, ExcelType.XLSX);
            bpImporter.importExcel(inputStream);
        } catch (Exception e) {
            assertEquals(e.getMessage(), "inputStream or workbookClass must not be null");
        }
    }

    @Test
    public void importExcelWithValidPath() {
        EmployeeWorkbook employeeWorkbook = bpImporter.importExcel(EMPLOYEE_DATA_XLSX_PATH);
        assertNotNull(employeeWorkbook);
        assertEquals(20, employeeWorkbook.getEmployeeRecordList().size());
    }

    @Before
    public void setUp() throws Exception {
        bpImporter = new BPImporter<>(EmployeeWorkbook.class, ExcelType.XLSX);
    }

    @Test
    public void setWorkbookClass() {
        bpImporter.setWorkbookClass(EmployeeWorkbook.class);
        assertEquals(EmployeeWorkbook.class, bpImporter.getWorkbookClass());
    }

    @Test
    public void testImportExcelWithError() {
        try (InputStream inputStream = getInputStream(FAKE_EMPLOYEE_DATA_ERROR_XLSX)) {
            EmployeeWorkbook employeeWorkbook = bpImporter.importExcel(inputStream);
            assertNull(employeeWorkbook);
            assertFalse(bpImporter.getErrorMessageList().isEmpty());
        } catch (Exception e) {
            fail("Unexpected exception");
        }
    }

    @Test
    public void testImportExcelWithNoErrors() {
        try (InputStream inputStream = getInputStream(RESOURCE_FAKE_EMPLOYEE_DATA_XLSX)) {
            EmployeeWorkbook employeeWorkbook = bpImporter.importExcel(inputStream);
            assertNotNull(employeeWorkbook);
            assertTrue(bpImporter.getErrorMessageList().isEmpty());
        } catch (Exception e) {
            fail("Unexpected exception");
        }
    }

    @Test
    public void testImportExcelWithNonExistentFile() {
        try {
            bpImporter.importExcel(new File(NON_EXISTENT_FILE_XLSX_PATH));
            fail("Expected NoSuchFileException");
        } catch (Exception e) {
            // Expected exception
        }
    }

    @Test
    public void testImportExcelWithNullFile() {
        File file = null;
        try {
            bpImporter.importExcel(file);
            fail("Expected NullPointerException");
        } catch (NullPointerException e) {
            // Expected exception
        }
    }

    @Test
    public void testImportExcelWithNullWorkbookClassAndNotValidExcelType() {
        try (InputStream inputStream = getInputStream(RESOURCE_FAKE_EMPLOYEE_DATA_XLSX)) {
            bpImporter = new BPImporter<>(EmployeeWorkbook.class, ExcelType.XLS);
            bpImporter.importExcel(inputStream);
        } catch (Exception e) {
            assertNotNull(e);
        }
    }

    @Test
    public void testImportExcelWithNullWorkbookClassAndNullExcelType() {
        try (InputStream inputStream = getInputStream(RESOURCE_FAKE_EMPLOYEE_DATA_XLSX)) {
            bpImporter = new BPImporter<>(null, null);
            bpImporter.importExcel(inputStream);
        } catch (Exception e) {
            assertEquals(e.getMessage(), "inputStream or workbookClass must not be null");
        }
    }

    @Test
    public void testImportExcelWithValidFile() {
        EmployeeWorkbook employeeWorkbook = bpImporter.importExcel(new File(EMPLOYEE_DATA_XLSX_PATH));
        assertNotNull(employeeWorkbook);
        assertEquals(20, employeeWorkbook.getEmployeeRecordList().size());
    }
}