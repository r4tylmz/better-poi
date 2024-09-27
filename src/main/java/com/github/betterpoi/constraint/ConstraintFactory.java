package com.github.betterpoi.constraint;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConstraintFactory {
    final static Logger logger = LoggerFactory.getLogger(ConstraintFactory.class);

    private static ConstraintFactory instance;

    private ConstraintFactory() {
    }

    public static ConstraintFactory getInstance() {
        if (instance == null) {
            instance = new ConstraintFactory();
        }
        return instance;
    }

    public Constraint getConstraint(Class<? extends Constraint> constraintClass) {
        try {
            return constraintClass.newInstance();
        } catch (ReflectiveOperationException e) {
            logger.error(e.getMessage(), e);
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}
