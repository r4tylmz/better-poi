package io.github.r4tylmz.betterpoi.exception;

import io.github.r4tylmz.betterpoi.BPImporter;
import io.github.r4tylmz.betterpoi.BPOptions;
import io.github.r4tylmz.betterpoi.annotation.BPColumn;
import io.github.r4tylmz.betterpoi.annotation.BPExcelWorkbook;
import io.github.r4tylmz.betterpoi.annotation.BPSheet;
import io.github.r4tylmz.betterpoi.annotation.BPWorkbook;
import io.github.r4tylmz.betterpoi.enums.ExcelType;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Test class for exception handling features.
 */
public class ExceptionHandlingTest {

    // Test data class
    public static class TestData {
        private String name;
        private Integer age;
        
        // Getters and setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public Integer getAge() { return age; }
        public void setAge(Integer age) { this.age = age; }
    }

    // Test workbook class
    @BPWorkbook
    public static class TestWorkbook implements BPExcelWorkbook {
        @BPSheet(sheetName = "Sheet1", type = TestData.class, columns = {
            @BPColumn(fieldName = "name", required = true),
            @BPColumn(fieldName = "age", required = true)
        })
        List<TestData> dataList;
        
        public List<TestData> getDataList() { return dataList; }
        public void setDataList(List<TestData> dataList) { this.dataList = dataList; }
    }

    @Test
    public void testBPImportExceptionWithNullFile() {
        try {
            BPImporter<TestWorkbook> importer = new BPImporter<>(TestWorkbook.class, BPOptions.createDefault());
            importer.importExcel((File) null);
            fail("Should throw BPImportException");
        } catch (BPImportException e) {
            assertEquals("BP-IMP-001", e.getErrorCode());
            assertEquals("File cannot be null", e.getMessage());
            assertNull(e.getSheetName());
            assertNull(e.getRowNumber());
            assertNull(e.getColumnNumber());
        }
    }

    @Test
    public void testBPImportExceptionWithNonExistentFile() {
        try {
            BPImporter<TestWorkbook> importer = new BPImporter<>(TestWorkbook.class, BPOptions.createDefault());
            importer.importExcel(new File("/non/existent/file.xlsx"));
            fail("Should throw BPImportException");
        } catch (BPImportException e) {
            assertEquals("BP-IMP-001", e.getErrorCode());
            assertTrue(e.getMessage().contains("File does not exist"));
        }
    }

    @Test
    public void testBPImportExceptionWithNullPath() {
        try {
            BPImporter<TestWorkbook> importer = new BPImporter<>(TestWorkbook.class, BPOptions.createDefault());
            importer.importExcel((String) null);
            fail("Should throw BPImportException");
        } catch (BPImportException e) {
            assertEquals("BP-IMP-001", e.getErrorCode());
            assertEquals("Path cannot be null or empty", e.getMessage());
        }
    }

    @Test
    public void testBPImportExceptionWithEmptyPath() {
        try {
            BPImporter<TestWorkbook> importer = new BPImporter<>(TestWorkbook.class, BPOptions.createDefault());
            importer.importExcel("");
            fail("Should throw BPImportException");
        } catch (BPImportException e) {
            assertEquals("BP-IMP-001", e.getErrorCode());
            assertEquals("Path cannot be null or empty", e.getMessage());
        }
    }

    @Test
    public void testBPImportExceptionWithNullInputStream() {
        try {
            BPImporter<TestWorkbook> importer = new BPImporter<>(TestWorkbook.class, BPOptions.createDefault());
            importer.importExcel((java.io.InputStream) null);
            fail("Should throw BPImportException");
        } catch (BPImportException e) {
            assertEquals("BP-IMP-001", e.getErrorCode());
            assertEquals("Input stream cannot be null", e.getMessage());
        }
    }

    @Test
    public void testBPConfigurationExceptionWithNullWorkbookClass() {
        try {
            BPImporter<TestWorkbook> importer = new BPImporter<>();
            importer.importExcel(new File("nonexistent_test_file.xlsx"));
            fail("Should throw BPImportException");
        } catch (BPImportException e) {
            // Expected exception because file does not exist
            assertTrue(e.getMessage().contains("File does not exist"));
        }
    }

