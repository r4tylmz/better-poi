package com.github.betterpoi.annotation;

import com.github.betterpoi.constraint.ColConstraint;
import com.github.betterpoi.constraint.DefaultConstraint;
import com.github.betterpoi.constraint.RowConstraint;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface BPSheet {
    BPColumn[] columns();

    String sheetName() default "Sheet1";

    Class<? extends RowConstraint>[] rowValidators() default DefaultConstraint.class;

    Class<? extends ColConstraint>[] colValidators() default DefaultConstraint.class;

    Class<?> type();

    boolean toImport() default true;

    boolean validate() default true;
}
