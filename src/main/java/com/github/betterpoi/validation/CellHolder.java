package com.github.betterpoi.validation;

import com.github.betterpoi.annotation.BPColumn;
import org.apache.poi.ss.usermodel.Cell;

import java.lang.reflect.Field;


/**
 * POJO object to hold values
 */
public class CellHolder {
    private Cell cell;
    private String cellValue;
    private Field field;
    private BPColumn bpColumn;

    public CellHolder() {
    }

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
