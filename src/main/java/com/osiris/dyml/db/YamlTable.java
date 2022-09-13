package com.osiris.dyml.db;

import com.osiris.dyml.SmartString;
import com.osiris.dyml.Yaml;
import com.osiris.dyml.YamlSection;
import com.osiris.dyml.exceptions.*;

import java.io.IOException;
import java.util.*;

public class YamlTable {
    private final YamlSection tableModule;

    public YamlTable(YamlSection tableModule) {
        Objects.requireNonNull(tableModule);
        this.tableModule = tableModule;
    }

    public YamlSection getTableModule() {
        return tableModule;
    }

    public String getName() {
        return tableModule.getLastKey();
    }

    /**
     * Returns the {@link YamlRow} at the provided index.
     *
     * @throws IndexOutOfBoundsException when the row at the provided index does not exist.
     */
    public YamlRow getRow(int index) {
        Map<SmartString, YamlColumn> valuesAndColumns = new HashMap<>();
        for (YamlColumn col :
                getColumns()) {
            valuesAndColumns.put(col.get(index), col);
        }
        return new YamlRow(index, valuesAndColumns);
    }

    /**
     * Returns the {@link YamlRow} at the provided index, as a list of {@link SmartString}s. <br>
     *
     * @throws IndexOutOfBoundsException when the row at the provided index does not exist.
     */
    public List<SmartString> getRowAsList(int index) {
        List<SmartString> row = new ArrayList<>();
        for (YamlColumn col :
                getColumns()) {
            row.add(col.get(index));
        }
        return row;
    }

