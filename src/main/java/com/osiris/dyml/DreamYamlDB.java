package com.osiris.dyml;

import com.osiris.dyml.exceptions.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

// TODO
public class DreamYamlDB {
    private File yamlFile;
    private DreamYaml yaml;
    private List<DYTable> tables;

    class DYTable{
        private String name;
        private List<DYColumn> columns;

        public DYTable(String name){
            this(name, null);
        }

        public DYTable(String name, List<DYColumn> columns) {
            Objects.requireNonNull(name);
            this.name = name;
            if (columns==null) columns = new ArrayList<>();
            this.columns = columns;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public List<DYColumn> getColumns() {
            return columns;
        }

        public void setColumns(List<DYColumn> columns) {
            this.columns = columns;
        }
    }

    class DYColumn{

    }

    /**
     * Creates a new yml file in the current working directory, with a random, unused name.
     */
    public DreamYamlDB() throws IOException, DuplicateKeyException, DYReaderException, IllegalListException {
        String name = "DB-"+new Random().nextInt(10000000);
        File yamlFile = null;
        for (int i = 1; i < 11; i++) {
            try{
                yamlFile = new File(System.getProperty("user.dir")+"/"+name+".yml");
                if (!yamlFile.exists())
                    break;
                else
                    name = "DB-"+new Random().nextInt(10000000);
            } catch (Exception ignored) { }
        }
        init(yamlFile);
    }

    /**
     * Creates a yml file in the current working directory with the provided name
     * and uses that as the database. <br>
     * @param name of the database.
     */
    public DreamYamlDB(String name) throws IOException, DuplicateKeyException, DYReaderException, IllegalListException {
        init(new File(System.getProperty("user.dir")+"/"+name+".yml"));
    }

    public DreamYamlDB(Path yamlFilePath) throws IOException, DuplicateKeyException, DYReaderException, IllegalListException {
        init(yamlFilePath.toFile());
    }

    public DreamYamlDB(File yamlFile) throws IOException, DuplicateKeyException, DYReaderException, IllegalListException {
        init(yamlFile);
    }

    private void init(File yamlFile) throws IOException, DuplicateKeyException, DYReaderException, IllegalListException {
        Objects.requireNonNull(yamlFile);
        this.yamlFile = yamlFile;
        this.yaml = new DreamYaml(yamlFile);
        yaml.load();
    }

    public DreamYamlDB addTable(DYTable table) throws NotLoadedException, IllegalKeyException, DYWriterException, IOException, DuplicateKeyException, DYReaderException, IllegalListException {
        Objects.requireNonNull(table);
        yaml.put("tables", table.getName());
        yaml.saveAndLoad();
        tables.add(table);
        return this;
    }

    public DreamYamlDB removeTable(DYTable table) throws DYWriterException, IOException, DuplicateKeyException, DYReaderException, IllegalListException {
        Objects.requireNonNull(table);
        yaml.remove("tables", table.getName());
        yaml.saveAndLoad();
        tables.remove(table);
        return this;
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

}
