package io.github.r4tylmz.betterpoi.validation.cell;

import io.github.r4tylmz.betterpoi.i18n.MessageSourceService;
import io.github.r4tylmz.betterpoi.utils.ColUtil;

/**
 * Validator implementation that checks if a cell value is required.
 * This class validates that the cell value is not null or empty if the BPColumn annotation specifies it as required.
 */
public class RequiredValidator implements CellValidator {
    private MessageSourceService messageSourceService;

    public RequiredValidator() {
        // Default constructor
    }

    public RequiredValidator(MessageSourceService messageSourceService) {
        this.messageSourceService = messageSourceService;
    }

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
        return messageSourceService.getMessage("required.validation.error", ColUtil.getHeaderTitle(cellHolder.getBpColumn()));
    }
}