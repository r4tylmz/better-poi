package com.r4tylmz.betterpoi.constraint;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

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

    public RowConstraint getRowConstraint(Class<? extends RowConstraint> constraintClass) {
        try {
            return constraintClass.newInstance();
        } catch (ReflectiveOperationException e) {
            logger.error(e.getMessage(), e);
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public ColConstraint getColumnConstraint(Class<? extends ColConstraint> constraintClass) {
        try {
            return constraintClass.newInstance();
        } catch (ReflectiveOperationException e) {
            logger.error(e.getMessage(), e);
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public List<ColConstraint> getColumnConstraints(Class<? extends ColConstraint>[] constraintClasses) {
        try {
            List<ColConstraint> constraints = new ArrayList<>();
            for (Class<? extends ColConstraint> constraintClass : constraintClasses) {
                constraints.add(constraintClass.newInstance());
            }
            return constraints;
        } catch (ReflectiveOperationException e) {
            logger.error(e.getMessage(), e);
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}
