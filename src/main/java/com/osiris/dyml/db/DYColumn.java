package com.osiris.dyml.db;

import com.osiris.dyml.DYModule;
import com.osiris.dyml.DYValueContainer;

import java.util.ArrayList;
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

    public DYColumn add(String... values) {
        columnModule.addValues(values);
        return this;
    }

    public DYColumn add(DYValueContainer... values) {
        columnModule.addValues(values);
        return this;
    }


    public DYColumn addDef(String... values) {
        columnModule.addDefValues(values);
        return this;
    }

    public DYColumn addDef(DYValueContainer... values) {
        columnModule.addDefValues(values);
        return this;
    }

    public int size(){
        return columnModule.getValues().size();
    }

    public int defSize(){
        return columnModule.getDefValues().size();
    }


    // GETTERS


    /**
     * Returns the {@link DYValueContainer} at the provided index.
     */
    public DYValueContainer get(int index) {
        return columnModule.getValueByIndex(index);
    }

    /**
     * Returns the default {@link DYValueContainer} at the provided index.
     */
    public DYValueContainer getDef(int index) {
        return columnModule.getDefValueByIndex(index);
    }


    // QUERIES (currently not available for def values)
    // TODO getValuesSimilarTo(value, minSimilarityInPercent)


    public List<DYValueContainer> getValuesEqualTo(String value) {
        List<DYValueContainer> results = new ArrayList<>();
        for (DYValueContainer v :
                columnModule.getValues()) {
            if (v.asString() != null && v.asString().equals(value))
                results.add(v);
        }
        return results;
    }

    public List<DYValueContainer> getValuesEqualTo(DYValueContainer value) {
        List<DYValueContainer> results = new ArrayList<>();
        for (DYValueContainer v :
                columnModule.getValues()) {
            if (v.equals(value))
                results.add(v);
        }
        return results;
    }

    public List<DYValueContainer> getRowsEqualTo(String value) {
        List<DYValueContainer> results = new ArrayList<>();
        for (DYValueContainer v :
                columnModule.getValues()) {
            if (v.asString() != null && v.asString().equals(value))
                results.add(v);
        }
        return results;
    }


    public List<DYValueContainer> getRowsEqualTo(DYValueContainer value) {
        List<DYValueContainer> results = new ArrayList<>();
        for (DYValueContainer v :
                columnModule.getValues()) {
            if (v.equals(value))
                results.add(v);
        }
        return results;
    }


    public List<DYValueContainer> getValuesBiggerThan(long value) {
        List<DYValueContainer> results = new ArrayList<>();
        for (DYValueContainer v :
                columnModule.getValues()) {
            if (v.asLong() > value)
                results.add(v);
        }
        return results;
    }

    public List<DYValueContainer> getValuesBiggerThan(double value) {
        List<DYValueContainer> results = new ArrayList<>();
        for (DYValueContainer v :
                columnModule.getValues()) {
            if (v.asDouble() > value)
                results.add(v);
        }
        return results;
    }

    public List<DYValueContainer> getSmallerThan(long value) {
        List<DYValueContainer> results = new ArrayList<>();
        for (DYValueContainer v :
                columnModule.getValues()) {
            if (v.asLong() < value)
                results.add(v);
        }
        return results;
    }

    public List<DYValueContainer> getSmallerThan(double value) {
        List<DYValueContainer> results = new ArrayList<>();
        for (DYValueContainer v :
                columnModule.getValues()) {
            if (v.asDouble() < value)
                results.add(v);
        }
        return results;
    }

}
