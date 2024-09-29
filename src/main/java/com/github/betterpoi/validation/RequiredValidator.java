package com.github.betterpoi.validation;

import com.github.betterpoi.utils.ColUtil;

public class RequiredValidator implements CellValidator {
    @Override
    public String validate(CellHolder cellHolder) {
        String cellValue = cellHolder.getCellValue();
        if (!cellHolder.getBpColumn().required()) return null;
        if (cellValue != null && !cellValue.isEmpty()) return null;
        return String.format("The column with the header [%s] is required and cannot be null or empty.", ColUtil.getHeaderTitle(cellHolder.getBpColumn()));
    }
}
