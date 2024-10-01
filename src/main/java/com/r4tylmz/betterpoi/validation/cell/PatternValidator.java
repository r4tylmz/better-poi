package com.r4tylmz.betterpoi.validation.cell;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PatternValidator implements CellValidator {
    @Override
    public String validate(CellHolder cellHolder) {
        final String cellValue = cellHolder.getCellValue();
        final String pattern = cellHolder.getBpColumn().pattern();
        if (cellValue == null || pattern == null || pattern.isEmpty()) {
            return null;
        }
        final Pattern validatorPattern = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);
        final Matcher matcher = validatorPattern.matcher(cellValue);
        if (!matcher.find()) {
            return String.format("Cell value [%s] is not valid. Cell value must match with the pattern : %s", cellValue, pattern);
        }
        return null;
    }
}
