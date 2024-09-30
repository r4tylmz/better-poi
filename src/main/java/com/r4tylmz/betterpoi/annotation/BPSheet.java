package com.r4tylmz.betterpoi.annotation;

import com.r4tylmz.betterpoi.constraint.ColConstraint;
import com.r4tylmz.betterpoi.constraint.DefaultConstraint;
import com.r4tylmz.betterpoi.constraint.RowConstraint;

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
