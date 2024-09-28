package com.github.betterpoi.utils;

import com.github.betterpoi.annotation.BPColumn;

public class ColUtil {
    public static String getHeaderTitle(BPColumn column) {
        return column.headerTitle().isEmpty() ? column.filedName() : column.headerTitle();
    }
}
