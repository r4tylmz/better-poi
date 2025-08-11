package io.github.r4tylmz.betterpoi;

import io.github.r4tylmz.betterpoi.enums.ExcelType;
import io.github.r4tylmz.betterpoi.exception.BPConfigurationException;
import io.github.r4tylmz.betterpoi.exception.BPImportException;
import io.github.r4tylmz.betterpoi.exception.BPValidationException;
import io.github.r4tylmz.betterpoi.test.EmployeeWorkbook;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Base64;
import java.util.List;

import static org.junit.Assert.*;

public class BPImporterTest {

    public static final String EMPLOYEE_DATA_XLSX_PATH = "src/test/resources/Fake_Employee_Data.xlsx";
    public static final String NON_EXISTENT_FILE_XLSX_PATH = "src/test/resources/Non_Existent_File.xlsx";
    public static final String RESOURCE_FAKE_EMPLOYEE_DATA_XLSX = "Fake_Employee_Data.xlsx";
    public static final String FAKE_EMPLOYEE_DATA_ERROR_XLSX = "Fake_Employee_Data_Error.xlsx";
    public static final String FAKE_EMPLOYEE_DATA_XLSX_GENERAL_FORMAT = "Fake_Employee_Data_General_Format.xlsx";
    public static final String FAKE_EMPLOYEE_DATA_XLSX_GENERAL_FORMAT_PATH = "src/test/resources/Fake_Employee_Data_General_Format.xlsx";

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
            bpImporter = new BPImporter<>(EmployeeWorkbook.class, BPOptions.createDefault());
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
        } catch (BPImportException e) {
            assertEquals("Input stream cannot be null", e.getMessage());
        }
    }

    @Test
    public void importExcelWithNullPath() {
        String path = null;
        try {
            bpImporter.importExcel(path);
            fail("Expected BPImportException");
        } catch (BPImportException e) {
            assertEquals("Path cannot be null or empty", e.getMessage());
        }
    }

    @Test
    public void importExcelWithNullWorkbookClass() {
        try (InputStream inputStream = getInputStream(RESOURCE_FAKE_EMPLOYEE_DATA_XLSX)) {
            BPOptions options = BPOptions
                    .builder()
                    .withExcelType(ExcelType.XLSX)
                    .withBundleName("messages")
                    .withLocale("tr")
                    .build();
            bpImporter = new BPImporter<>(null, options);
            bpImporter.importExcel(inputStream);
        } catch (BPConfigurationException e) {
            assertEquals("Workbook class is not configured", e.getMessage());
        } catch (IOException e) {
            // Handle IOException from closing the stream
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
        BPOptions options = BPOptions
                .builder()
                .withExcelType(ExcelType.XLSX)
                .withBundleName("messages")
                .withLocale("tr")
                .build();
        bpImporter = new BPImporter<>(EmployeeWorkbook.class, options);
    }

    @Test
    public void setWorkbookClass() {
        bpImporter.setWorkbookClass(EmployeeWorkbook.class);
        assertEquals(EmployeeWorkbook.class, bpImporter.getWorkbookClass());
    }

    @Test
    public void testImportExcelWithError() {
        try (InputStream inputStream = getInputStream(FAKE_EMPLOYEE_DATA_ERROR_XLSX)) {
            // Import should complete even with validation errors
            EmployeeWorkbook employeeWorkbook = bpImporter.importExcel(inputStream);
            assertNotNull(employeeWorkbook);
            
            // Check that validation errors were detected
            assertTrue(bpImporter.hasValidationErrors());
            assertFalse(bpImporter.isValidationSuccessful());
            
            // Check that error messages are available
            List<String> errorMessages = bpImporter.getErrorMessageList();
            assertFalse(errorMessages.isEmpty());
            
            // Verify we can explicitly throw exception if needed
            try {
                bpImporter.throwValidationExceptionIfErrors();
                fail("Expected BPValidationException when calling throwValidationExceptionIfErrors");
            } catch (BPValidationException e) {
                assertTrue(e.getMessage().startsWith("Workbook validation failed"));
                assertFalse(e.getValidationErrors().isEmpty());
            }
        } catch (IOException e) {
            // Handle IOException from closing the stream
        }
    }

    @Test
    public void testImportExcelWithGeneralFormat() {
        try (InputStream inputStream = getInputStream(FAKE_EMPLOYEE_DATA_XLSX_GENERAL_FORMAT)) {
            EmployeeWorkbook employeeWorkbook = bpImporter.importExcel(inputStream);
            assertNotNull(employeeWorkbook);
            assertEquals(20, employeeWorkbook.getEmployeeRecordList().size());
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
            assertFalse(bpImporter.hasValidationErrors());
            assertTrue(bpImporter.isValidationSuccessful());
        } catch (Exception e) {
            fail("Unexpected exception");
        }
    }

    @Test
    public void testValidationStatusMethods() {
        try (InputStream inputStream = getInputStream(FAKE_EMPLOYEE_DATA_ERROR_XLSX)) {
            // Import with validation errors
            EmployeeWorkbook employeeWorkbook = bpImporter.importExcel(inputStream);
            assertNotNull(employeeWorkbook);
            
            // Test validation status methods
            assertTrue(bpImporter.hasValidationErrors());
            assertFalse(bpImporter.isValidationSuccessful());
            
            // Test error message access
            List<String> errorMessages = bpImporter.getErrorMessageList();
            assertFalse(errorMessages.isEmpty());
            assertTrue(errorMessages.size() > 0);
            
        } catch (IOException e) {
            fail("Unexpected IOException");
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
    public void importExcelWithNullFile() {
        File file = null;
        try {
            bpImporter.importExcel(file);
            fail("Expected BPImportException");
        } catch (BPImportException e) {
            assertEquals("File cannot be null", e.getMessage());
        }
    }

    @Test
    public void testImportExcelWithNullWorkbookClassAndNotValidExcelType() {
        try (InputStream inputStream = getInputStream(RESOURCE_FAKE_EMPLOYEE_DATA_XLSX)) {
            BPOptions options = BPOptions
                    .builder()
                    .withExcelType(ExcelType.XLS)
                    .withBundleName("messages")
                    .withLocale("tr")
                    .build();
            bpImporter = new BPImporter<>(EmployeeWorkbook.class, options);
            bpImporter.importExcel(inputStream);
        } catch (Exception e) {
            assertNotNull(e);
        }
    }

    @Test
    public void testImportExcelWithNullWorkbookClassAndNullExcelType() {
        try (InputStream inputStream = getInputStream(RESOURCE_FAKE_EMPLOYEE_DATA_XLSX)) {
            bpImporter = new BPImporter<>(null, BPOptions.createDefault());
            bpImporter.importExcel(inputStream);
        } catch (BPConfigurationException e) {
            assertEquals("Workbook class is not configured", e.getMessage());
        } catch (IOException e) {
            // Handle IOException from closing the stream
        }
    }

    @Test
    public void testImportExcelWithValidBase64() {
        try {
            String base64 = Base64.getEncoder().encodeToString(Files.readAllBytes(new File(FAKE_EMPLOYEE_DATA_XLSX_GENERAL_FORMAT_PATH).toPath()));
            EmployeeWorkbook employeeWorkbook = bpImporter.importExcelBase64(base64);
            assertNotNull(employeeWorkbook);
            assertEquals(20, employeeWorkbook.getEmployeeRecordList().size());
        } catch (Exception e) {
            fail("Unexpected exception");
        }
    }

    @Test
    public void testImportExcelWithValidFile() {
        EmployeeWorkbook employeeWorkbook = bpImporter.importExcel(new File(EMPLOYEE_DATA_XLSX_PATH));
        assertNotNull(employeeWorkbook);
        assertEquals(20, employeeWorkbook.getEmployeeRecordList().size());
    }
}