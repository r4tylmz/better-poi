package io.github.r4tylmz.betterpoi;

import io.github.r4tylmz.betterpoi.annotation.BPColumn;
import io.github.r4tylmz.betterpoi.annotation.BPSheet;
import io.github.r4tylmz.betterpoi.annotation.BPWorkbook;
import org.apache.commons.beanutils.PropertyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.*;

/**
 * BPMetadataHandler is responsible for handling metadata for the workbook and its sheets.
 * It uses reflection to parse the workbook and its sheets, and provides methods to retrieve metadata.
 */
public class BPMetadataHandler {

    private static final Logger logger = LoggerFactory.getLogger(BPMetadataHandler.class);

    private final LinkedHashMap<BPSheet, Field> sheets = new LinkedHashMap<>();

    private final Class<?> workbookClass;


    /**
     * @param workbookClass a class annotated with @BPWorkbook
     */
    public BPMetadataHandler(Class<?> workbookClass) {
        if (workbookClass == null) {
            throw new IllegalArgumentException("workbookClass can't be null");
        }
        this.workbookClass = workbookClass;
        assertWorkbookAnnotation(workbookClass);
        parseSheets();
    }

    /**
     * @param workbook an object annotated with @BPWorkbook
     */
    public BPMetadataHandler(Object workbook) {
        if (workbook == null) {
            throw new IllegalArgumentException("workbook can't be null");
        }
        workbookClass = workbook.getClass();
        assertWorkbookAnnotation(workbookClass);
        parseSheets();
    }

    /**
     * Check that object is correctly annotated.
     */
    private void assertWorkbookAnnotation(Class<?> workbookClass) {
        if (workbookClass.getAnnotation(BPWorkbook.class) == null) {
            throw new IllegalArgumentException("workbookClass must be annotated with @BPWorkbook");
        }
    }

    /**
     * @param bpSheet the sheet to get the column types for
     * @return a map of fieldName and their associated type class
     */
    public Map<String, Class<?>> getColumnTypes(BPSheet bpSheet) {
        try {
            final Map<String, Class<?>> columnTypes = new HashMap<>();
            for (BPColumn bpColumn : bpSheet.columns()) {
                final Class<?> beanClass = bpSheet.type();
                final String fieldName = bpColumn.fieldName();
                final Field field = beanClass.getDeclaredField(fieldName);
                columnTypes.put(fieldName, field.getType());
            }
            return columnTypes;
        } catch (ReflectiveOperationException e) {
            logger.error(e.getMessage(), e);
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    /**
     * @param bpSheet the sheet to get the data fields for
     * @return a map of fieldName and their associated Field
     */
    public Map<String, Field> getDataFields(BPSheet bpSheet) {
        final BPColumn[] bpColumns = bpSheet.columns();
        final Class<?> beanClass = bpSheet.type();
        final Map<String, Field> fields = new HashMap<>(bpColumns.length);
        for (BPColumn bpColumn : bpColumns) {
            final Field field = getField(beanClass, bpColumn);
            fields.put(bpColumn.fieldName(), field);
        }
        return fields;
    }

    private Field getField(Class<?> beanClass, BPColumn bpColumn) {
        try {
            return beanClass.getDeclaredField(bpColumn.fieldName());
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    public Field getField(BPSheet bpSheet) {
        return sheets.get(bpSheet);
    }

    private Object getProperty(Object bean, Field field) {
        try {
            return PropertyUtils.getProperty(bean, field.getName());
        } catch (ReflectiveOperationException e) {
            logger.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    public List<BPSheet> getSheets() {
        return new ArrayList<>(sheets.keySet());
    }

    /***
     * @param workbook an object annotated with @BPWorkbook
     * @param bpSheet the associated sheet
     * @return the list of beans (db tuples)
     */
    public List<?> getValues(final Object workbook, BPSheet bpSheet) {
        final Field field = getField(bpSheet);
        final Object scrollableResults = getProperty(workbook, field);
        if (scrollableResults instanceof List<?>) {
            return (List<?>) scrollableResults;
        }
        final String msg = "Expected " + List.class.getCanonicalName() + " got "
                + scrollableResults.getClass().getCanonicalName();
        throw new IllegalStateException(msg);
    }

    private void parseSheets() {
        final Field[] fields = workbookClass.getDeclaredFields();
        for (final Field field : fields) {
            final BPSheet annotation = field.getAnnotation(BPSheet.class);
            if (annotation != null) {
                sheets.put(annotation, field);
            }
        }
    }
}

