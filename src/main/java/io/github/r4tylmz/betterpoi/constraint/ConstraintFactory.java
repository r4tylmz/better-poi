package io.github.r4tylmz.betterpoi.constraint;

import io.github.r4tylmz.betterpoi.i18n.MessageSourceService;
import io.github.r4tylmz.betterpoi.validation.cell.CellValidator;
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
    private MessageSourceService messageSourceService;

    /**
     * Singleton instance of ConstraintFactory.
     */
    private static ConstraintFactory instance;

    private ConstraintFactory() {
    }

    private ConstraintFactory(MessageSourceService messageSourceService) {
        this.messageSourceService = messageSourceService;
    }

    /**
     * Returns the singleton instance of ConstraintFactory.
     * @param messageSourceService the service for retrieving localized messages
     * @return the singleton instance of ConstraintFactory
     */
    public static ConstraintFactory getInstance(MessageSourceService messageSourceService) {
        if (instance == null) {
            instance = new ConstraintFactory(messageSourceService);
        }
        return instance;
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
            CellValidator cellValidator = validatorClass.getDeclaredConstructor(MessageSourceService.class).newInstance(messageSourceService);
            return cellValidator;
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
        List<CellValidator> cellValidators = new ArrayList<>();
        for (Class<? extends CellValidator> validatorClass : validatorClasses) {
            cellValidators.add(getCellValidator(validatorClass));
        }
        return cellValidators;
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
            ColConstraint colConstraint = constraintClass.newInstance();
            colConstraint.setMessageSourceService(messageSourceService);
            return colConstraint;
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
        List<ColConstraint> constraints = new ArrayList<>();
        for (Class<? extends ColConstraint> constraintClass : constraintClasses) {
            constraints.add(getColumnConstraint(constraintClass));
        }
        return constraints;
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
            Constraint constraint = constraintClass.getDeclaredConstructor(MessageSourceService.class).newInstance(messageSourceService);
            return constraint;
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
            RowConstraint rowConstraint = constraintClass.getDeclaredConstructor(MessageSourceService.class).newInstance(messageSourceService);
            return rowConstraint;
        } catch (ReflectiveOperationException e) {
            logger.error(e.getMessage(), e);
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}