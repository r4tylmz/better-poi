package com.github.betterpoi.validation;

import com.github.betterpoi.utils.ColUtil;

public class RequiredValidator implements CellValidator {
    @Override
    public String validate(CellHolder cellHolder) {
        String cellValue = cellHolder.getCellValue();
        if (!cellHolder.getBpColumn().required()) return null;
        if (cellValue == null || cellValue.isEmpty()) return null;
        return String.format("Column %s is required", ColUtil.getHeaderTitle(cellHolder.getBpColumn()));
    }
}
