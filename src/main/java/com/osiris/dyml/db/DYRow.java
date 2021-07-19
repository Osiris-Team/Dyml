package com.osiris.dyml.db;

import com.osiris.dyml.DYValueContainer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Represents the row of a {@link DYTable}. Example:
 * <pre>
 * column1 | column2
 * =================
 * rock    | paper   <--- This is the row at index 0
 * tree    | grass   <--- This is the row at index 1
 * </pre>
 * @see DYColumn
 */
public class DYRow {
    private int index;
    private Map<DYValueContainer, DYColumn> valuesAndColumns;

    public DYRow(int index, Map<DYValueContainer, DYColumn> valuesAndColumns) {
        this.index = index;
        this.valuesAndColumns = valuesAndColumns;
    }

    /**
     * Values are read from left to right. Example table:
     * <pre>
     *     column1 | column2
     *     =================
     *     rock    | paper
     * </pre>
     * The returned list has two {@link DYValueContainer}s. <br>
     * The first one contains 'rock' and the second one 'paper' and so on... <br>
     */
    public List<DYValueContainer> getValues(){
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
    public int getIndex() {
        return index;
    }

    /**
     * Returns a map with values mapped to their columns.
     */
    public Map<DYValueContainer, DYColumn> getValuesAndColumns() {
        return valuesAndColumns;
    }

    public DYColumn getColumnFromValue(DYValueContainer value){
        return valuesAndColumns.get(value);
    }

    /**
     * Returns the linked {@link DYValueContainer} for the provided {@link DYColumn}.
     * @throws NullPointerException if the provided column is null, or couldn't be found in the map.
     */
    public DYValueContainer getValueFromColumn(DYColumn column){
        Objects.requireNonNull(column);
        DYValueContainer[] values = (DYValueContainer[]) valuesAndColumns.keySet().toArray();
        int index = 0;
        DYValueContainer val = null;
        for (DYColumn col :
                valuesAndColumns.values()) {
            if (col.equals(column))
                val = values[index];
            index++;
        }
        if (val==null)
            throw new NullPointerException("Column '"+column.getName()+"' couldn't be found in: "+ values);
        else
            return val;
    }

}
