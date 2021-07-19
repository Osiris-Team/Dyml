package com.osiris.dyml.db;

import com.osiris.dyml.DYModule;
import com.osiris.dyml.DYValueContainer;

import java.util.ArrayList;
import java.util.List;

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


    // GETTERS


    public DYValueContainer get(int index) {
        return columnModule.getValueByIndex(index);
    }

    public DYValueContainer getDef(int index) {
        return columnModule.getDefValueByIndex(index);
    }


    // QUERIES (currently not available for def values)
    // TODO getValuesSimilarTo(value, minSimilarityInPercent)


    public List<DYValueContainer> getEqualTo(String value) {
        List<DYValueContainer> results = new ArrayList<>();
        for (DYValueContainer v :
                columnModule.getValues()) {
            if (v.asString() != null && v.asString().equals(value))
                results.add(v);
        }
        return results;
    }

    public List<DYValueContainer> getEqualTo(DYValueContainer value) {
        List<DYValueContainer> results = new ArrayList<>();
        for (DYValueContainer v :
                columnModule.getValues()) {
            if (v.equals(value))
                results.add(v);
        }
        return results;
    }


    public List<DYValueContainer> getBiggerThan(long value) {
        List<DYValueContainer> results = new ArrayList<>();
        for (DYValueContainer v :
                columnModule.getValues()) {
            if (v.asLong() > value)
                results.add(v);
        }
        return results;
    }

    public List<DYValueContainer> getBiggerThan(double value) {
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
