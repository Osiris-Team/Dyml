package com.osiris.dyml.db;

import com.osiris.dyml.SmartString;

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
    private final Map<SmartString, YamlColumn> valuesAndColumns;

    public YamlRow(int rowIndex, Map<SmartString, YamlColumn> valuesAndColumns) {
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
     * The examples, returned list has two {@link SmartString}s. <br>
     * The first one contains 'rock' and the second one 'paper'. <br>
     */
    public List<SmartString> getValues() {
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
    public Map<SmartString, YamlColumn> getValuesAndColumns() {
        return valuesAndColumns;
    }

    public YamlColumn getColumnFromValue(SmartString value) {
        return valuesAndColumns.get(value);
    }

    /**
     * Returns the linked {@link SmartString} for the provided {@link YamlColumn}.
     *
     * @throws NullPointerException if the provided column is null, or couldn't be found in the map.
     */
    public SmartString getValueFromColumn(YamlColumn column) {
        Objects.requireNonNull(column);
        SmartString[] values = valuesAndColumns.keySet().toArray(new SmartString[0]);
        int index = 0;
        SmartString val = null;
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
