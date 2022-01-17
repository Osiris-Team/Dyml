package com.osiris.dyml.db;

import com.osiris.dyml.YamlSection;
import com.osiris.dyml.YamlValue;

import java.util.List;

/**
 * Represents the column of a {@link YamlTable}. Example:
 * <pre>
 * column1 (index 0) | column2 (index 1)
 * =====================================
 * rock              | paper
 * tree              | grass
 * </pre>
 *
 * @see YamlRow
 */
public class YamlColumn {
    private final YamlSection columnModule;

    public YamlColumn(YamlSection columnModule) {
        this.columnModule = columnModule;
    }

    public YamlSection getColumnModule() {
        return columnModule;
    }

    public String getName() {
        return columnModule.getLastKey();
    }

    /**
     * Adds the provided string to a {@link YamlValue} and then adds that to the column.
     */
    public YamlColumn add(String... values) {
        columnModule.addValues(values);
        return this;
    }

    /**
     * Adds the provided {@link YamlValue} to the column.
     */
    public YamlColumn add(YamlValue... values) {
        columnModule.addValues(values);
        return this;
    }


    /**
     * Adds the provided string to a {@link YamlValue} and then adds that to the column. <br>
     * Note that this is a default value, thus it only gets written to the file if the column was empty before. <br>
     */
    public YamlColumn addDef(String... values) {
        columnModule.addDefValues(values);
        return this;
    }

    /**
     * Adds the provided {@link YamlValue} to the column. <br>
     * Note that this is a default value, thus it only gets written to the file if the column was empty before. <br>
     */
    public YamlColumn addDef(YamlValue... values) {
        columnModule.addDefValues(values);
        return this;
    }

    /**
     * Returns the columns size,<br>
     * aka length,<br>
     * aka the amount of rows for this column, <br>
     * aka the amount of {@link YamlValue}s. <br>
     */
    public int size() {
        return columnModule.getValues().size();
    }

    /**
     * Returns the columns size for default values,<br>
     * aka length,<br>
     * aka the amount of default rows for this column, <br>
     * aka the amount of default {@link YamlValue}s. <br>
     */
    public int defSize() {
        return columnModule.getDefValues().size();
    }

    public List<YamlValue> getValues() {
        return columnModule.getValues();
    }

    public List<YamlValue> getDefValues() {
        return columnModule.getDefValues();
    }


    // GETTERS


    /**
     * Returns the {@link YamlValue} at the provided index (row).
     */
    public YamlValue get(int index) {
        return columnModule.getValueByIndex(index);
    }

    /**
     * Returns the default {@link YamlValue} at the provided index (row).
     */
    public YamlValue getDef(int index) {
        return columnModule.getDefValueByIndex(index);
    }

}
