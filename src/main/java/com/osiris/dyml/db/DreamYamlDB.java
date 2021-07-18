package com.osiris.dyml.db;

import com.osiris.dyml.DYModule;
import com.osiris.dyml.DreamYaml;
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
    }

    /**
     * This is the first thing you should do after initialising. <br>
     * See {@link DreamYaml#load()} for details.
     */
    public DreamYamlDB load() throws IOException, DuplicateKeyException, DYReaderException, IllegalListException {
        yaml.load();
        return this;
    }

    public DreamYamlDB save() throws DYWriterException, IOException, DuplicateKeyException, DYReaderException, IllegalListException {
        yaml.save();
        return this;
    }

    /**
     * Note that duplicate names in the same database are not allowed.
     */
    public DYTable addTable(String name) throws NotLoadedException, IllegalKeyException {
        DYTable table = new DYTable(yaml, name);
        addTable(table);
        return table;
    }

    /**
     * Note that duplicate names in the same database are not allowed.
     */
    public DreamYamlDB addTable(DYTable table) throws NotLoadedException, IllegalKeyException {
        Objects.requireNonNull(table);
        yaml.put("tables", table.getName());
        return this;
    }

    public DreamYamlDB removeTable(DYTable table) {
        Objects.requireNonNull(table);
        yaml.remove("tables", table.getName());
        return this;
    }

    public DYTable getTableByName(String name){
        List<DYTable> tables = getTables();
        for (DYTable t :
                tables) {
            if (t.getName().equals(name))
                return t;
        }
        return null;
    }

    public DYTable getTableAtIndex(int index){
        return getTables().get(index);
    }

    public List<DYTable> getTables(){
        List<DYTable> tables = new ArrayList<>();
        for (DYModule tableModule :
                yaml.get("tables").getChildModules()) {
            String name = tableModule.getLastKey();
            List<DYColumn> columns = new ArrayList<>();
            for (DYModule columnModule :
                 tableModule.getChildModules()) {
                columns.add(new DYColumn(yaml, columnModule.getLastKey()));
            }
            tables.add(new DYTable(yaml, name, columns));
        }
        return tables;
    }

}
