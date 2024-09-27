package com.github.betterpoi.validation;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PatternValidator implements CellValidator {
    @Override
    public String validate(CellHolder cellHolder) {
        final String cellValue = cellHolder.getCellValue();
        if (cellValue == null) {
            return null;
        }
        final String pattern = cellHolder.getBpColumn().pattern();
        final Pattern validatorPattern = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);
        final Matcher matcher = validatorPattern.matcher(cellValue);
        if (!matcher.find()) {
            return String.format("Cell value is not valid. Cell value must match with the pattern : %s", pattern);
        }
        return null;
    }
}
