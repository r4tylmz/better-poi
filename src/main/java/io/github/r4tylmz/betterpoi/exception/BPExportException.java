package io.github.r4tylmz.betterpoi.exception;

/**
 * Exception thrown when there's an error during Excel export operations.
 */
public class BPExportException extends BPException {
    
    private final String sheetName;
    private final String fieldName;
    
    public BPExportException(String message) {
        super("BP-EXP-001", message);
        this.sheetName = null;
        this.fieldName = null;
    }
    
    public BPExportException(String message, Throwable cause) {
        super("BP-EXP-001", message, cause);
        this.sheetName = null;
        this.fieldName = null;
    }
    
    public BPExportException(String message, String sheetName, String fieldName) {
        super("BP-EXP-001", message);
        this.sheetName = sheetName;
        this.fieldName = fieldName;
    }
    
    public BPExportException(String message, String sheetName, String fieldName, Throwable cause) {
        super("BP-EXP-001", message, cause);
        this.sheetName = sheetName;
        this.fieldName = fieldName;
    }
    
    public String getSheetName() {
        return sheetName;
    }
    
    public String getFieldName() {
        return fieldName;
    }
    
    @Override
    public String getMessage() {
        StringBuilder sb = new StringBuilder(super.getMessage());
        if (sheetName != null) {
            sb.append(" [Sheet: ").append(sheetName).append("]");
        }
        if (fieldName != null) {
            sb.append(" [Field: ").append(fieldName).append("]");
        }
        return sb.toString();
    }
}
