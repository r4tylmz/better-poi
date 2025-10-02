package io.github.r4tylmz.betterpoi;

import io.github.r4tylmz.betterpoi.annotation.BPColumn;
import io.github.r4tylmz.betterpoi.annotation.BPSheet;
import io.github.r4tylmz.betterpoi.annotation.BPWorkbook;
import junit.framework.TestCase;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class BPExporterTest extends TestCase {

    private BPExporter bpExporter;
    private File tempFile;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        tempFile = File.createTempFile("test", ".xlsx");
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        if (tempFile != null && tempFile.exists()) {
            tempFile.delete();
        }
    }

    public void testExportToInvalidPath() {
        TestWorkbook workbook = new TestWorkbook();
        bpExporter = new BPExporter(workbook);

        try {
            bpExporter.exportExcel("/invalid/path/file.xlsx");
            fail("Should throw RuntimeException");
        } catch (RuntimeException e) {
            // Expected
        }
    }

    public void testExportToOutputStream() throws Exception {
        TestWorkbook workbook = new TestWorkbook();
        List<TestData> dataList = new ArrayList<>();

        TestData data = new TestData();
        data.setStringField("Test");
        data.setIntField(1);
        data.setDateField(new Date());
        data.setLocalDateField(LocalDate.now());
        data.setLocalDateTimeField(LocalDateTime.now());
        data.setBigDecimalField(new BigDecimal("123.456"));
        data.setDoubleField(123.456);
        data.setFloatField(123.456f);

        dataList.add(data);
        workbook.setData(dataList);

        bpExporter = new BPExporter(workbook);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bpExporter.exportExcel(baos);

        assertTrue(baos.size() > 0);
        baos.close();
    }

    public void testExportToString() throws Exception {
        TestWorkbook workbook = new TestWorkbook();
        List<TestData> dataList = new ArrayList<>();

        TestData data = new TestData();
        data.setStringField("Test");
        data.setIntField(1);
        data.setLocalDateField(LocalDate.now());
        dataList.add(data);
        workbook.setData(dataList);

        bpExporter = new BPExporter(workbook);
        String filePath = tempFile.getAbsolutePath();
        bpExporter.exportExcel(filePath);

        assertTrue(new File(filePath).exists());
    }

    public void testExportWithEmptyData() throws Exception {
        TestWorkbook workbook = new TestWorkbook();
        workbook.setData(new ArrayList<>());

        bpExporter = new BPExporter(workbook);
        bpExporter.exportExcel(tempFile);

        FileInputStream fis = new FileInputStream(tempFile);
        Workbook wb = new XSSFWorkbook(fis);
        Sheet sheet = wb.getSheetAt(0);

        assertEquals(1, sheet.getPhysicalNumberOfRows());

        wb.close();
        fis.close();
    }

    public void testExportWithNullValues() throws Exception {
        TestWorkbook workbook = new TestWorkbook();
        List<TestData> dataList = new ArrayList<>();

        TestData data = new TestData();
        dataList.add(data);
        workbook.setData(dataList);

        bpExporter = new BPExporter(workbook);
        bpExporter.exportExcel(tempFile);

        FileInputStream fis = new FileInputStream(tempFile);
        Workbook wb = new XSSFWorkbook(fis);
        Sheet sheet = wb.getSheetAt(0);

        Row dataRow = sheet.getRow(1);
        assertTrue(dataRow.getCell(0).getStringCellValue().isEmpty());
        assertEquals(0, (int) dataRow.getCell(1).getNumericCellValue());
        assertNull(dataRow.getCell(2).getDateCellValue());
        assertNull(dataRow.getCell(3).getDateCellValue()); // LocalDate
        assertNull(dataRow.getCell(4).getDateCellValue()); // LocalDateTime
        assertEquals(0.0, dataRow.getCell(5).getNumericCellValue(), 0.001); // BigDecimal
        assertEquals(0.0, dataRow.getCell(6).getNumericCellValue(), 0.001); // Double
        assertEquals(0.0, dataRow.getCell(7).getNumericCellValue(), 0.001); // Float

        wb.close();
        fis.close();
    }

    public void testExportWithNullWorkbook() {
        BPExporter exporter = new BPExporter(null);
        try {
            exporter.exportExcel(tempFile.getAbsolutePath());
            fail("Should throw RuntimeException");
        } catch (RuntimeException e) {
            // Expected exception because workbook is null
            assertTrue(true);
        }
    }

    public void testExportWithValidData() throws Exception {
        TestWorkbook workbook = new TestWorkbook();
        List<TestData> dataList = new ArrayList<>();

        TestData data1 = new TestData();
        data1.setStringField("Test1");
        data1.setIntField(1);
        data1.setDateField(new Date());
        data1.setLocalDateField(LocalDate.now());
        data1.setLocalDateTimeField(LocalDateTime.now());
        data1.setBigDecimalField(new BigDecimal("123.456"));
        data1.setDoubleField(123.456);
        data1.setFloatField(123.456f);

        TestData data2 = new TestData();
        data2.setStringField("Test2");
        data2.setIntField(2);
        data2.setDateField(new Date());
        data2.setLocalDateField(LocalDate.now().plusDays(1));
        data2.setLocalDateTimeField(LocalDateTime.now().plusHours(1));
        data2.setBigDecimalField(new BigDecimal("456.789"));
        data2.setDoubleField(456.789);
        data2.setFloatField(456.789f);

        dataList.addAll(Arrays.asList(data1, data2));
        workbook.setData(dataList);

        bpExporter = new BPExporter(workbook);
        bpExporter.exportExcel(tempFile);

        FileInputStream fis = new FileInputStream(tempFile);
        Workbook wb = new XSSFWorkbook(fis);
        Sheet sheet = wb.getSheetAt(0);

        // Verify header row
        Row headerRow = sheet.getRow(0);
        assertEquals("String Column", headerRow.getCell(0).getStringCellValue());
        assertEquals("Int Column", headerRow.getCell(1).getStringCellValue());
        assertEquals("Date Column", headerRow.getCell(2).getStringCellValue());
        assertEquals("LocalDate Column", headerRow.getCell(3).getStringCellValue());
        assertEquals("LocalDateTime Column", headerRow.getCell(4).getStringCellValue());
        assertEquals("BigDecimal Column", headerRow.getCell(5).getStringCellValue());
        assertEquals("Double Column", headerRow.getCell(6).getStringCellValue());
        assertEquals("Float Column", headerRow.getCell(7).getStringCellValue());

        // Verify data rows
        Row dataRow1 = sheet.getRow(1);
        assertEquals("Test1", dataRow1.getCell(0).getStringCellValue());
        assertEquals(1, (int) dataRow1.getCell(1).getNumericCellValue());
        assertTrue(dataRow1.getCell(2).getDateCellValue() instanceof Date);
        assertNotNull(dataRow1.getCell(3).getDateCellValue()); // LocalDate
        assertNotNull(dataRow1.getCell(4).getDateCellValue()); // LocalDateTime
        assertEquals(123.456, dataRow1.getCell(5).getNumericCellValue(), 0.001);
        assertEquals(123.456, dataRow1.getCell(6).getNumericCellValue(), 0.001);
        assertEquals(123.456f, (float) dataRow1.getCell(7).getNumericCellValue(), 0.001);

        wb.close();
        fis.close();
    }

    @BPWorkbook
    public static class TestWorkbook {
        @BPSheet(sheetName = "Test Sheet", columns = {
                @BPColumn(fieldName = "stringField", headerTitle = "String Column"),
                @BPColumn(fieldName = "intField", headerTitle = "Int Column"),
                @BPColumn(fieldName = "dateField", headerTitle = "Date Column"),
                @BPColumn(fieldName = "localDateField", headerTitle = "LocalDate Column"),
                @BPColumn(fieldName = "localDateTimeField", headerTitle = "LocalDateTime Column"),
                @BPColumn(fieldName = "bigDecimalField", headerTitle = "BigDecimal Column"),
                @BPColumn(fieldName = "doubleField", headerTitle = "Double Column"),
                @BPColumn(fieldName = "floatField", headerTitle = "Float Column")
        }, type = TestData.class)
        private List<TestData> data;

        public List<TestData> getData() {
            return data;
        }

        public void setData(List<TestData> data) {
            this.data = data;
        }
    }

    public static class TestData {
        private String stringField;
        private Integer intField;
        private Date dateField;
        private LocalDate localDateField;
        private LocalDateTime localDateTimeField;
        private BigDecimal bigDecimalField;
        private Double doubleField;
        private Float floatField;

        public BigDecimal getBigDecimalField() {
            return bigDecimalField;
        }

        public void setBigDecimalField(BigDecimal bigDecimalField) {
            this.bigDecimalField = bigDecimalField;
        }

        public Date getDateField() {
            return dateField;
        }

        public void setDateField(Date dateField) {
            this.dateField = dateField;
        }

        public Double getDoubleField() {
            return doubleField;
        }

        public void setDoubleField(Double doubleField) {
            this.doubleField = doubleField;
        }

        public Float getFloatField() {
            return floatField;
        }

        public void setFloatField(Float floatField) {
            this.floatField = floatField;
        }

        public Integer getIntField() {
            return intField;
        }

        public void setIntField(Integer intField) {
            this.intField = intField;
        }

        public LocalDate getLocalDateField() {
            return localDateField;
        }

        public void setLocalDateField(LocalDate localDateField) {
            this.localDateField = localDateField;
        }

        public LocalDateTime getLocalDateTimeField() {
            return localDateTimeField;
        }

        public void setLocalDateTimeField(LocalDateTime localDateTimeField) {
            this.localDateTimeField = localDateTimeField;
        }

        public String getStringField() {
            return stringField;
        }

        public void setStringField(String stringField) {
            this.stringField = stringField;
        }
    }
}