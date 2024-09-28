package com.github.betterpoi.annotation;

import com.github.betterpoi.constraint.Constraint;
import com.github.betterpoi.constraint.DefaultConstraint;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(value = RetentionPolicy.RUNTIME)
public @interface BPColumn {
    Class<? extends Constraint> cellValidator() default DefaultConstraint.class;

    String filedName();

    boolean required() default false;

    String pattern() default "";

    String datePattern() default "";

    String headerTitle() default "";
}
