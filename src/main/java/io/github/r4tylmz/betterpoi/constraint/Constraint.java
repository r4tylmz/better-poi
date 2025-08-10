package io.github.r4tylmz.betterpoi.constraint;

import io.github.r4tylmz.betterpoi.i18n.MessageSourceService;
import org.apache.poi.ss.usermodel.Cell;

/**
 * Interface for cell constraints in an Excel sheet.
 * Implementations of this interface should provide validation logic for cells in a sheet.
 */
public interface Constraint {

    void setMessageSourceService(MessageSourceService messageSourceService);

    /**
     * Validates the given cell.
     *
     * @param cell the Excel cell to be validated
     * @return a validation error message if the cell is invalid, or null if the cell is valid
     */
    String validate(Cell cell);
}