package io.github.r4tylmz.betterpoi.exception;

/**
 * Exception thrown when there's an error during Excel import operations.
 */
public class BPImportException extends BPException {
    
    private final String sheetName;
    private final Integer rowNumber;
    private final Integer columnNumber;
    
    public BPImportException(String message) {
        super("BP-IMP-001", message);
        this.sheetName = null;
        this.rowNumber = null;
        this.columnNumber = null;
    }
    
    public BPImportException(String message, Throwable cause) {
        super("BP-IMP-001", message, cause);
        this.sheetName = null;
        this.rowNumber = null;
        this.columnNumber = null;
    }
    
    public BPImportException(String message, String sheetName, Integer rowNumber, Integer columnNumber) {
        super("BP-IMP-001", message);
        this.sheetName = sheetName;
        this.rowNumber = rowNumber;
        this.columnNumber = columnNumber;
    }
    
    public BPImportException(String message, String sheetName, Integer rowNumber, Integer columnNumber, Throwable cause) {
        super("BP-IMP-001", message, cause);
        this.sheetName = sheetName;
        this.rowNumber = rowNumber;
        this.columnNumber = columnNumber;
    }
    
    public String getSheetName() {
        return sheetName;
    }
    
    public Integer getRowNumber() {
        return rowNumber;
    }
    
    public Integer getColumnNumber() {
        return columnNumber;
    }
    
    @Override
    public String getMessage() {
        StringBuilder sb = new StringBuilder(super.getMessage());
        if (sheetName != null) {
            sb.append(" [Sheet: ").append(sheetName).append("]");
        }
        if (rowNumber != null) {
            sb.append(" [Row: ").append(rowNumber).append("]");
        }
        if (columnNumber != null) {
            sb.append(" [Column: ").append(columnNumber).append("]");
        }
        return sb.toString();
    }
}
