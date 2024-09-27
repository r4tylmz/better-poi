package com.github.betterpoi.validation;

public class RequiredValidator implements CellValidator {
    @Override
    public String validate(CellHolder cellHolder) {
        String cellValue = cellHolder.getCellValue();
        if (!cellHolder.getBpColumn().required()) return null;
        if (cellValue == null || cellValue.isEmpty()) return null;
        return "Field is required";
    }
}