    /**
     * Tries to add a {@link YamlRow} to the table. <br>
     * If there are missing values to fill the row, null values get added. <br>
     * Note that all columns must have the same length. <br>
     *
     * @throws IndexOutOfBoundsException if there are more values than columns available.
     * @throws BrokenColumnsException    if the columns have different sizes/lengths.
     */
    public YamlTable addRow(String... values) {
        List<YamlColumn> columns = getColumns();
        List<YamlColumn> columnsCopy = new ArrayList<>(columns);

        // Check for different column lengths
        for (YamlColumn col :
                columns) {
            for (YamlColumn col1 :
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
     * Tries to add a {@link YamlRow} to the table. <br>
     * If there are missing values to fill the row, null values get added. <br>
     * Note that all columns must have the same length. <br>
     *
     * @throws IndexOutOfBoundsException if there are more values than columns available.
     * @throws BrokenColumnsException    if the columns have different sizes/lengths.
     */
    public YamlTable addDefRow(String... values) {
        List<YamlColumn> allColumns = getColumns();
        List<YamlColumn> columnsCopy = new ArrayList<>(allColumns);

        // Check for different column lengths
        for (YamlColumn col :
                allColumns) {
            for (YamlColumn col1 :
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
    public YamlTable setRow(int index, SmartString... values) {
        List<YamlColumn> columns = getColumns();
        for (YamlColumn col :
                columns) {
            try {
                col.get(index);
            } catch (Exception e) {
                throw new IndexOutOfBoundsException("Row '" + index + "' does not exist for column '" + col.getName() + "' in table '" + getName() + "'.");
            }
        }
        for (int i = 0; i < values.length; i++) {
            columns.get(i).get(index).set(values[i].asString());
        }

        for (int i = values.length - 1; i < columns.size() - values.length; i++) {
            columns.get(i).get(index).set((String) null); // Adds null values to complete the row
        }
        return this;
    }

    /**
     * See {@link Yaml#add(String...)} for details.
     */
    public YamlColumn addColumn(String name) throws NotLoadedException, IllegalKeyException, DuplicateKeyException, YamlWriterException, IOException, YamlReaderException, IllegalListException {
        YamlColumn column = new YamlColumn(tableModule.getYaml().add("tables", getName(), name));
        tableModule.getYaml().saveAndLoad();
        return column;
    }

    /**
     * Note that this triggers {@link Yaml#saveAndLoad()}, to ensure the database/yaml-file, <br>
     * as well as the parent/child modules of this {@link YamlDatabase} object are up-to-date. <br>
     * See {@link Yaml#put(String...)} for details.
     */
    public YamlColumn putColumn(String name) throws NotLoadedException, IllegalKeyException, YamlWriterException, IOException, DuplicateKeyException, YamlReaderException, IllegalListException {
        YamlColumn column = new YamlColumn(tableModule.getYaml().put("tables", getName(), name));
        tableModule.getYaml().saveAndLoad();
        return column;
    }

    /**
     * Note that this triggers {@link Yaml#saveAndLoad()}, to ensure the database/yaml-file, <br>
     * as well as the parent/child modules of this {@link YamlDatabase} object are up-to-date. <br>
     * See {@link Yaml#remove(YamlSection)} for details.
     */
    public YamlTable removeColumn(YamlColumn column) throws YamlWriterException, IOException, DuplicateKeyException, YamlReaderException, IllegalListException {
        Objects.requireNonNull(column);
        tableModule.getYaml().remove("tables", getName(), column.getName());
        tableModule.getYaml().saveAndLoad();
        return this;
    }

    public YamlColumn getColumn(String name) {
        for (YamlColumn c :
                getColumns()) {
            if (c.getName().equals(name))
                return c;
        }
        return null;
    }

    public YamlColumn getColumnAtIndex(int index) {
        return getColumns().get(index);
    }

    /**
     * Columns are ordered in this list the same way they are in the database/yaml-file.
     */
    public List<YamlColumn> getColumns() {
        // TODO idk if this is the best way
        List<YamlColumn> columns = new ArrayList<>();
        for (YamlSection columnModule :
                tableModule.getChildSections()) {
            // Note that these modules must have been already added to the
            // inEditModules list, that's why we do the below (to ensure that):
            columns.add(new YamlColumn(tableModule.getYaml().get(columnModule.getKeys())));
        }
        return columns;
    }


    // QUERIES:
    // TODO getValuesSimilarTo(value, minSimilarityInPercent)


    public List<YamlRow> getValuesEqualTo(String colName, String value) {
        YamlColumn column = getColumn(colName);
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
    public List<YamlRow> getValuesEqualTo(YamlColumn column, String value) {
        List<YamlRow> results = new ArrayList<>();
        int index = 0;
        for (SmartString v :
                column.getValues()) {
            if (v.asString() != null && v.asString().equals(value)) {
                // Get the other columns values at the current index position
                Map<SmartString, YamlColumn> map = new HashMap<>();
                for (YamlColumn col :
                        getColumns()) {
                    map.put(col.get(index), col);
                }
                results.add(new YamlRow(index, map));
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
    public List<YamlRow> getValuesBiggerThan(YamlColumn column, long value) {
        List<YamlRow> results = new ArrayList<>();
        int index = 0;
        for (SmartString v :
                column.getValues()) {
            if (v.asLong() > value) {
                // Get the other columns values at the current index position
                Map<SmartString, YamlColumn> map = new HashMap<>();
                for (YamlColumn col :
                        getColumns()) {
                    map.put(col.get(index), col);
                }
                results.add(new YamlRow(index, map));
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
    public List<YamlRow> getValuesBiggerThan(YamlColumn column, double value) {
        List<YamlRow> results = new ArrayList<>();
        int index = 0;
        for (SmartString v :
                column.getValues()) {
            if (v.asDouble() > value) {
                // Get the other columns values at the current index position
                Map<SmartString, YamlColumn> map = new HashMap<>();
                for (YamlColumn col :
                        getColumns()) {
                    map.put(col.get(index), col);
                }
                results.add(new YamlRow(index, map));
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
    public List<YamlRow> getValuesSmallerThan(YamlColumn column, long value) {
        List<YamlRow> results = new ArrayList<>();
        int index = 0;
        for (SmartString v :
                column.getValues()) {
            if (v.asLong() < value) {
                // Get the other columns values at the current index position
                Map<SmartString, YamlColumn> map = new HashMap<>();
                for (YamlColumn col :
                        getColumns()) {
                    map.put(col.get(index), col);
                }
                results.add(new YamlRow(index, map));
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
    public List<YamlRow> getValuesSmallerThan(YamlColumn column, double value) {
        List<YamlRow> results = new ArrayList<>();
        int index = 0;
        for (SmartString v :
                column.getValues()) {
            if (v.asDouble() < value) {
                // Get the other columns values at the current index position
                Map<SmartString, YamlColumn> map = new HashMap<>();
                for (YamlColumn col :
                        getColumns()) {
                    map.put(col.get(index), col);
                }
                results.add(new YamlRow(index, map));
            }
            index++;
        }
        return results;
    }
}
