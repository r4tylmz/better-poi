# Better Poi

> Still in development and not ready for production use.

> Did not release to Maven Central Repository yet.

## Introduction

Better Poi is an extension for Apache POI, a powerful library used for reading and writing Microsoft Office documents.
This extension is designed to simplify the usage of Apache POI for handling Excel files.

## Features

- Simplifies reading Excel files with just one line of code.
- Supports reading both XLS and XLSX files.
- Supports custom validation for Excel files.
- Allows custom constraints for Excel cells, columns and rows.
- Provides pattern validation for Excel cells.

## TO DO List

- [ ] Add exception handling
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
class (`RowConstraint` for rows, `ColConstraint` for columns, and `UserDefinedValidator` for cells).

```java

@BPWorkbook
public class TestWorkBook implements BPExcelWorkbook {

    @BPSheet(sheetName = "Sheet1",
            colValidators = {DefaultConstraint.class},
            rowValidators = {DefaultConstraint.class},
            type = TestExcel.class, columns = {
            @BPColumn(fieldName = "col1", headerTitle = "Column 1", cellValidator = DefaultConstraint.class),
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

Read the Excel and get the corresponding workbook class.

```java
public class Test {
    public static void main(String[] args) {
        final BPImporter<TestWorkBook> bpImporter = new BPImporter<>(TestWorkBook.class, ExcelType.XLSX);
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