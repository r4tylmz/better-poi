package io.github.r4tylmz.betterpoi.converters;

import org.apache.commons.beanutils.Converter;

import java.time.LocalDateTime;

public class LocalDateTimeConverter implements Converter {
    @Override
    public <T> T convert(Class<T> type, Object value) {
        if (value == null) {
            return null;
        }

        if (value instanceof LocalDateTime) {
            return (T) value;
        }

        if (value instanceof java.util.Date) {
            return (T) ((java.util.Date) value).toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime();
        }
        return null;
    }
}