    @Test
    public void testBPImportExceptionWithNullBase64() {
        try {
            BPImporter<TestWorkbook> importer = new BPImporter<>(TestWorkbook.class, BPOptions.createDefault());
            importer.importExcelBase64(null);
            fail("Should throw BPImportException");
        } catch (BPImportException e) {
            assertEquals("BP-IMP-001", e.getErrorCode());
            assertEquals("Base64 string cannot be null or empty", e.getMessage());
        }
    }

    @Test
    public void testBPImportExceptionWithEmptyBase64() {
        try {
            BPImporter<TestWorkbook> importer = new BPImporter<>(TestWorkbook.class, BPOptions.createDefault());
            importer.importExcelBase64("");
            fail("Should throw BPImportException");
        } catch (BPImportException e) {
            assertEquals("BP-IMP-001", e.getErrorCode());
            assertEquals("Base64 string cannot be null or empty", e.getMessage());
        }
    }

    @Test
    public void testBPImportExceptionWithInvalidBase64() {
        try {
            BPImporter<TestWorkbook> importer = new BPImporter<>(TestWorkbook.class, BPOptions.createDefault());
            importer.importExcelBase64("invalid-base64-string!");
            fail("Should throw BPImportException");
        } catch (BPImportException e) {
            assertEquals("BP-IMP-001", e.getErrorCode());
            assertTrue(e.getMessage().contains("Invalid Base64 string format"));
        }
    }

    @Test
    public void testBPValidationExceptionCreation() {
        BPValidationException exception = new BPValidationException("Test validation error");
        assertEquals("BP-VAL-001", exception.getErrorCode());
        assertEquals("Test validation error", exception.getMessage());
        assertFalse(exception.hasValidationErrors());
        assertEquals(0, exception.getValidationErrors().size());
    }

    @Test
    public void testBPValidationExceptionWithErrors() {
        BPValidationException exception = new BPValidationException("Validation failed");
        
        BPValidationException.ValidationError error1 = new BPValidationException.ValidationError(
            "Sheet1", 1, 0, "name", "Field is required", "REQ-001"
        );
        BPValidationException.ValidationError error2 = new BPValidationException.ValidationError(
            "Sheet1", 2, 1, "age", "Invalid age value", "AGE-001"
        );
        
        exception.addValidationError(error1);
        exception.addValidationError(error2);
        
        assertTrue(exception.hasValidationErrors());
        assertEquals(2, exception.getValidationErrors().size());
        assertTrue(exception.getMessage().contains("2 validation error(s) found"));
        
        List<BPValidationException.ValidationError> errors = exception.getValidationErrors();
        assertEquals("Sheet1", errors.get(0).getSheetName());
        assertEquals(Integer.valueOf(1), errors.get(0).getRowNumber());
        assertEquals("name", errors.get(0).getFieldName());
        assertEquals("Field is required", errors.get(0).getErrorMessage());
        assertEquals("REQ-001", errors.get(0).getErrorCode());
    }

    @Test
    public void testBPConfigurationExceptionCreation() {
        BPConfigurationException exception = new BPConfigurationException("Config error", "excelType", "XLS");
        assertEquals("BP-CFG-001", exception.getErrorCode());
        assertEquals("Config error [Key: excelType] [Value: XLS]", exception.getMessage());
        assertEquals("excelType", exception.getConfigurationKey());
        assertEquals("XLS", exception.getConfigurationValue());
    }

    @Test
    public void testExceptionHierarchy() {
        // Test that all exceptions extend BPException
        BPImportException importEx = new BPImportException("Import error");
        BPExportException exportEx = new BPExportException("Export error");
        BPValidationException validationEx = new BPValidationException("Validation error");
        BPConfigurationException configEx = new BPConfigurationException("Config error");
        
        assertTrue(importEx instanceof BPException);
        assertTrue(exportEx instanceof BPException);
        assertTrue(validationEx instanceof BPException);
        assertTrue(configEx instanceof BPException);
        
        // Test error codes
        assertEquals("BP-IMP-001", importEx.getErrorCode());
        assertEquals("BP-EXP-001", exportEx.getErrorCode());
        assertEquals("BP-VAL-001", validationEx.getErrorCode());
        assertEquals("BP-CFG-001", configEx.getErrorCode());
    }
}
