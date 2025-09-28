package io.github.r4tylmz.betterpoi.utils;

import io.github.r4tylmz.betterpoi.annotation.BPColumn;
import io.github.r4tylmz.betterpoi.i18n.MessageSourceService;

/**
 * Utility class for column-related operations.
 */
public class ColUtil {

    /**
     * Retrieves the header title for the given BPColumn.
     * If the header title is empty, it returns the field name instead.
     *
     * @param column the BPColumn annotation containing column metadata
     * @return the header title if present, otherwise the field name
     */
    public static String getHeaderTitle(BPColumn column) {
        return column.headerTitle().isEmpty() ? column.fieldName() : column.headerTitle();
    }

    /**
     * Retrieves the header title for the given BPColumn with i18n support.
     * If the header title is a key that exists in the properties file, it returns the localized value.
     * If the key doesn't exist in properties, it falls back to using the key as the header title.
     * If the header title is empty, it returns the field name instead.
     *
     * @param column the BPColumn annotation containing column metadata
     * @param messageSourceService the message source service for i18n lookup
     * @return the localized header title if key exists in properties, otherwise the header title or field name
     */
    public static String getHeaderTitle(BPColumn column, MessageSourceService messageSourceService) {
        String headerTitle = column.headerTitle();
        
        // If headerTitle is empty, return fieldName
        if (headerTitle.isEmpty()) {
            return column.fieldName();
        }
        
        // If messageSourceService is null, return headerTitle as-is
        if (messageSourceService == null) {
            return headerTitle;
        }
        
        // Check if headerTitle exists as a key in properties
        if (messageSourceService.hasMessage(headerTitle)) {
            return messageSourceService.getMessage(headerTitle);
        }
        
        // Fallback: use headerTitle as-is (the key becomes the header)
        return headerTitle;
    }
}