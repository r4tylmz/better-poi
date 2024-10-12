package com.r4tylmz.betterpoi.validation.cell;

import com.r4tylmz.betterpoi.utils.ColUtil;

/**
 * Validator implementation that checks if a cell value is required.
 * This class validates that the cell value is not null or empty if the BPColumn annotation specifies it as required.
 */
public class RequiredValidator implements CellValidator {
    /**
     * Validates the given cell holder.
     * Checks if the cell value is not null or empty if the BPColumn annotation specifies it as required.
     *
     * @param cellHolder the cell holder containing the cell and its metadata
     * @return a validation error message if the cell value is required and is null or empty, otherwise null
     */
    @Override
    public String validate(CellHolder cellHolder) {
        String cellValue = cellHolder.getCellValue();
        if (!cellHolder.getBpColumn().required()) return null;
        if (cellValue != null && !cellValue.isEmpty()) return null;
        return String.format("The column with the header [%s] is required and cannot be null or empty.", ColUtil.getHeaderTitle(cellHolder.getBpColumn()));
    }
}