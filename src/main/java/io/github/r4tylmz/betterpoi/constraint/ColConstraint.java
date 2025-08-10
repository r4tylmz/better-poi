package io.github.r4tylmz.betterpoi.constraint;

import io.github.r4tylmz.betterpoi.annotation.BPSheet;
import io.github.r4tylmz.betterpoi.i18n.MessageSourceService;
import org.apache.poi.ss.usermodel.Sheet;

import java.util.Map;

/**
 * Interface for column constraints in an Excel sheet.
 * Implementations of this interface should provide validation logic for columns in a sheet.
 */
public interface ColConstraint {

    void setMessageSourceService(MessageSourceService messageSourceService);

    /**
     * Validates the columns in the given sheet based on the specified BPSheet annotation.
     *
     * @param sheet   the Excel sheet to be validated
     * @param bpSheet the BPSheet annotation containing validation rules
     * @return a map where the key is the column index and the value is the validation error message
     */
    Map<Integer, String> validate(Sheet sheet, BPSheet bpSheet);
}