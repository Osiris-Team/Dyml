package com.osiris.dyml.db;

import com.osiris.dyml.DYModule;
import com.osiris.dyml.DYValueContainer;
import com.osiris.dyml.DreamYaml;
import com.osiris.dyml.exceptions.*;

import java.io.IOException;
import java.util.*;

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
     * Returns the {@link DYRow} at the provided index.
     *
     * @throws IndexOutOfBoundsException when the row at the provided index does not exist.
     */
    public DYRow getRow(int index) {
        Map<DYValueContainer, DYColumn> valuesAndColumns = new HashMap<>();
        for (DYColumn col :
                getColumns()) {
            valuesAndColumns.put(col.get(index), col);
        }
        return new DYRow(index, valuesAndColumns);
    }

    /**
     * Returns the {@link DYRow} at the provided index, as a list of {@link DYValueContainer}s. <br>
     *
     * @throws IndexOutOfBoundsException when the row at the provided index does not exist.
     */
    public List<DYValueContainer> getRowAsList(int index) {
        List<DYValueContainer> row = new ArrayList<>();
        for (DYColumn col :
                getColumns()) {
            row.add(col.get(index));
        }
        return row;
    }

    /**
     * Tries to add a {@link DYRow} to the table. <br>
     * If there are missing values to fill the row, null values get added. <br>
     * Note that all columns must have the same length. <br>
     *
     * @throws IndexOutOfBoundsException if there are more values than columns available.
     * @throws BrokenColumnsException    if the columns have different sizes/lengths.
     */
    public DYTable addRow(String... values) {
        List<DYColumn> columns = getColumns();
        List<DYColumn> columnsCopy = new ArrayList<>(columns);

        // Check for different column lengths
        for (DYColumn col :
                columns) {
            for (DYColumn col1 :
                    columnsCopy) {
                if (!col.equals(col1) && col.size() != col1.size())
                    throw new BrokenColumnsException("All columns should have the same length." +
                            " Column '" + col.getName() + "' has a size of '" + col.size() + "' but column " + col1.getName() + " has a size of '" + col1.size() + "'.");
            }
        }

        if (values.length > columns.size())
            throw new IndexOutOfBoundsException("Failed to addRow(), because there are more values(" + values.length + ") than columns(" + columns.size() + ") available!");

        for (int i = 0; i < columns.size(); i++) {
            String val = null;
            try {
                val = values[i];
            } catch (Exception ignored) {
            }

            columns.get(i).add(val); // Adds null values to complete the row
        }

        return this;
    }

    /**
     * Tries to add a {@link DYRow} to the table. <br>
     * If there are missing values to fill the row, null values get added. <br>
     * Note that all columns must have the same length. <br>
     *
     * @throws IndexOutOfBoundsException if there are more values than columns available.
     * @throws BrokenColumnsException    if the columns have different sizes/lengths.
     */
    public DYTable addDefRow(String... values) {
        List<DYColumn> allColumns = getColumns();
        List<DYColumn> columnsCopy = new ArrayList<>(allColumns);

        // Check for different column lengths
        for (DYColumn col :
                allColumns) {
            for (DYColumn col1 :
                    columnsCopy) {
                if (!col.equals(col1) && col.size() != col1.size())
                    throw new BrokenColumnsException("All columns should have the same size/length." +
                            " Column '" + col.getName() + "' has a size of '" + col.size() + "' but column '" + col1.getName() + "' has a size of '" + col1.size() + "'.");
            }
        }

        if (values.length > allColumns.size())
            throw new IndexOutOfBoundsException("Failed to addDefRow(), because there are more values(" + values.length + ") than columns(" + allColumns.size() + ") available!");

        for (int i = 0; i < values.length; i++) {
            allColumns.get(i).addDef(values[i]);
            System.out.println("ADDED");
        }

        for (int i = values.length - 1; i < allColumns.size() - values.length; i++) {
            allColumns.get(i).addDef((String) null); // Adds null values to complete the row
        }
        return this;
    }


    /**
     * Sets the row at the provided index. <br>
     * Values are read from left to right. <br>
     * Note that if there are more columns than you provided values, the values for those columns get set to null. <br>
     *
     * @throws IndexOutOfBoundsException if the row at the provided index does not exist.
     */
    public DYTable setRow(int index, DYValueContainer... values) {
        List<DYColumn> columns = getColumns();
        for (DYColumn col :
                columns) {
            try {
                col.get(index);
            } catch (Exception e) {
                throw new IndexOutOfBoundsException("Row '" + index + "' does not exist for column '" + col.getName() + "' in table '" + getName() + "'.");
            }
        }
        for (int i = 0; i < values.length; i++) {
            columns.get(i).get(index).set(values[i].get());
        }

        for (int i = values.length - 1; i < columns.size() - values.length; i++) {
            columns.get(i).get(index).set((String) null); // Adds null values to complete the row
        }
        return this;
    }

    /**
     * See {@link DreamYaml#add(String...)} for details.
     */
    public DYColumn addColumn(String name) throws NotLoadedException, IllegalKeyException, DuplicateKeyException, DYWriterException, IOException, DYReaderException, IllegalListException {
        DYColumn column = new DYColumn(tableModule.getYaml().add("tables", getName(), name));
        tableModule.getYaml().saveAndLoad();
        return column;
    }

    /**
     * Note that this triggers {@link DreamYaml#saveAndLoad()}, to ensure the database/yaml-file, <br>
     * as well as the parent/child modules of this {@link DreamYamlDB} object are up-to-date. <br>
     * See {@link DreamYaml#put(String...)} for details.
     */
    public DYColumn putColumn(String name) throws NotLoadedException, IllegalKeyException, DYWriterException, IOException, DuplicateKeyException, DYReaderException, IllegalListException {
        DYColumn column = new DYColumn(tableModule.getYaml().put("tables", getName(), name));
        tableModule.getYaml().saveAndLoad();
        return column;
    }

    /**
     * Note that this triggers {@link DreamYaml#saveAndLoad()}, to ensure the database/yaml-file, <br>
     * as well as the parent/child modules of this {@link DreamYamlDB} object are up-to-date. <br>
     * See {@link DreamYaml#remove(DYModule)} for details.
     */
    public DYTable removeColumn(DYColumn column) throws DYWriterException, IOException, DuplicateKeyException, DYReaderException, IllegalListException {
        Objects.requireNonNull(column);
        tableModule.getYaml().remove("tables", getName(), column.getName());
        tableModule.getYaml().saveAndLoad();
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

    /**
     * Columns are ordered in this list the same way they are in the database/yaml-file.
     */
    public List<DYColumn> getColumns() {
        // TODO idk if this is the best way
        List<DYColumn> columns = new ArrayList<>();
        for (DYModule columnModule :
                tableModule.getChildModules()) {
            // Note that these modules must have been already added to the
            // inEditModules list, that's why we do the below (to ensure that):
            columns.add(new DYColumn(tableModule.getYaml().get(columnModule.getKeys())));
        }
        return columns;
    }


    // QUERIES:
    // TODO getValuesSimilarTo(value, minSimilarityInPercent)


    public List<DYRow> getValuesEqualTo(String colName, String value) {
        DYColumn column = getColumn(colName);
        Objects.requireNonNull(column);
        return getValuesEqualTo(column, value);
    }

    /**
     * Compares all values inside the provided column, with the provided value. <br>
     * Matches get added to the list and returned. <br>
     *
     * @param column the column to execute the query in.
     * @param value  the value to search for.
     */
    public List<DYRow> getValuesEqualTo(DYColumn column, String value) {
        List<DYRow> results = new ArrayList<>();
        int index = 0;
        for (DYValueContainer v :
                column.getValues()) {
            if (v.asString() != null && v.asString().equals(value)) {
                // Get the other columns values at the current index position
                Map<DYValueContainer, DYColumn> map = new HashMap<>();
                for (DYColumn col :
                        getColumns()) {
                    map.put(col.get(index), col);
                }
                results.add(new DYRow(index, map));
            }
            index++;
        }
        return results;
    }

    /**
     * Compares all values inside the provided column, with the provided value. <br>
     * Matches get added to the list and returned. <br>
     *
     * @param column the column to execute the query in.
     * @param value  the value to search for.
     */
    public List<DYRow> getValuesBiggerThan(DYColumn column, long value) {
        List<DYRow> results = new ArrayList<>();
        int index = 0;
        for (DYValueContainer v :
                column.getValues()) {
            if (v.asLong() > value) {
                // Get the other columns values at the current index position
                Map<DYValueContainer, DYColumn> map = new HashMap<>();
                for (DYColumn col :
                        getColumns()) {
                    map.put(col.get(index), col);
                }
                results.add(new DYRow(index, map));
            }
            index++;
        }
        return results;
    }

    /**
     * Compares all values inside the provided column, with the provided value. <br>
     * Matches get added to the list and returned. <br>
     *
     * @param column the column to execute the query in.
     * @param value  the value to search for.
     */
    public List<DYRow> getValuesBiggerThan(DYColumn column, double value) {
        List<DYRow> results = new ArrayList<>();
        int index = 0;
        for (DYValueContainer v :
                column.getValues()) {
            if (v.asDouble() > value) {
                // Get the other columns values at the current index position
                Map<DYValueContainer, DYColumn> map = new HashMap<>();
                for (DYColumn col :
                        getColumns()) {
                    map.put(col.get(index), col);
                }
                results.add(new DYRow(index, map));
            }
            index++;
        }
        return results;
    }

    /**
     * Compares all values inside the provided column, with the provided value. <br>
     * Matches get added to the list and returned. <br>
     *
     * @param column the column to execute the query in.
     * @param value  the value to search for.
     */
    public List<DYRow> getValuesSmallerThan(DYColumn column, long value) {
        List<DYRow> results = new ArrayList<>();
        int index = 0;
        for (DYValueContainer v :
                column.getValues()) {
            if (v.asLong() < value) {
                // Get the other columns values at the current index position
                Map<DYValueContainer, DYColumn> map = new HashMap<>();
                for (DYColumn col :
                        getColumns()) {
                    map.put(col.get(index), col);
                }
                results.add(new DYRow(index, map));
            }
            index++;
        }
        return results;
    }

    /**
     * Compares all values inside the provided column, with the provided value. <br>
     * Matches get added to the list and returned. <br>
     *
     * @param column the column to execute the query in.
     * @param value  the value to search for.
     */
    public List<DYRow> getValuesSmallerThan(DYColumn column, double value) {
        List<DYRow> results = new ArrayList<>();
        int index = 0;
        for (DYValueContainer v :
                column.getValues()) {
            if (v.asDouble() < value) {
                // Get the other columns values at the current index position
                Map<DYValueContainer, DYColumn> map = new HashMap<>();
                for (DYColumn col :
                        getColumns()) {
                    map.put(col.get(index), col);
                }
                results.add(new DYRow(index, map));
            }
            index++;
        }
        return results;
    }
}
