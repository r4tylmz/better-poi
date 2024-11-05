package com.r4tylmz.betterpoi.annotation;

import com.r4tylmz.betterpoi.constraint.DefaultConstraint;
import com.r4tylmz.betterpoi.validation.cell.CellValidator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to define a column in a {@link BPSheet}.
 * This annotation can be used to specify various properties of the column,
 * such as the cell validator, field name, required status, pattern, date pattern, and header title.
 */
@Target(ElementType.FIELD)
@Retention(value = RetentionPolicy.RUNTIME)
public @interface BPColumn {
    /**
     * Specifies the cell validators to be used for the column.
     * By default, it uses {@link DefaultConstraint}.
     *
     * @return an array of cell validator classes
     */
    Class<? extends CellValidator>[] cellValidators() default DefaultConstraint.class;

    /**
     * Specifies the date pattern to be used for the column.
     *
     * @return the date pattern as a string
     */
    String datePattern() default "";

    /**
     * Specifies the name of the field.
     *
     * @return the field name
     */
    String fieldName();

    /**
     * Specifies the header title of the column.
     *
     * @return the header title as a string
     */
    String headerTitle() default "";

    /**
     * Specifies the pattern to be used for the column.
     *
     * @return the pattern as a string
     */
    String pattern() default "";

    /**
     * Specifies whether the column is required.
     *
     * @return true if the column is required, default value is false
     */
    boolean required() default false;
}