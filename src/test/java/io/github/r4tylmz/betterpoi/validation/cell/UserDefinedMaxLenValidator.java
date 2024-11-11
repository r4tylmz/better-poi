package io.github.r4tylmz.betterpoi.validation.cell;

import io.github.r4tylmz.betterpoi.utils.ColUtil;

public class UserDefinedMaxLenValidator implements CellValidator {
    @Override
    public String validate(CellHolder cellHolder) {
        if (cellHolder == null
                || cellHolder.getBpColumn() == null
                || cellHolder.getCellValue() == null
                || cellHolder.getCellValue().isEmpty()) {
            return null;
        }

        if (cellHolder.getCellValue().length() > 10) {
            return String.format("Column with header [%s] has a max length of 10 characters.", ColUtil.getHeaderTitle(cellHolder.getBpColumn()));
        }
        return null;
    }
}
