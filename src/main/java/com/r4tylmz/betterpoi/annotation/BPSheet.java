package com.r4tylmz.betterpoi.annotation;

import com.r4tylmz.betterpoi.constraint.ColConstraint;
import com.r4tylmz.betterpoi.constraint.DefaultConstraint;
import com.r4tylmz.betterpoi.constraint.RowConstraint;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to define a sheet in an Excel workbook.
 * This annotation can be used to specify various properties of the sheet,
 * such as the columns, sheet name, row validators, column validators, type, import status, and validation status.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface BPSheet {
    /**
     * Specifies the column validators to be used for this sheet.
     * Defaults to {@link DefaultConstraint}.
     *
     * @return an array of column validator classes
     */
    Class<? extends ColConstraint>[] colValidators() default DefaultConstraint.class;

    /**
     * Specifies the columns in the sheet.
     *
     * @return an array of BPColumn annotations
     */
    BPColumn[] columns();

    /**
     * Specifies the row validators to be used for this sheet.
     * Defaults to {@link DefaultConstraint}.
     *
     * @return an array of row validator classes
     */
    Class<? extends RowConstraint>[] rowValidators() default DefaultConstraint.class;

    /**
     * Specifies the name of the sheet.
     * Defaults to "Sheet1".
     *
     * @return the sheet name as a string
     */
    String sheetName() default "Sheet1";

    /**
     * Specifies whether the sheet should be imported.
     * Defaults to true.
     *
     * @return true if the sheet should be imported, false otherwise
     */
    boolean toImport() default true;

    /**
     * Specifies the type of the sheet.
     *
     * @return the class type
     */
    Class<?> type();

    /**
     * Specifies whether the sheet should be validated.
     * Defaults to true.
     *
     * @return true if the sheet should be validated, false otherwise
     */
    boolean validate() default true;
}