package com.osiris.dyml.db;

import com.osiris.dyml.DreamYaml;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DYTable{
    private DreamYaml yaml;
    private String name;
    private List<DYColumn> columns;

    public DYTable(DreamYaml yaml, String name){
        this(yaml, name, null);
    }

    public DYTable(DreamYaml yaml, String name, List<DYColumn> columns) {
        Objects.requireNonNull(yaml);
        Objects.requireNonNull(name);
        this.yaml = yaml;
        this.name = name;
        if (columns==null) columns = new ArrayList<>();
        this.columns = columns;
    }
            /*
    // standard methods:
    load()
    save();
    // add/remove methods:
    addTable(table)
    addColumn(table, column)
    addRow(table, values...)

    // specific set/remove methods:
    setTableAtIndex(table, index)
    setColumnAtIndex(table, column, index)
    setRowAtIndex(table, row, index)
    setValueAtIndex(column, value, index)

    // getters/setters:
    getTables()
    getTableAtIndex(index)
    getColumns(table)
    getColumnAtIndex(table, index)
    getValues(column)
    getValueAtIndex(column, index)

    // specific value getters:
    getValuesBiggerThan(value)
    getValuesSmallerThan(value)
    getValuesEqualTo(value)
    getValuesSimilarTo(value, minSimilarityInPercent)

     */


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public DYTable addColumn(DYColumn column){
        columns.add(column);
        return this;
    }

    public DYColumn getColumnByName(String name){
        for (DYColumn c :
                columns) {
            if (c.getName().equals(name))
                return c;
        }
        return null;
    }

    public DYColumn getColumnAtIndex(int index){
        return columns.get(index);
    }

    public List<DYColumn> getColumns() {
        return columns;
    }

    public void setColumns(List<DYColumn> columns) {
        this.columns = columns;
    }
}
