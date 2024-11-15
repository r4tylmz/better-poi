package io.github.r4tylmz.betterpoi.utils;

import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;

/**
 * Utility class for row operations.
 */
public class RowUtil {
    public static final DataFormatter dataFormatter = new DataFormatter();

    /**
     * Checks if a row is empty.
     *
     * @param row the row to check
     * @return true if the row is empty, false otherwise
     */
    public static boolean isRowEmpty(Row row) {
        if (row == null) {
            return true;
        }
        for (int i = row.getFirstCellNum(); i < row.getLastCellNum(); i++) {
            if (!dataFormatter.formatCellValue(row.getCell(i)).trim().isEmpty()) {
                return false;
            }
        }
        return true;
    }
}
