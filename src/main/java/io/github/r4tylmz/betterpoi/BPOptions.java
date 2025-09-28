package io.github.r4tylmz.betterpoi;

import io.github.r4tylmz.betterpoi.enums.ExcelType;

import java.util.Locale;

public final class BPOptions {
    private final ExcelType excelType;
    private final Locale locale;
    private final String bundleName;

    private BPOptions(Builder builder) {
        this.excelType = builder.excelType;
        this.locale = builder.locale;
        this.bundleName = builder.bundleName;
    }

    public static Builder builder() {
        return new Builder();
    }

    /**
     * Creates default options with XLSX format, system default locale, and library properties.
     * 
     * @return default BPOptions instance
     */
    public static BPOptions createDefault() {
        return new Builder()
                .withExcelType(ExcelType.XLSX)
                .withLocale(Locale.getDefault())
                .withBundleName(null) // Use library properties
                .build();
    }

    /**
     * Gets the bundle name for properties files.
     * 
     * @return the bundle name, or null if using library properties
     */
    public String getBundleName() {
        return bundleName;
    }

    /**
     * Gets the Excel file type.
     * 
     * @return the Excel type
     */
    public ExcelType getExcelType() {
        return excelType;
    }

    /**
     * Gets the locale for internationalization.
     * 
     * @return the locale
     */
    public Locale getLocale() {
        return locale;
    }

    /**
     * Checks if custom bundle name is specified.
     * 
     * @return true if custom bundle is specified, false if using library properties
     */
    public boolean hasCustomBundle() {
        return bundleName != null && !bundleName.trim().isEmpty();
    }

    public static class Builder {
        private ExcelType excelType;
        private Locale locale;
        private String bundleName;

        private Builder() {
        }

        /**
         * Builds the BPOptions instance.
         * 
         * @return the configured BPOptions
         * @throws IllegalArgumentException if required fields are not set
         */
        public BPOptions build() {
            if (excelType == null) {
                throw new IllegalArgumentException("ExcelType must not be null");
            }
            if (locale == null) {
                locale = Locale.getDefault();
            }
            // bundleName can be null (use library properties) or a valid name
            return new BPOptions(this);
        }

        /**
         * Sets the bundle name for properties files.
         * 
         * <p><strong>Important:</strong> If you specify a bundle name, the library will:</p>
         * <ul>
         *   <li>First look for your properties files (e.g., myapp_tr.properties, myapp.properties)</li>
         *   <li>Fall back to library properties if a key is not found in your files</li>
         * </ul>
         * 
         * <p><strong>Examples:</strong></p>
         * <ul>
         *   <li>withBundleName("project") → looks for project_tr.properties, project.properties</li>
         *   <li>withBundleName("myapp") → looks for myapp_tr.properties, myapp.properties</li>
         *   <li>withBundleName(null) → uses only library properties (bp_messages.properties)</li>
         * </ul>
         * 
         * @param bundleName the name of your properties bundle (without .properties extension)
         * @return this builder for method chaining
         */
        public Builder withBundleName(String bundleName) {
            this.bundleName = bundleName;
            return this;
        }

        /**
         * Sets the Excel file type.
         * 
         * @param excelType the Excel file type
         * @return this builder for method chaining
         */
        public Builder withExcelType(ExcelType excelType) {
            this.excelType = excelType;
            return this;
        }

        /**
         * Sets the locale for internationalization.
         * 
         * @param locale the locale object
         * @return this builder for method chaining
         */
        public Builder withLocale(Locale locale) {
            this.locale = locale;
            return this;
        }

        /**
         * Sets the locale for internationalization using a string.
         * 
         * @param locale the locale string (e.g., "en", "tr", "fr")
         * @return this builder for method chaining
         */
        public Builder withLocale(String locale) {
            this.locale = new Locale(locale);
            return this;
        }
    }
}