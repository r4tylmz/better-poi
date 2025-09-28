package io.github.r4tylmz.betterpoi.i18n;

import io.github.r4tylmz.betterpoi.BPExporter;
import io.github.r4tylmz.betterpoi.BPOptions;
import io.github.r4tylmz.betterpoi.annotation.BPColumn;
import io.github.r4tylmz.betterpoi.annotation.BPSheet;
import io.github.r4tylmz.betterpoi.annotation.BPWorkbook;
import io.github.r4tylmz.betterpoi.utils.ColUtil;
import junit.framework.TestCase;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class I18nHeaderTest extends TestCase {

    public static class Employee {
        private String name;
        private String email;
        private Integer age;
        private String department;
        private String address;

        // Constructors and getters
        public Employee() {}

        public Employee(String name, String email, Integer age, String department, String address) {
            this.name = name;
            this.email = email;
            this.age = age;
            this.department = department;
            this.address = address;
        }

        // Getters
        public String getName() { return name; }
        public String getEmail() { return email; }
        public Integer getAge() { return age; }
        public String getDepartment() { return department; }
        public String getAddress() { return address; }
    }

    @BPWorkbook
    public static class EmployeeWorkbook {
        @BPSheet(sheetName = "Employees", 
                type = Employee.class,
                columns = {
                    @BPColumn(fieldName = "name", headerTitle = "header.name"),
                    @BPColumn(fieldName = "email", headerTitle = "header.email"),
                    @BPColumn(fieldName = "age", headerTitle = "header.age"),
                    @BPColumn(fieldName = "department", headerTitle = "nonexistent.key"),
                    @BPColumn(fieldName = "address", headerTitle = "Regular Header")
                })
        private List<Employee> employees;
        
        public List<Employee> getEmployees() { return employees; }
        public void setEmployees(List<Employee> employees) { this.employees = employees; }
    }

    public void testI18nHeaderTitles() throws Exception {
        // Save original locale and set to English
        Locale originalLocale = Locale.getDefault();
        Locale.setDefault(Locale.ENGLISH);
        
        try {
            // Debug: Check system locale
            System.out.println("System default locale: " + Locale.getDefault());
            System.out.println("English locale: " + Locale.ENGLISH);
            
            // Test with English locale - use test properties bundle
            MessageSourceService messageSourceService = new MessageSourceService(Locale.ENGLISH, "test");
            
            // Debug: Check which locale is being used
            System.out.println("MessageSourceService locale: " + messageSourceService.getLocale());
            System.out.println("Bundle count: " + messageSourceService.getBundleCount());
            
            // Test direct ResourceBundle behavior
            java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("test", Locale.ENGLISH);
            System.out.println("Direct ResourceBundle for 'header.name': " + bundle.getString("header.name"));
            
            // Create mock BPColumn annotations for testing
            BPColumn nameColumn = EmployeeWorkbook.class.getDeclaredField("employees")
                .getAnnotation(BPSheet.class).columns()[0];
            String nameHeader = ColUtil.getHeaderTitle(nameColumn, messageSourceService);
            System.out.println("Actual header for 'header.name': " + nameHeader);
            assertEquals("Name", nameHeader);

            // Test non-existing key (should fallback to key itself)
            BPColumn deptColumn = EmployeeWorkbook.class.getDeclaredField("employees")
                .getAnnotation(BPSheet.class).columns()[3];
            String deptHeader = ColUtil.getHeaderTitle(deptColumn, messageSourceService);
            assertEquals("nonexistent.key", deptHeader);

            // Test regular header (not a key)
            BPColumn addressColumn = EmployeeWorkbook.class.getDeclaredField("employees")
                .getAnnotation(BPSheet.class).columns()[4];
            String addressHeader = ColUtil.getHeaderTitle(addressColumn, messageSourceService);
            assertEquals("Regular Header", addressHeader);
        } finally {
            // Restore original locale
            Locale.setDefault(originalLocale);
        }
    }

    public void testI18nHeaderTitlesWithTurkish() throws Exception {
        // Test with Turkish locale
        MessageSourceService messageSourceService = new MessageSourceService(new Locale("tr"), "test");
        
        // Test existing key in Turkish
        BPColumn nameColumn = EmployeeWorkbook.class.getDeclaredField("employees")
            .getAnnotation(BPSheet.class).columns()[0];
        String nameHeader = ColUtil.getHeaderTitle(nameColumn, messageSourceService);
        assertEquals("İsim", nameHeader);

        BPColumn emailColumn = EmployeeWorkbook.class.getDeclaredField("employees")
            .getAnnotation(BPSheet.class).columns()[1];
        String emailHeader = ColUtil.getHeaderTitle(emailColumn, messageSourceService);
        assertEquals("E-posta Adresi", emailHeader);
    }

    public void testExportWithI18nHeaders() throws Exception {
        List<Employee> employees = Arrays.asList(
            new Employee("John Doe", "john@example.com", 30, "IT", "123 Main St"),
            new Employee("Jane Smith", "jane@example.com", 25, "HR", "456 Oak Ave")
        );

        // Test export with English locale
        BPOptions options = BPOptions.builder()
            .withExcelType(io.github.r4tylmz.betterpoi.enums.ExcelType.XLSX)
            .withLocale(Locale.ENGLISH)
            .withBundleName("test")
            .build();

        EmployeeWorkbook workbook = new EmployeeWorkbook();
        workbook.setEmployees(employees);
        BPExporter exporter = new BPExporter(workbook, options);
        
        // Export to byte array using OutputStream
        java.io.ByteArrayOutputStream outputStream = new java.io.ByteArrayOutputStream();
        exporter.exportExcel(outputStream);
        byte[] excelData = outputStream.toByteArray();
        
        // Verify that Excel data is generated (basic check)
        assertTrue(excelData.length > 0);
        
        // Test export with Turkish locale
        BPOptions turkishOptions = BPOptions.builder()
            .withExcelType(io.github.r4tylmz.betterpoi.enums.ExcelType.XLSX)
            .withLocale(new Locale("tr"))
            .withBundleName("test")
            .build();

        BPExporter turkishExporter = new BPExporter(workbook, turkishOptions);
        java.io.ByteArrayOutputStream turkishOutputStream = new java.io.ByteArrayOutputStream();
        turkishExporter.exportExcel(turkishOutputStream);
        byte[] turkishExcelData = turkishOutputStream.toByteArray();
        
        // Verify that Excel data is generated (basic check)
        assertTrue(turkishExcelData.length > 0);
    }
}