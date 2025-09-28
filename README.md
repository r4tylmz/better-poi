# Better POI

A Java library for Excel file operations using Apache POI with enhanced validation, internationalization, and error handling capabilities.

![Maven Central Version](https://img.shields.io/maven-central/v/io.github.r4tylmz/better-poi)
![GitHub](https://img.shields.io/github/license/r4tylmz/better-poi)
![GitHub issues](https://img.shields.io/github/issues/r4tylmz/better-poi)
![GitHub pull requests](https://img.shields.io/github/issues-pr/r4tylmz/better-poi)

## Features

- Excel import/export support for XLS and XLSX formats
- Advanced validation system with custom constraints
- Internationalization support with user customization
- Flexible error handling with optional exception throwing
- Custom validation rules and constraints
- UTF-8 encoding support
- Dual properties system with fallback mechanism

## Installation

### Maven

```xml
<dependency>
    <groupId>io.github.r4tylmz</groupId>
    <artifactId>better-poi</artifactId>
    <version>1.0.8</version>
</dependency>
```

## Quick Start

### Basic Usage

```java
// Configure options
BPOptions options = BPOptions.builder()
        .withExcelType(ExcelType.XLSX)
        .withLocale("en")
        .build();

// Import Excel file
BPImporter<TestWorkBook> importer = new BPImporter<>(TestWorkBook.class, options);
TestWorkBook workbook = importer.importExcel("test.xlsx");

// Check validation status
if (importer.hasValidationErrors()) {
    List<String> errors = importer.getErrorMessageList();
    // Handle validation errors
}

// Export Excel file
BPExporter exporter = new BPExporter(workbook);
exporter.exportExcel("output.xlsx");
```

## Core Components

### BPImporter

Handles Excel file import operations with validation support.

```java
BPImporter<TestWorkBook> importer = new BPImporter<>(TestWorkBook.class, options);

// Import methods
TestWorkBook workbook = importer.importExcel("file.xlsx");
TestWorkBook workbook = importer.importExcel(new File("file.xlsx"));
TestWorkBook workbook = importer.importExcel(inputStream);
TestWorkBook workbook = importer.importExcelBase64(base64String);

// Validation methods
boolean hasErrors = importer.hasValidationErrors();
boolean isValid = importer.isValidationSuccessful();
List<String> errors = importer.getErrorMessageList();

// Optional exception throwing
importer.throwValidationExceptionIfErrors();
```

### BPExporter

Manages Excel file export operations.

```java
BPExporter exporter = new BPExporter(workbook);

// Export methods
exporter.exportExcel("output.xlsx");
exporter.exportExcel(new File("output.xlsx"));
exporter.exportExcel(outputStream);
```

### BPOptions

Configuration options for library operations.

```java
BPOptions options = BPOptions.builder()
        .withExcelType(ExcelType.XLSX)
        .withLocale("tr")
        .withBundleName("myapp")  // Optional: custom properties
        .build();
```

## Annotations

### @BPWorkbook

Marks a class as an Excel workbook. The class must implement the `BPExcelWorkbook` interface.

```java
@BPWorkbook
public class TestWorkBook implements BPExcelWorkbook {
    // Workbook implementation
}
```

### @BPSheet

Defines a sheet in the workbook with validation and column configuration.

```java
@BPSheet(
    sheetName = "Sheet1",
    type = TestExcel.class,
    colValidators = {DefaultConstraint.class},
    rowValidators = {DefaultConstraint.class},
    columns = {
        @BPColumn(fieldName = "col1", headerTitle = "Column 1", cellValidators = {DefaultConstraint.class}),
        @BPColumn(fieldName = "col2", headerTitle = "Column 2", required = true),
        @BPColumn(fieldName = "col3", headerTitle = "Column 3"),
        @BPColumn(fieldName = "col4", headerTitle = "Column 4", pattern = "^[a-zA-Z0-9]*$"),
        @BPColumn(fieldName = "col5", headerTitle = "Column 5"),
        @BPColumn(fieldName = "col6", headerTitle = "Column 6")
    }
)
private List<TestExcel> testExcelList;
```

### @BPColumn

Defines column properties and validation rules.

```java
@BPColumn(
    fieldName = "col1",
    headerTitle = "Column 1",
    required = true,
    pattern = "^[a-zA-Z0-9]*$",
    cellValidators = {DefaultConstraint.class}
)
private String col1;
```

## Validation System

### Built-in Validators

- **RequiredValidator**: Ensures required fields are not null or empty
- **PatternValidator**: Validates cell values against regex patterns
- **UserDefinedMaxLenValidator**: Customizable maximum length validation

### Custom Validators

Implement custom validation by extending `CellValidator`:

```java
public class CustomEmailValidator extends CellValidator {
    @Override
    public String validate(CellHolder cellHolder) {
        String value = cellHolder.getCellValue();
        if (value != null && !value.contains("@")) {
            return "Invalid email format";
        }
        return null; // No error
    }
}
```

### Constraints

- **Row Constraints**: Validate entire rows (e.g., duplicate detection)
- **Column Constraints**: Validate column headers and structure
- **Cell Constraints**: Validate individual cell values

## Internationalization

### Dual Properties Support

The library supports both built-in library properties and user-defined properties with intelligent fallback.

#### Properties Resolution Order

1. User localized properties (e.g., `project_tr.properties`)
2. User default properties (e.g., `project.properties`)
3. Library localized properties (e.g., `bp_messages_tr.properties`)
4. Library default properties (e.g., `bp_messages.properties`)

#### Usage Examples

**Library Properties Only (Default)**
```java
BPOptions options = BPOptions.builder()
        .withExcelType(ExcelType.XLSX)
        .withLocale("tr")
        .build();
```

**Custom Properties with Fallback**
```java
BPOptions options = BPOptions.builder()
        .withExcelType(ExcelType.XLSX)
        .withLocale("tr")
        .withBundleName("project")
        .build();
```

#### Properties File Structure

**Library Properties (Built-in)**
```
src/main/resources/
├── bp_messages.properties          # English (default)
└── bp_messages_tr.properties      # Turkish
```

**Custom Properties**
```
src/main/resources/
├── project.properties          # Default messages
├── project_tr.properties      # Turkish messages
├── project_fr.properties      # French messages
└── project_de.properties      # German messages
```

## Error Handling

### Non-Exception Validation (Recommended)

By default, the importer completes the import process even when validation fails, collecting all errors for review.

#### Benefits

- Business logic continues regardless of validation errors
- All validation errors are collected and accessible
- Flexible error handling based on business requirements
- Better user experience with comprehensive error reporting

#### Available Methods

```java
// Check validation status
boolean hasErrors = bpImporter.hasValidationErrors();
boolean isValid = bpImporter.isValidationSuccessful();

// Access error messages
List<String> errors = bpImporter.getErrorMessageList();

// Optional exception throwing
bpImporter.throwValidationExceptionIfErrors();
```

#### Example Usage

```java
BPImporter<TestWorkBook> bpImporter = new BPImporter<>(TestWorkBook.class, options);
TestWorkBook workbook = bpImporter.importExcel("/path/to/file.xlsx");

if (bpImporter.isValidationSuccessful()) {
    System.out.println("Import successful - no validation errors");
    // Process the workbook normally
} else {
    System.err.println("Import completed with validation errors:");
    
    List<String> errors = bpImporter.getErrorMessageList();
    for (String error : errors) {
        System.err.println("  - " + error);
    }
    
    // Handle based on business logic
    if (errors.size() <= 5) {
        System.out.println("Minor validation issues - proceeding with import");
    } else {
        System.err.println("Too many validation errors - import rejected");
    }
}
```

### Exception-Based Error Handling (Optional)

Traditional exception handling is still supported:

```java
try {
    BPImporter<TestWorkBook> bpImporter = new BPImporter<>(TestWorkBook.class, options);
    TestWorkBook workbook = bpImporter.importExcel("/path/to/file.xlsx");
    
    if (bpImporter.hasValidationErrors()) {
        List<String> errors = bpImporter.getErrorMessageList();
        for (String error : errors) {
            System.err.println("  - " + error);
        }
        
        // Handle errors gracefully or throw exception
        bpImporter.throwValidationExceptionIfErrors();
    }
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
}
```

## Exception Types

- **BPException**: Base exception class for all Better POI exceptions
- **BPImportException**: Thrown during import operations with detailed error information
- **BPExportException**: Thrown during export operations with field and sheet information
- **BPValidationException**: Thrown when validation fails with comprehensive error details
- **BPConfigurationException**: Thrown for configuration errors with key-value information

## Complete Example

### TestExcel Model

```java
public class TestExcel {
    private String col1;
    private String col2;
    private Double col3;
    private String col4;
    private String col5;
    private String col6;
    
    // Getters and setters
}
```

### TestWorkBook Implementation

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
    private List<TestExcel> testExcelList;
    
    // Getters and setters
}
```

### Import with Validation

```java
// Configure options with custom properties
BPOptions options = BPOptions.builder()
        .withExcelType(ExcelType.XLSX)
        .withLocale("tr")
        .withBundleName("myapp")
        .build();

// Create importer
BPImporter<TestWorkBook> importer = new BPImporter<>(TestWorkBook.class, options);

// Import Excel file
TestWorkBook workbook = importer.importExcel("test.xlsx");

// Check validation status
if (importer.hasValidationErrors()) {
    System.err.println("Validation errors found:");
    List<String> errors = importer.getErrorMessageList();
    for (String error : errors) {
        System.err.println("  - " + error);
    }
    
    // Handle based on business logic
    if (errors.size() <= 5) {
        System.out.println("Minor validation issues - proceeding with import");
    } else {
        System.err.println("Too many validation errors - import rejected");
    }
} else {
    System.out.println("Import successful - no validation errors");
}

// Process the workbook
List<TestExcel> testExcelList = workbook.getTestExcelList();
System.out.println("Imported " + testExcelList.size() + " records");
```

### Export

```java
// Export to Excel file
BPExporter exporter = new BPExporter(workbook);
exporter.exportExcel("output_test.xlsx");
```

## Migration Guide

### From Library Properties Only

**Before**
```java
BPOptions options = BPOptions.builder()
        .withExcelType(ExcelType.XLSX)
        .withLocale("tr")
        .withBundleName("bp_messages")
        .build();
```

**After**
```java
BPOptions options = BPOptions.builder()
        .withExcelType(ExcelType.XLSX)
        .withLocale("tr")
        .withBundleName("myapp")     // Uses your properties first
        .build();
```

**Or continue using library properties**
```java
BPOptions options = BPOptions.builder()
        .withExcelType(ExcelType.XLSX)
        .withLocale("tr")
        // No withBundleName() - uses library properties
        .build();
```

## TODO List

- [x] Add exception handling
- [ ] Add multi thread support for large files
- [ ] Add support for CSV files
- [ ] Add column data type constraint
- [ ] Add error cell highlighting
- [ ] Add support for inserting Excel files into an existing Excel file

## Contributing

Contributions are welcome. Please submit a Pull Request.

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.