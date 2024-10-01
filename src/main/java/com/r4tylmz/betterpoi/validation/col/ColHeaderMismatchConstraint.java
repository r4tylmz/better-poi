package com.r4tylmz.betterpoi.validation.col;

import com.r4tylmz.betterpoi.annotation.BPColumn;
import com.r4tylmz.betterpoi.annotation.BPSheet;
import com.r4tylmz.betterpoi.constraint.ColConstraint;
import com.r4tylmz.betterpoi.utils.ColUtil;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import java.util.*;

public class ColHeaderMismatchConstraint implements ColConstraint {
    private final List<String> colHeaders = new ArrayList<>();
    private final DataFormatter dataFormatter = new DataFormatter();

    private List<String> getColHeaders(Sheet sheet) {
        if (!colHeaders.isEmpty()) {
            return Collections.unmodifiableList(colHeaders);
        }

        final Row headerRow = sheet.getRow(0);
        if (headerRow == null) {
            throw new IllegalArgumentException("Sheet has no header row.");
        }

        for (int i = 0; i < headerRow.getLastCellNum(); i++) {
            Cell cell = headerRow.getCell(i);
            if (cell != null) {
                colHeaders.add(dataFormatter.formatCellValue(cell));
            }
        }
        return Collections.unmodifiableList(colHeaders);
    }

    @Override
    public Map<Integer, String> validate(Sheet sheet, BPSheet bpSheet) {
        final List<String> colHeaders = getColHeaders(sheet);
        Row headerRow = sheet.getRow(0);
        if (headerRow == null) {
            throw new IllegalArgumentException("Sheet has no header row.");
        }

        Map<Integer, String> violationMap = new HashMap<>();

        for (int colNo = 0; colNo < bpSheet.columns().length; colNo++) {
            BPColumn bpColumn = bpSheet.columns()[colNo];
            String expectedHeader = ColUtil.getHeaderTitle(bpColumn);

            Cell cell = headerRow.getCell(colNo);
            String actualHeader = Optional.ofNullable(cell)
                    .map(dataFormatter::formatCellValue)
                    .orElse("");

            if (!actualHeader.equals(expectedHeader)) {
                String violation = String.format("Expected header: %s but not found in headers %s", expectedHeader, colHeaders);
                violationMap.put(colNo, violation);
            }
        }
        return violationMap;
    }
}
