package com.r4tylmz.betterpoi.utils;

import com.r4tylmz.betterpoi.annotation.BPColumn;

public class ColUtil {
    public static String getHeaderTitle(BPColumn column) {
        return column.headerTitle().isEmpty() ? column.fieldName() : column.headerTitle();
    }
}
