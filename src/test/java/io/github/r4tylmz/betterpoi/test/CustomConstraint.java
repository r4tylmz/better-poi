package io.github.r4tylmz.betterpoi.test;

import io.github.r4tylmz.betterpoi.constraint.Constraint;
import io.github.r4tylmz.betterpoi.i18n.MessageSourceService;
import org.apache.poi.ss.usermodel.Cell;

public class CustomConstraint implements Constraint {

    private MessageSourceService messageSourceService;

    @Override
    public void setMessageSourceService(MessageSourceService messageSourceService) {
        this.messageSourceService = messageSourceService;
    }

    @Override
    public String validate(Cell cell) {
        return messageSourceService.getMessage("error.custom.constraint");
    }
}
