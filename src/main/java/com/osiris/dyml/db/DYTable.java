package com.osiris.dyml.db;

import com.osiris.dyml.DYModule;
import com.osiris.dyml.DYValueContainer;
import com.osiris.dyml.DreamYaml;
import com.osiris.dyml.exceptions.DuplicateKeyException;
import com.osiris.dyml.exceptions.IllegalKeyException;
import com.osiris.dyml.exceptions.NotLoadedException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DYTable {
    private final DYModule tableModule;

    public DYTable(DYModule tableModule) {
        Objects.requireNonNull(tableModule);
        this.tableModule = tableModule;
    }

    public DYModule getTableModule() {
        return tableModule;
    }

    public String getName() {
        return tableModule.getLastKey();
    }

    /**
     * Returns the row at the provided index. <br>
     * Values are read from left to right. <br>
     * @throws IndexOutOfBoundsException when the row at the provided index does not exist.
     */
    public List<DYValueContainer> getRow(int index){
        List<DYValueContainer> row = new ArrayList<>();
        for (DYColumn col :
                getColumns()) {
            row.add(col.get(index));
        }
        return row;
    }

    /**
     * Tries to add a complete row. <br>
     * Values are read from left to right. <br>
     * If there are missing values to fill the row, null values get added. <br>
     * @throws IndexOutOfBoundsException if there are more values than columns available.
     */
    public DYTable addRow(DYValueContainer... values){
        List<DYColumn> columns = getColumns();
        for (int i = 0; i < values.length; i++) {
            columns.get(i).add(values[i]);
        }

        for (int i = values.length-1; i < columns.size() - values.length; i++) {
            columns.get(i).add((String) null); // Adds null values to complete the row
        }
        return this;
    }


    /**
     * Sets the row at the provided index. <br>
     * Values are read from left to right. <br>
     * Note that if there are more columns than you provided values, the values for those columns get set to null. <br>
     * @throws IndexOutOfBoundsException if the row at the provided index does not exist.
     */
    public DYTable setRow(int index, DYValueContainer... values){
        List<DYColumn> columns = getColumns();
        for (DYColumn col :
                columns) {
            try{
                col.get(index);
            } catch (Exception e) {
                throw new IndexOutOfBoundsException("Row '"+index+"' does not exist for column '"+col.getName()+"' in table '"+getName()+"'.");
            }
        }
        for (int i = 0; i < values.length; i++) {
            columns.get(i).get(index).set(values[i].get());
        }

        for (int i = values.length-1; i < columns.size() - values.length; i++) {
            columns.get(i).get(index).set((String) null); // Adds null values to complete the row
        }
        return this;
    }

    /**
     * See {@link DreamYaml#add(String...)} for details.
     */
    public DYColumn addColumn(String name) throws NotLoadedException, IllegalKeyException, DuplicateKeyException {
        return new DYColumn(tableModule.getYaml().add("tables", getName(), name));
    }

    /**
     * See {@link DreamYaml#put(String...)} for details.
     */
    public DYColumn putColumn(String name) throws NotLoadedException, IllegalKeyException {
        return new DYColumn(tableModule.getYaml().put("tables", getName(), name));
    }

    public DYTable removeColumn(DYColumn column) {
        Objects.requireNonNull(column);
        tableModule.getYaml().remove("tables", getName(), column.getName());
        return this;
    }

    public DYColumn getColumn(String name) {
        for (DYColumn c :
                getColumns()) {
            if (c.getName().equals(name))
                return c;
        }
        return null;
    }

    public DYColumn getColumnAtIndex(int index) {
        return getColumns().get(index);
    }

    public List<DYColumn> getColumns() {
        List<DYColumn> columns = new ArrayList<>();
        for (DYModule columnModule :
                tableModule.getChildModules()) {
            columns.add(new DYColumn(columnModule));
        }
        return columns;
    }
}
