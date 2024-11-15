package io.github.r4tylmz.betterpoi.validation.row;

import io.github.r4tylmz.betterpoi.annotation.BPSheet;
import io.github.r4tylmz.betterpoi.constraint.RowConstraint;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class DuplicateRowConstraint implements RowConstraint {
    DataFormatter dataFormatter = new DataFormatter();

    private String hashRow(Row row, int colSize) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < colSize; i++) {
            Cell cell = row.getCell(i);
            if (cell != null) {
                sb.append(dataFormatter.formatCellValue(cell));
                sb.append("###;###");
            }
        }
        return sb.toString();
    }

    @Override
    public Map<Integer, String> validate(Sheet sheet, BPSheet bpSheet) {
        Set<String> rowSet = new HashSet<>();
        Map<Integer, String> rowViolationMap = new HashMap<>();
        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row != null && rowSet.contains(hashRow(row, bpSheet.columns().length))) {
                rowViolationMap.put(i, "Duplicate row found");
            } else {
                rowSet.add(hashRow(row, bpSheet.columns().length));
            }
        }
        return rowViolationMap;
    }
}
