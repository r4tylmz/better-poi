package io.github.r4tylmz.betterpoi.validation.cell;

import io.github.r4tylmz.betterpoi.i18n.MessageSourceService;
import io.github.r4tylmz.betterpoi.utils.ColUtil;

public class UserDefinedMaxLenValidator implements CellValidator {
    private MessageSourceService messageSourceService;

    public UserDefinedMaxLenValidator() {
        // Default constructor
    }

    public UserDefinedMaxLenValidator(MessageSourceService messageSourceService) {
        this.messageSourceService = messageSourceService;
    }

    @Override
    public void setMessageSourceService(MessageSourceService messageSourceService) {
        this.messageSourceService = messageSourceService;
    }

    @Override
    public String validate(CellHolder cellHolder) {
        if (cellHolder == null
                || cellHolder.getBpColumn() == null
                || cellHolder.getCellValue() == null
                || cellHolder.getCellValue().isEmpty()) {
            return null;
        }

        if (cellHolder.getCellValue().length() > 10) {
            return this.messageSourceService.getMessage("error.userdefined.violation", ColUtil.getHeaderTitle(cellHolder.getBpColumn()));
        }
        return null;
    }
}
