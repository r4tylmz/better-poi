package io.github.r4tylmz.betterpoi;

import io.github.r4tylmz.betterpoi.annotation.BPColumn;
import io.github.r4tylmz.betterpoi.annotation.BPSheet;
import io.github.r4tylmz.betterpoi.annotation.BPWorkbook;
import io.github.r4tylmz.betterpoi.constraint.DefaultConstraint;
import io.github.r4tylmz.betterpoi.i18n.MessageSourceService;
import io.github.r4tylmz.betterpoi.validation.cell.CellHolder;
import io.github.r4tylmz.betterpoi.validation.cell.CellValidator;
import junit.framework.TestCase;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.File;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.when;

public class BPMetadataHandlerTest extends TestCase {
    private BPMetadataHandler metadataHandler;

    @Mock
    private BPColumn mockColumn;

    @Mock
    private BPSheet mockSheet;

    private File tempFile;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        MockitoAnnotations.initMocks(this);
        metadataHandler = new BPMetadataHandler(ValidWorkbook.class);
        tempFile = File.createTempFile("test", ".xlsx");
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        if (tempFile != null && tempFile.exists()) {
            tempFile.delete();
        }
    }

    // Annotation Property Tests
    public void testColumnAnnotationProperties() {
        BPSheet sheet = metadataHandler.getSheets().get(0);
        BPColumn[] columns = sheet.columns();

        // Test first column (String)
        BPColumn stringColumn = columns[0];
        assertEquals("stringField", stringColumn.fieldName());
        assertEquals("String Field", stringColumn.headerTitle());
        assertTrue(stringColumn.required());
        assertEquals("^[A-Za-z]*$", stringColumn.pattern());
        assertEquals("", stringColumn.datePattern());
        assertEquals(DefaultConstraint.class, stringColumn.cellValidators()[0]);

        // Test date column
        BPColumn dateColumn = columns[3];
        assertEquals("dateField", dateColumn.fieldName());
        assertEquals("Date Field", dateColumn.headerTitle());
        assertEquals("yyyy-MM-dd", dateColumn.datePattern());
    }

    public void testConstructorWithInvalidWorkbook() {
        try {
            new BPMetadataHandler(InvalidWorkbook.class);
            fail("Should throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertEquals("workbookClass must be annotated with @BPWorkbook", e.getMessage());
        }
    }

    // Constructor Tests
    public void testConstructorWithNullClass() {
        try {
            new BPMetadataHandler((Class<?>) null);
            fail("Should throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertEquals("workbookClass can't be null", e.getMessage());
        }
    }

    public void testConstructorWithNullObject() {
        try {
            new BPMetadataHandler((Object) null);
            fail("Should throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertEquals("workbook can't be null", e.getMessage());
        }
    }

    public void testConstructorWithValidObject() {
        ValidWorkbook workbook = new ValidWorkbook();
        BPMetadataHandler handler = new BPMetadataHandler(workbook);
        assertNotNull(handler);
        assertEquals(2, handler.getSheets().size());
    }

    // Custom Validator Test
    public void testCustomCellValidator() {
        class CustomCellValidator implements CellValidator {

            @Override
            public void setMessageSourceService(MessageSourceService messageSourceService) {
                return;
            }

            @Override
            public String validate(CellHolder cellHolder) {
                return "";
            }
        }

        when(mockColumn.cellValidators()).thenReturn(new Class[]{CustomCellValidator.class});
        assertEquals(CustomCellValidator.class, mockColumn.cellValidators()[0]);
    }

    // Mock Tests for Default Values
    public void testDefaultValues() {
        when(mockColumn.fieldName()).thenReturn("testField");
        when(mockColumn.headerTitle()).thenReturn("");
        when(mockColumn.required()).thenReturn(false);
        when(mockColumn.pattern()).thenReturn("");
        when(mockColumn.datePattern()).thenReturn("");
        when(mockColumn.cellValidators()).thenReturn(new Class[]{DefaultConstraint.class});

        assertEquals("", mockColumn.headerTitle());
        assertFalse(mockColumn.required());
        assertEquals("", mockColumn.pattern());
        assertEquals("", mockColumn.datePattern());
        assertEquals(DefaultConstraint.class, mockColumn.cellValidators()[0]);
    }

    // Column Type Tests
    public void testGetColumnTypes() {
        BPSheet sheet = metadataHandler.getSheets().get(0);
        Map<String, Class<?>> columnTypes = metadataHandler.getColumnTypes(sheet);

        assertEquals(5, columnTypes.size());
        assertEquals(String.class, columnTypes.get("stringField"));
        assertEquals(Integer.class, columnTypes.get("intField"));
        assertEquals(BigDecimal.class, columnTypes.get("bigDecimalField"));
        assertEquals(LocalDate.class, columnTypes.get("dateField"));
        assertEquals(LocalDateTime.class, columnTypes.get("localDateTimeField"));
    }

    public void testGetColumnTypesWithMock() {
        when(mockSheet.type()).thenReturn((Class) TestData.class);
        when(mockSheet.columns()).thenReturn(new BPColumn[]{mockColumn});
        when(mockColumn.fieldName()).thenReturn("stringField");

        Map<String, Class<?>> columnTypes = metadataHandler.getColumnTypes(mockSheet);
        assertEquals(String.class, columnTypes.get("stringField"));
    }

    // Data Fields Tests
    public void testGetDataFields() {
        BPSheet sheet = metadataHandler.getSheets().get(0);
        Map<String, Field> fields = metadataHandler.getDataFields(sheet);

        assertEquals(5, fields.size());
        assertTrue(fields.containsKey("stringField"));
        assertTrue(fields.containsKey("intField"));
        assertTrue(fields.containsKey("bigDecimalField"));
        assertTrue(fields.containsKey("dateField"));
        assertTrue(fields.containsKey("localDateTimeField"));
    }

    public void testGetDataFieldsWithMock() {
        when(mockSheet.type()).thenReturn((Class) TestData.class);
        when(mockSheet.columns()).thenReturn(new BPColumn[]{mockColumn});
        when(mockColumn.fieldName()).thenReturn("stringField");

        Map<String, Field> fields = metadataHandler.getDataFields(mockSheet);
        assertNotNull(fields.get("stringField"));
    }

    // Field Tests
    public void testGetField() {
        BPSheet sheet = metadataHandler.getSheets().get(0);
        Field field = metadataHandler.getField(sheet);

        assertNotNull(field);
        assertEquals("data1", field.getName());
    }

    // Sheet Tests
    public void testGetSheets() {
        List<BPSheet> sheets = metadataHandler.getSheets();
        assertEquals(2, sheets.size());
        assertEquals("Sheet1", sheets.get(0).sheetName());
        assertEquals("Sheet2", sheets.get(1).sheetName());
    }

    // Values Tests
    public void testGetValues() {
        ValidWorkbook workbook = new ValidWorkbook();
        List<TestData> testDataList = new ArrayList<>();

        TestData testData = new TestData();
        testData.setStringField("test");
        testData.setIntField(123);
        testData.setBigDecimalField(new BigDecimal("123.45"));
        testData.setDateField(LocalDate.now());
        testData.setLocalDateTimeField(LocalDateTime.now());

        testDataList.add(testData);
        workbook.setData1(testDataList);

        BPSheet sheet = metadataHandler.getSheets().get(0);
        List<?> values = metadataHandler.getValues(workbook, sheet);

        assertEquals(1, values.size());
        TestData resultData = (TestData) values.get(0);
        assertEquals("test", resultData.getStringField());
        assertEquals(Integer.valueOf(123), resultData.getIntField());
        assertEquals(new BigDecimal("123.45"), resultData.getBigDecimalField());
        assertNotNull(resultData.getDateField());
        assertNotNull(resultData.getLocalDateTimeField());
    }

    public void testGetValuesWithInvalidType() {
        BPMetadataHandler handler = new BPMetadataHandler(InvalidTypeWorkbook.class);
        InvalidTypeWorkbook workbook = new InvalidTypeWorkbook();

        try {
            handler.getValues(workbook, handler.getSheets().get(0));
            fail("Should throw IllegalStateException");
        } catch (IllegalStateException e) {
            assertTrue(e.getMessage().contains("Expected " + List.class.getCanonicalName()));
        }
    }

    // Invalid Field Test
    public void testInvalidFieldPattern() {
        when(mockColumn.fieldName()).thenReturn("nonexistentField");
        when(mockColumn.pattern()).thenReturn("[invalid pattern");
        when(mockSheet.columns()).thenReturn(new BPColumn[]{mockColumn});
        when(mockSheet.type()).thenReturn((Class) TestData.class);

        try {
            metadataHandler.getDataFields(mockSheet);
            fail("Should throw RuntimeException for invalid field");
        } catch (RuntimeException e) {
            // Expected
        }
    }

    // Test Workbook Class
    @BPWorkbook
    public static class ValidWorkbook {
        @BPSheet(sheetName = "Sheet1",
                columns = {
                        @BPColumn(
                                fieldName = "stringField",
                                headerTitle = "String Field",
                                required = true,
                                pattern = "^[A-Za-z]*$",
                                datePattern = "",
                                cellValidators = {DefaultConstraint.class}
                        ),
                        @BPColumn(
                                fieldName = "intField",
                                headerTitle = "Int Field",
                                required = true,
                                pattern = "^\\d+$"
                        ),
                        @BPColumn(
                                fieldName = "bigDecimalField",
                                headerTitle = "BigDecimal Field",
                                pattern = "^\\d+\\.\\d{2}$"
                        ),
                        @BPColumn(
                                fieldName = "dateField",
                                headerTitle = "Date Field",
                                datePattern = "yyyy-MM-dd"
                        ),
                        @BPColumn(
                                fieldName = "localDateTimeField",
                                headerTitle = "DateTime Field",
                                datePattern = "yyyy-MM-dd HH:mm:ss"
                        )
                },
                type = TestData.class)
        private List<TestData> data1;

        @BPSheet(sheetName = "Sheet2",
                columns = {
                        @BPColumn(
                                fieldName = "doubleField",
                                headerTitle = "Double Field",
                                pattern = "^\\d+\\.\\d+$"
                        ),
                        @BPColumn(
                                fieldName = "booleanField",
                                headerTitle = "Boolean Field"
                        )
                },
                type = TestData.class)
        private List<TestData> data2;

        public List<TestData> getData1() {
            return data1;
        }

        public void setData1(List<TestData> data) {
            this.data1 = data;
        }

        public List<TestData> getData2() {
            return data2;
        }

        public void setData2(List<TestData> data) {
            this.data2 = data;
        }
    }

    // Test invalid workbook (without annotation)
    public static class InvalidWorkbook {
        @BPSheet(sheetName = "Sheet1", columns = {}, type = TestData.class)
        private List<TestData> data;
    }

    // Test Data Class
    public static class TestData {
        private String stringField;
        private Integer intField;
        private BigDecimal bigDecimalField;
        private LocalDate dateField;
        private LocalDateTime localDateTimeField;
        private Double doubleField;
        private Boolean booleanField;

        public BigDecimal getBigDecimalField() {
            return bigDecimalField;
        }

        public void setBigDecimalField(BigDecimal bigDecimalField) {
            this.bigDecimalField = bigDecimalField;
        }

        public Boolean getBooleanField() {
            return booleanField;
        }

        public void setBooleanField(Boolean booleanField) {
            this.booleanField = booleanField;
        }

        public LocalDate getDateField() {
            return dateField;
        }

        public void setDateField(LocalDate dateField) {
            this.dateField = dateField;
        }

        public Double getDoubleField() {
            return doubleField;
        }

        public void setDoubleField(Double doubleField) {
            this.doubleField = doubleField;
        }

        public Integer getIntField() {
            return intField;
        }

        public void setIntField(Integer intField) {
            this.intField = intField;
        }

        public LocalDateTime getLocalDateTimeField() {
            return localDateTimeField;
        }

        public void setLocalDateTimeField(LocalDateTime localDateTimeField) {
            this.localDateTimeField = localDateTimeField;
        }

        // Getters and Setters
        public String getStringField() {
            return stringField;
        }

        public void setStringField(String stringField) {
            this.stringField = stringField;
        }
    }

    @BPWorkbook
    public static class InvalidTypeWorkbook {
        @BPSheet(sheetName = "Sheet1", columns = {
                @BPColumn(fieldName = "testField", headerTitle = "Test Field")
        }, type = TestData.class)
        private String invalidTypeField = "not a list";  // Should be List<TestData>

        public String getInvalidTypeField() {
            return invalidTypeField;
        }

        public void setInvalidTypeField(String invalidTypeField) {
            this.invalidTypeField = invalidTypeField;
        }
    }
}