package com.osiris.dyml.db;

import com.osiris.dyml.YamlValue;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Represents the row of a {@link YamlTable}. Example:
 * <pre>
 * column1 | column2
 * =================
 * rock    | paper   <--- This is the row at index 0
 * tree    | grass   <--- This is the row at index 1
 * </pre>
 *
 * @see YamlColumn
 */
public class YamlRow {
    private final int rowIndex;
    private final Map<YamlValue, YamlColumn> valuesAndColumns;

    public YamlRow(int rowIndex, Map<YamlValue, YamlColumn> valuesAndColumns) {
        this.rowIndex = rowIndex;
        this.valuesAndColumns = valuesAndColumns;
    }

    /**
     * Values are read from left to right. Example table:
     * <pre>
     *     column1 | column2
     *     =================
     *     rock    | paper
     * </pre>
     * The examples, returned list has two {@link YamlValue}s. <br>
     * The first one contains 'rock' and the second one 'paper'. <br>
     */
    public List<YamlValue> getValues() {
        return new ArrayList<>(valuesAndColumns.keySet());
    }

    /**
     * This rows index position in the table. Example table:
     * <pre>
     *     column1 | column2
     *     =================
     *     rock    | paper   <--- Index: 0
     *     tree    | grass   <--- Index: 1
     * </pre>
     */
    public int getRowIndex() {
        return rowIndex;
    }


    /**
     * Returns a map with values mapped to their columns.
     */
    public Map<YamlValue, YamlColumn> getValuesAndColumns() {
        return valuesAndColumns;
    }

    public YamlColumn getColumnFromValue(YamlValue value) {
        return valuesAndColumns.get(value);
    }

    /**
     * Returns the linked {@link YamlValue} for the provided {@link YamlColumn}.
     *
     * @throws NullPointerException if the provided column is null, or couldn't be found in the map.
     */
    public YamlValue getValueFromColumn(YamlColumn column) {
        Objects.requireNonNull(column);
        YamlValue[] values = valuesAndColumns.keySet().toArray(new YamlValue[0]);
        int index = 0;
        YamlValue val = null;
        for (YamlColumn col :
                valuesAndColumns.values()) {
            if (col.getName().equals(column.getName()))
                val = values[index];
            index++;
        }
        if (val == null)
            throw new NullPointerException("Column '" + column.getName() + "' couldn't be found in: " + values);
        else
            return val;
    }

}
