package com.osiris.dyml.db;

import com.osiris.dyml.DYModule;
import com.osiris.dyml.DYValue;

import java.util.List;

/**
 * Represents the column of a {@link DYTable}. Example:
 * <pre>
 * column1 (index 0) | column2 (index 1)
 * =====================================
 * rock              | paper
 * tree              | grass
 * </pre>
 *
 * @see DYRow
 */
public class DYColumn {
    private final DYModule columnModule;

    public DYColumn(DYModule columnModule) {
        this.columnModule = columnModule;
    }

    public DYModule getColumnModule() {
        return columnModule;
    }

    public String getName() {
        return columnModule.getLastKey();
    }

    /**
     * Adds the provided string to a {@link DYValue} and then adds that to the column.
     */
    public DYColumn add(String... values) {
        columnModule.addValues(values);
        return this;
    }

    /**
     * Adds the provided {@link DYValue} to the column.
     */
    public DYColumn add(DYValue... values) {
        columnModule.addValues(values);
        return this;
    }


    /**
     * Adds the provided string to a {@link DYValue} and then adds that to the column. <br>
     * Note that this is a default value, thus it only gets written to the file if the column was empty before. <br>
     */
    public DYColumn addDef(String... values) {
        columnModule.addDefValues(values);
        return this;
    }

    /**
     * Adds the provided {@link DYValue} to the column. <br>
     * Note that this is a default value, thus it only gets written to the file if the column was empty before. <br>
     */
    public DYColumn addDef(DYValue... values) {
        columnModule.addDefValues(values);
        return this;
    }

    /**
     * Returns the columns size,<br>
     * aka length,<br>
     * aka the amount of rows for this column, <br>
     * aka the amount of {@link DYValue}s. <br>
     */
    public int size() {
        return columnModule.getValues().size();
    }

    /**
     * Returns the columns size for default values,<br>
     * aka length,<br>
     * aka the amount of default rows for this column, <br>
     * aka the amount of default {@link DYValue}s. <br>
     */
    public int defSize() {
        return columnModule.getDefValues().size();
    }

    public List<DYValue> getValues() {
        return columnModule.getValues();
    }

    public List<DYValue> getDefValues() {
        return columnModule.getDefValues();
    }


    // GETTERS


    /**
     * Returns the {@link DYValue} at the provided index (row).
     */
    public DYValue get(int index) {
        return columnModule.getValueByIndex(index);
    }

    /**
     * Returns the default {@link DYValue} at the provided index (row).
     */
    public DYValue getDef(int index) {
        return columnModule.getDefValueByIndex(index);
    }

}
