# Better Poi

> Still in development and not ready for production use.

![Maven Central Version](https://img.shields.io/maven-central/v/io.github.r4tylmz/better-poi)
![GitHub](https://img.shields.io/github/license/r4tylmz/better-poi)
![GitHub issues](https://img.shields.io/github/issues/r4tylmz/better-poi)
![GitHub pull requests](https://img.shields.io/github/issues-pr/r4tylmz/better-poi)

## Installation

```xml
<dependency>
    <groupId>io.github.r4tylmz</groupId>
    <artifactId>better-poi</artifactId>
    <version>1.0.7</version>
</dependency>
```

## Introduction

Better Poi is an extension for Apache POI, a powerful library used for reading and writing Microsoft Office documents.
This extension is designed to simplify the usage of Apache POI for handling Excel files.

## Features

- Simplifies reading Excel files with just one line of code.
- Supports reading both XLS and XLSX files.
- Supports custom validation for Excel files.
- Allows custom constraints for Excel cells, columns and rows.
- Provides pattern validation for Excel cells.
- **NEW**: Internationalization (i18n) support with UTF-8 encoding.
- **NEW**: Configurable options through BPOptions class.
- **NEW**: Localized validation error messages in multiple languages.
- **NEW**: Comprehensive exception handling with detailed error information.

## TO DO List

- [x] Add exception handling
- [ ] Add multi thread support for large files
- [ ] Add support for CSV files
- [ ] Add column data type constraint
- [ ] Add error cell highlighting
- [ ] Add support for inserting Excel files into an existing Excel file

## Usage

Define a class to represent the Excel file.

```java
public class TestExcel {
    private String col1;
    private String col2;
    private Double col3;
    private String col4;
    private String col5;
    private String col6;

    // Getters and setters
    // ...
}
```

Define a Workbook class, annotate it with `@BPWorkbook`, and implement the `BPExcelWorkbook` interface.
```java
@BPWorkbook
public class TestWorkBook implements BPExcelWorkbook {

    @BPSheet(sheetName = "Sheet1",
            type = TestExcel.class, columns = {
            @BPColumn(fieldName = "col1"),
            @BPColumn(fieldName = "col2", required = true),
            @BPColumn(fieldName = "col3"),
            @BPColumn(fieldName = "col4"),
            @BPColumn(fieldName = "col5"),
            @BPColumn(fieldName = "col6"),
    })
    List<TestExcel> testExcelList;
    // Getters and setters
    // ...
}
```

You can use predefined constraints for Excel cells, rows, and columns, or create your own by extending the relevant
class (`RowConstraint` for rows, `ColConstraint` for columns, and `CellValidator` for cells).

```java
@BPWorkbook
public class TestWorkBook implements BPExcelWorkbook {

    @BPSheet(sheetName = "Sheet1",
            colValidators = {DefaultConstraint.class},
            rowValidators = {DefaultConstraint.class},
            type = TestExcel.class, columns = {
            @BPColumn(fieldName = "col1", headerTitle = "Column 1", cellValidators = {DefaultConstraint.class}),
            @BPColumn(fieldName = "col2", headerTitle = "Column 2", required = true),
            @BPColumn(fieldName = "col3", headerTitle = "Column 3"),
            @BPColumn(fieldName = "col4", headerTitle = "Column 4", pattern = "^[a-zA-Z0-9]*$"),
            @BPColumn(fieldName = "col5", headerTitle = "Column 5"),
            @BPColumn(fieldName = "col6", headerTitle = "Column 6"),
    })
    List<TestExcel> testExcelList;
    // Getters and setters
    // ...
}
```

### Reading Excel Files

**NEW**: Use BPOptions for configuration and localization support.

```java
public class Test {
    public static void main(String[] args) {
        // Create options with default settings
        BPOptions options = BPOptions.createDefault();
        
        // Or customize options
        BPOptions customOptions = BPOptions.builder()
                .withExcelType(ExcelType.XLSX)
                .withLocale("tr") // Turkish locale
                .withBundleName("messages")
                .build();
        
        final BPImporter<TestWorkBook> bpImporter = new BPImporter<>(TestWorkBook.class, customOptions);
        final InputStream inputStream = Files.newInputStream(new File("/your_source/file.xlsx").toPath());
        final TestWorkBook workbook = bpImporter.importExcel(inputStream);

        // Alternatively, you can use a File or String Path to import the Excel file:
        // final TestWorkBook workbook = bpImporter.importExcel(new File("/your_source/file.xlsx"));
        // final TestWorkBook workbook = bpImporter.importExcel("/your_source/file.xlsx");

        // Excel is now ready to be used as a Java object.
        List<TestExcel> testExcelList = workbook.getTestExcelList();
    }
}
```

### Exception Handling

**NEW**: The library now provides comprehensive exception handling with detailed error information and proper error codes.

#### Exception Types

- **BPException**: Base exception class for all Better POI exceptions
- **BPImportException**: Thrown during import operations with sheet, row, and column information
- **BPExportException**: Thrown during export operations with sheet and field information
- **BPValidationException**: Thrown when validation fails with detailed error information
- **BPConfigurationException**: Thrown for configuration errors

#### Example: Handling Import Exceptions

```java
try {
    final BPImporter<TestWorkBook> bpImporter = new BPImporter<>(TestWorkBook.class, options);
    final TestWorkBook workbook = bpImporter.importExcel("/path/to/file.xlsx");
    // Process workbook...
} catch (BPImportException e) {
    System.err.println("Import failed: " + e.getMessage());
    System.err.println("Error Code: " + e.getErrorCode());
    if (e.getSheetName() != null) {
        System.err.println("Sheet: " + e.getSheetName());
    }
    if (e.getRowNumber() != null) {
        System.err.println("Row: " + e.getRowNumber());
    }
    if (e.getColumnNumber() != null) {
        System.err.println("Column: " + e.getColumnNumber());
    }
} catch (BPValidationException e) {
    System.err.println("Validation failed: " + e.getMessage());
    for (BPValidationException.ValidationError error : e.getValidationErrors()) {
        System.err.println("  - " + error.toString());
    }
} catch (BPConfigurationException e) {
    System.err.println("Configuration error: " + e.getMessage());
    System.err.println("Key: " + e.getConfigurationKey());
    System.err.println("Value: " + e.getConfigurationValue());
}
```

#### Example: Handling Export Exceptions

```java
try {
    final BPExporter bpExporter = new BPExporter(workbook);
    bpExporter.exportExcel("/path/to/output.xlsx");
} catch (BPExportException e) {
    System.err.println("Export failed: " + e.getMessage());
    System.err.println("Error Code: " + e.getErrorCode());
    if (e.getSheetName() != null) {
        System.err.println("Sheet: " + e.getSheetName());
    }
    if (e.getFieldName() != null) {
        System.err.println("Field: " + e.getFieldName());
    }
} catch (BPConfigurationException e) {
    System.err.println("Configuration error: " + e.getMessage());
}
```

### Localization Support

**NEW**: The library now supports internationalization with UTF-8 encoding. Error messages are automatically localized based on the locale specified in BPOptions.

```java
// English locale (default)
BPOptions englishOptions = BPOptions.builder()
        .withExcelType(ExcelType.XLSX)
        .withLocale("en")
        .build();

// Turkish locale
BPOptions turkishOptions = BPOptions.builder()
        .withExcelType(ExcelType.XLSX)
        .withLocale("tr")
        .build();

// Custom bundle name
BPOptions customBundleOptions = BPOptions.builder()
        .withExcelType(ExcelType.XLSX)
        .withLocale("en")
        .withBundleName("custom-messages")
        .build();
```

### Export Excel Files

Export Excel file from a list of objects.

```java
public class Test {
    public static void main(String[] args) {
        final TestWorkBook workbook = new TestWorkBook();
        // Assume that you have a list of TestExcel objects
        workbook.setTestExcelList(new ArrayList<>());
        final BPExporter bpExporter = new BPExporter(workbook);
        bpExporter.exportExcel(new File("/your_destination/file.xlsx"));
    }
}
```