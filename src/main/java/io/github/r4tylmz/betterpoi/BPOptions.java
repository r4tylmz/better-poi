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

    public static BPOptions createDefault() {
        return new Builder()
                .withExcelType(ExcelType.XLSX)
                .withLocale(Locale.getDefault())
                .withBundleName("messages")
                .build();
    }

    public String getBundleName() {
        return bundleName;
    }

    public ExcelType getExcelType() {
        return excelType;
    }

    public Locale getLocale() {
        return locale;
    }

    public static class Builder {
        private ExcelType excelType;
        private Locale locale;
        private String bundleName;

        private Builder() {
        }

        public BPOptions build() {
            if (excelType == null) {
                throw new IllegalArgumentException("ExcelType must not be null");
            }
            if (locale == null) {
                locale = Locale.getDefault();
            }
            if (bundleName == null || bundleName.isEmpty()) {
                bundleName = "messages";
            }
            return new BPOptions(this);
        }

        public Builder withBundleName(String bundleName) {
            this.bundleName = bundleName;
            return this;
        }

        public Builder withExcelType(ExcelType excelType) {
            this.excelType = excelType;
            return this;
        }

        public Builder withLocale(Locale locale) {
            this.locale = locale;
            return this;
        }

        public Builder withLocale(String locale) {
            this.locale = new Locale(locale);
            return this;
        }
    }
}