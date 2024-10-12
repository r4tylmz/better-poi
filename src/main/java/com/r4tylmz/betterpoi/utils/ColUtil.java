package com.r4tylmz.betterpoi.utils;

import com.r4tylmz.betterpoi.annotation.BPColumn;

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
}