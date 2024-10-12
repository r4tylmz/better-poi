package com.r4tylmz.betterpoi.utils;

import org.apache.poi.ss.usermodel.Workbook;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

import static org.junit.Assert.*;

public class ExcelUtilsTest {

    @Test
    public void convertXlsToXlsxWithEmptyInput() {
        InputStream inputStream = new ByteArrayInputStream(new byte[0]);
        try {
            ExcelUtils.convertXlsToXlsx(inputStream);
            fail("Expected IOException");
        } catch (IOException e) {
            assertEquals("Error converting .xls to .xlsx", e.getMessage());
        }
    }

    @Test
    public void convertXlsToXlsxWithInvalidInput() {
        InputStream inputStream = new ByteArrayInputStream("invalid content".getBytes());
        try {
            ExcelUtils.convertXlsToXlsx(inputStream);
            fail("Expected IOException");
        } catch (IOException e) {
            assertEquals("Error converting .xls to .xlsx", e.getMessage());
        }
    }

    @Test
    public void convertXlsToXlsxWithValidInput() throws IOException {
        InputStream inputStream = new ByteArrayInputStream(createValidXlsContent());
        Workbook workbook = ExcelUtils.convertXlsToXlsx(inputStream);
        assertNotNull(workbook);
        assertEquals(1, workbook.getNumberOfSheets());
    }

    private byte[] createValidXlsContent() throws IOException {
        return Files.readAllBytes(new File("src/test/resources/sample_xls.xls").toPath());
    }
}