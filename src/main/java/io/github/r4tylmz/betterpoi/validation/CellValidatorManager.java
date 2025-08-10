package io.github.r4tylmz.betterpoi.validation;

import io.github.r4tylmz.betterpoi.BPFormatter;
import io.github.r4tylmz.betterpoi.annotation.BPColumn;
import io.github.r4tylmz.betterpoi.constraint.ConstraintFactory;
import io.github.r4tylmz.betterpoi.i18n.MessageSourceService;
import io.github.r4tylmz.betterpoi.utils.ColUtil;
import io.github.r4tylmz.betterpoi.validation.cell.CellHolder;
import io.github.r4tylmz.betterpoi.validation.cell.CellValidator;
import io.github.r4tylmz.betterpoi.validation.cell.PatternValidator;
import io.github.r4tylmz.betterpoi.validation.cell.RequiredValidator;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Manager class to handle cell validation.
 * This class aggregates multiple cell validators and applies them to cells to check for validation errors.
 */
public class CellValidatorManager {
    private final List<CellValidator> cellValidators = new ArrayList<>();
    private final BPFormatter formatter;
    private final MessageSourceService messageSourceService;
    /**
     * Constructor to initialize the CellValidatorManager with a formatter.
     * Adds default validators to the list.
     *
     * @param formatter the formatter to use for cell value formatting
     * @param messageSourceService the service for retrieving localized messages
     */
    public CellValidatorManager(BPFormatter formatter, MessageSourceService messageSourceService) {
        this.formatter = formatter;
        this.messageSourceService = messageSourceService;
        cellValidators.add(new RequiredValidator(messageSourceService));
        cellValidators.add(new PatternValidator(messageSourceService));
    }

    /**
     * Retrieves the formatted value of a cell.
     *
     * @param cell the cell to format
     * @return the formatted cell value as a string
     */
    public String getValue(Cell cell) {
        final DataFormatter dataFormatter = new DataFormatter();
        return dataFormatter.formatCellValue(cell).trim();
    }

    /**
     * Runs all cell validators on the specified cell.
     * Collects and returns any validation error messages.
     *
     * @param cell the cell to validate
     * @param bpColumn the BPColumn annotation containing metadata for the cell
     * @param field the field in the class corresponding to the cell
     * @return a set of validation error messages, if any
     */
    public Set<String> validate(Cell cell, BPColumn bpColumn, Field field) {
        final Set<String> violations = new HashSet<>();
        final String value = getValue(cell);
        this.cellValidators.addAll(ConstraintFactory.getInstance(messageSourceService).getCellValidators(bpColumn.cellValidators()));
        for (CellValidator cellValidator : cellValidators) {
            final CellHolder cellHolder = new CellHolder(cell, value, field, bpColumn);
            final String errorMessage = cellValidator.validate(cellHolder);
            if (errorMessage != null) {
                String violation = messageSourceService.getMessage("error.row.column.violation", ColUtil.getHeaderTitle(bpColumn), errorMessage);
                violations.add(violation);
            }
        }
        return violations;
    }
}