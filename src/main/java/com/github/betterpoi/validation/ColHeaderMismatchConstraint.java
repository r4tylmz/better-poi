package com.github.betterpoi.validation;

import com.github.betterpoi.annotation.BPColumn;
import com.github.betterpoi.annotation.BPSheet;
import com.github.betterpoi.constraint.ColConstraint;
import com.github.betterpoi.utils.ColUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ColHeaderMismatchConstraint implements ColConstraint {

    private List<String> getColHeaders(Sheet sheet) {
        final List<String> colHeaders = new ArrayList<>();
        final Row headerRow = sheet.getRow(0);
        for (int i = 0; i < headerRow.getLastCellNum(); i++) {
            colHeaders.add(headerRow.getCell(i).getStringCellValue());
        }
        return colHeaders;
    }

    @Override
    public Map<Integer, String> validate(Sheet sheet, BPSheet bpSheet) {
        final List<String> colHeaders = getColHeaders(sheet);
        Row headerRow = sheet.getRow(0);
        Map<Integer, String> violationMap = new HashMap<>();
        for (int colNo = 0; colNo < bpSheet.columns().length; colNo++) {
            BPColumn bpColumn = bpSheet.columns()[colNo];
            String expectedHeader = ColUtil.getHeaderTitle(bpColumn);
            if (!headerRow.getCell(colNo).getStringCellValue().equals(expectedHeader)) {
                String violation = String.format("Expected header: %s but not found in headers %s", expectedHeader, colHeaders);
                violationMap.put(colNo, violation);
            }
        }
        return violationMap;
    }
}
