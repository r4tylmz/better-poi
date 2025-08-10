package io.github.r4tylmz.betterpoi.exception;

/**
 * Exception thrown when there's a configuration error in the Better POI library.
 */
public class BPConfigurationException extends BPException {
    
    private final String configurationKey;
    private final String configurationValue;
    
    public BPConfigurationException(String message) {
        super("BP-CFG-001", message);
        this.configurationKey = null;
        this.configurationValue = null;
    }
    
    public BPConfigurationException(String message, Throwable cause) {
        super("BP-CFG-001", message, cause);
        this.configurationKey = null;
        this.configurationValue = null;
    }
    
    public BPConfigurationException(String message, String configurationKey) {
        super("BP-CFG-001", message);
        this.configurationKey = configurationKey;
        this.configurationValue = null;
    }
    
    public BPConfigurationException(String message, String configurationKey, String configurationValue) {
        super("BP-CFG-001", message);
        this.configurationKey = configurationKey;
        this.configurationValue = configurationValue;
    }
    
    public BPConfigurationException(String message, String configurationKey, String configurationValue, Throwable cause) {
        super("BP-CFG-001", message, cause);
        this.configurationKey = configurationKey;
        this.configurationValue = configurationValue;
    }
    
    public String getConfigurationKey() {
        return configurationKey;
    }
    
    public String getConfigurationValue() {
        return configurationValue;
    }
    
    @Override
    public String getMessage() {
        StringBuilder sb = new StringBuilder(super.getMessage());
        if (configurationKey != null) {
            sb.append(" [Key: ").append(configurationKey).append("]");
        }
        if (configurationValue != null) {
            sb.append(" [Value: ").append(configurationValue).append("]");
        }
        return sb.toString();
    }
}
