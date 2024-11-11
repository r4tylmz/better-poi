package io.github.r4tylmz.betterpoi.validation.cell;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Validator implementation that checks if a cell value matches a specified pattern.
 * This class uses regular expressions to validate the cell value against the pattern defined in the BPColumn annotation.
 */
public class PatternValidator implements CellValidator {
    /**
     * Validates the given cell holder.
     * Checks if the cell value matches the pattern specified in the BPColumn annotation.
     *
     * @param cellHolder the cell holder containing the cell and its metadata
     * @return a validation error message if the cell value does not match the pattern, otherwise null
     */
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