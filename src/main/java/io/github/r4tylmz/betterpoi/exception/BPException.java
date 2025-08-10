package io.github.r4tylmz.betterpoi.exception;

/**
 * Base exception class for Better POI library.
 * All exceptions thrown by this library extend this class.
 */
public class BPException extends RuntimeException {
    
    private final String errorCode;
    
    public BPException(String message) {
        super(message);
        this.errorCode = "BP-001";
    }
    
    public BPException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = "BP-001";
    }
    
    public BPException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
    
    public BPException(String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }
    
    public String getErrorCode() {
        return errorCode;
    }
}
