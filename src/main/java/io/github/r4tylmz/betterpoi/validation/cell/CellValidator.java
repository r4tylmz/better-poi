package io.github.r4tylmz.betterpoi.validation.cell;

/**
 * Interface for cell validation.
 * Implementations of this interface should provide logic to validate a cell based on its value and metadata.
 */
public interface CellValidator {
    /**
     * Validates the given cell holder.
     *
     * @param cellHolder the cell holder containing the cell and its metadata
     * @return a validation error message if the cell is invalid, otherwise null
     */
    String validate(CellHolder cellHolder);
}