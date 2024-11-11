package io.github.r4tylmz.betterpoi.validation.cell;

import io.github.r4tylmz.betterpoi.annotation.BPColumn;
import org.apache.poi.ss.usermodel.Cell;

import java.lang.reflect.Field;


/**
 * POJO object to hold values.
 * This class encapsulates the details of a cell, including its value, the field it corresponds to, and the BPColumn annotation.
 */
public class CellHolder {
    private Cell cell;
    private String cellValue;
    private Field field;
    private BPColumn bpColumn;

    public CellHolder() {
    }

    /**
     * Parameterized constructor to initialize all fields.
     *
     * @param cell      the Excel cell
     * @param cellValue the value of the cell as a string
     * @param field     the field in the class corresponding to the cell
     * @param bpColumn  the BPColumn annotation containing metadata for the cell
     */
    public CellHolder(Cell cell, String cellValue, Field field, BPColumn bpColumn) {
        this.cell = cell;
        this.cellValue = cellValue;
        this.field = field;
        this.bpColumn = bpColumn;
    }

    public Cell getCell() {
        return cell;
    }

    public void setCell(Cell cell) {
        this.cell = cell;
    }

    public String getCellValue() {
        return cellValue;
    }

    public void setCellValue(String cellValue) {
        this.cellValue = cellValue;
    }

    public Field getField() {
        return field;
    }

    public void setField(Field field) {
        this.field = field;
    }

    public BPColumn getBpColumn() {
        return bpColumn;
    }

    public void setBpColumn(BPColumn bpColumn) {
        this.bpColumn = bpColumn;
    }
}
