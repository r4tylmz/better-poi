package com.r4tylmz.betterpoi.constraint;

import com.r4tylmz.betterpoi.validation.cell.CellValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Factory class for creating constraint instances.
 * This class provides methods to create instances of various constraint types.
 */
public class ConstraintFactory {
    final static Logger logger = LoggerFactory.getLogger(ConstraintFactory.class);

    /**
     * Singleton instance of ConstraintFactory.
     */
    private static ConstraintFactory instance;

    private ConstraintFactory() {
    }

    /**
     * Returns the singleton instance of ConstraintFactory.
     *
     * @return the singleton instance of ConstraintFactory
     */
    public static ConstraintFactory getInstance() {
        if (instance == null) {
            instance = new ConstraintFactory();
        }
        return instance;
    }

    /**
     * Creates an instance of the specified column constraint class.
     *
     * @param constraintClass the class of the column constraint to be created
     * @return an instance of the specified column constraint class
     * @throws RuntimeException if an error occurs during instantiation
     */
    public ColConstraint getColumnConstraint(Class<? extends ColConstraint> constraintClass) {
        try {
            return constraintClass.newInstance();
        } catch (ReflectiveOperationException e) {
            logger.error(e.getMessage(), e);
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    /**
     * Creates a list of instances of the specified column constraint classes.
     *
     * @param constraintClasses an array of column constraint classes to be created
     * @return a list of instances of the specified column constraint classes
     * @throws RuntimeException if an error occurs during instantiation
     */
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

    /**
     * Creates an instance of the specified constraint class.
     *
     * @param constraintClass the class of the constraint to be created
     * @return an instance of the specified constraint class
     * @throws RuntimeException if an error occurs during instantiation
     */
    public Constraint getConstraint(Class<? extends Constraint> constraintClass) {
        try {
            return constraintClass.newInstance();
        } catch (ReflectiveOperationException e) {
            logger.error(e.getMessage(), e);
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    /**
     * Creates an instance of the specified row constraint class.
     *
     * @param constraintClass the class of the row constraint to be created
     * @return an instance of the specified row constraint class
     * @throws RuntimeException if an error occurs during instantiation
     */
    public RowConstraint getRowConstraint(Class<? extends RowConstraint> constraintClass) {
        try {
            return constraintClass.newInstance();
        } catch (ReflectiveOperationException e) {
            logger.error(e.getMessage(), e);
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    /**
     * Creates an instance of the specified cell validator class.
     *
     * @param validatorClass the class of the cell validator to be created
     * @return an instance of the specified cell validator class
     * @throws RuntimeException if an error occurs during instantiation
     */
    public CellValidator getCellValidator(Class<? extends CellValidator> validatorClass) {
        try {
            return validatorClass.newInstance();
        } catch (ReflectiveOperationException e) {
            logger.error(e.getMessage(), e);
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    /**
     * Creates a list of instances of the specified cell validator classes.
     *
     * @param validatorClasses an array of cell validator classes to be created
     * @return a list of instances of the specified cell validator classes
     * @throws RuntimeException if an error occurs during instantiation
     */
    public List<CellValidator> getCellValidators(Class<? extends CellValidator>[] validatorClasses) {
        try {
            List<CellValidator> cellValidators = new ArrayList<>();
            for (Class<? extends CellValidator> validatorClass : validatorClasses) {
                cellValidators.add(validatorClass.newInstance());
            }
            return cellValidators;
        } catch (ReflectiveOperationException e) {
            logger.error(e.getMessage(), e);
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}