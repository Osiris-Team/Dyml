package com.osiris.dyml.db;

import com.osiris.dyml.DYModule;
import com.osiris.dyml.DYValueContainer;

import java.util.List;

/**
 * Represents the column of a {@link DYTable}. Example:
 * <pre>
 * column1 (index 0) | column2 (index 1)
 * =====================================
 * rock              | paper
 * tree              | grass
 * </pre>
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
     * Adds the provided string to a {@link DYValueContainer} and then adds that to the column.
     */
    public DYColumn add(String... values) {
        columnModule.addValues(values);
        return this;
    }

    /**
     * Adds the provided {@link DYValueContainer} to the column.
     */
    public DYColumn add(DYValueContainer... values) {
        columnModule.addValues(values);
        return this;
    }


    /**
     * Adds the provided string to a {@link DYValueContainer} and then adds that to the column. <br>
     * Note that this is a default value, thus it only gets written to the file if the column was empty before. <br>
     */
    public DYColumn addDef(String... values) {
        columnModule.addDefValues(values);
        return this;
    }

    /**
     * Adds the provided {@link DYValueContainer} to the column. <br>
     * Note that this is a default value, thus it only gets written to the file if the column was empty before. <br>
     */
    public DYColumn addDef(DYValueContainer... values) {
        columnModule.addDefValues(values);
        return this;
    }

    /**
     * Returns the columns size,<br>
     * aka length,<br>
     * aka the amount of rows for this column, <br>
     * aka the amount of {@link DYValueContainer}s. <br>
     */
    public int size(){
        return columnModule.getValues().size();
    }

    /**
     * Returns the columns size for default values,<br>
     * aka length,<br>
     * aka the amount of default rows for this column, <br>
     * aka the amount of default {@link DYValueContainer}s. <br>
     */
    public int defSize(){
        return columnModule.getDefValues().size();
    }

    public List<DYValueContainer> getValues(){
        return columnModule.getValues();
    }

    public List<DYValueContainer> getDefValues(){
        return columnModule.getDefValues();
    }


    // GETTERS


    /**
     * Returns the {@link DYValueContainer} at the provided index (row).
     */
    public DYValueContainer get(int index) {
        return columnModule.getValueByIndex(index);
    }

    /**
     * Returns the default {@link DYValueContainer} at the provided index (row).
     */
    public DYValueContainer getDef(int index) {
        return columnModule.getDefValueByIndex(index);
    }

}
