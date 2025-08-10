package io.github.r4tylmz.betterpoi.exception;

import java.util.List;
import java.util.ArrayList;

/**
 * Exception thrown when there's a validation error during Excel processing.
 */
public class BPValidationException extends BPException {
    
    private final List<ValidationError> validationErrors;
    
    public BPValidationException(String message) {
        super("BP-VAL-001", message);
        this.validationErrors = new ArrayList<>();
    }
    
    public BPValidationException(String message, List<ValidationError> validationErrors) {
        super("BP-VAL-001", message);
        this.validationErrors = validationErrors != null ? validationErrors : new ArrayList<>();
    }
    
    public BPValidationException(String message, List<ValidationError> validationErrors, Throwable cause) {
        super("BP-VAL-001", message, cause);
        this.validationErrors = validationErrors != null ? validationErrors : new ArrayList<>();
    }
    
    public List<ValidationError> getValidationErrors() {
        return new ArrayList<>(validationErrors);
    }
    
    public void addValidationError(ValidationError error) {
        if (error != null) {
            this.validationErrors.add(error);
        }
    }
    
    public boolean hasValidationErrors() {
        return !validationErrors.isEmpty();
    }
    
    @Override
    public String getMessage() {
        StringBuilder sb = new StringBuilder(super.getMessage());
        if (!validationErrors.isEmpty()) {
            sb.append(" - ").append(validationErrors.size()).append(" validation error(s) found");
        }
        return sb.toString();
    }
    
    /**
     * Inner class to represent individual validation errors.
     */
    public static class ValidationError {
        private final String sheetName;
        private final Integer rowNumber;
        private final Integer columnNumber;
        private final String fieldName;
        private final String errorMessage;
        private final String errorCode;
        
        public ValidationError(String sheetName, Integer rowNumber, Integer columnNumber, 
                             String fieldName, String errorMessage, String errorCode) {
            this.sheetName = sheetName;
            this.rowNumber = rowNumber;
            this.columnNumber = columnNumber;
            this.fieldName = fieldName;
            this.errorMessage = errorMessage;
            this.errorCode = errorCode;
        }
        
        // Getters
        public String getSheetName() { return sheetName; }
        public Integer getRowNumber() { return rowNumber; }
        public Integer getColumnNumber() { return columnNumber; }
        public String getFieldName() { return fieldName; }
        public String getErrorMessage() { return errorMessage; }
        public String getErrorCode() { return errorCode; }
        
        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            if (sheetName != null) sb.append("Sheet: ").append(sheetName).append(", ");
            if (rowNumber != null) sb.append("Row: ").append(rowNumber).append(", ");
            if (columnNumber != null) sb.append("Column: ").append(columnNumber).append(", ");
            if (fieldName != null) sb.append("Field: ").append(fieldName).append(", ");
            sb.append("Error: ").append(errorMessage);
            if (errorCode != null) sb.append(" [").append(errorCode).append("]");
            return sb.toString();
        }
    }
}
