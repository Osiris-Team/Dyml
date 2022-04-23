package com.osiris.dyml.db;

import com.osiris.dyml.Yaml;
import com.osiris.dyml.YamlSection;
import com.osiris.dyml.exceptions.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

/**
 * A single DreamYamlDB (DreamYamlDatabase) object, represents a single database. <br>
 * To be more exact, its just a single yaml file with a structure like this: <br>
 * <pre>
 * tables:
 *   table_potatoes:
 *     column1_potato_names:
 *       - Russet                     # First row
 *       - Jewel Yam                  # Second row
 *       -                            # Third row
 *     column2_potato_description:
 *       - Most people know this one as the "classic potato".
 *       -    # This means there is no value, thus it returns null
 *       - "" # Note that this also returns null, there are no empty values
 *   another_table:
 *     column1:
 *       - ....
 * </pre>
 * This class provides several database related methods, that don't exist
 * in the regular {@link Yaml} class. <br>
 * Note that this database is persistent, but you will have to call {@link #save()} manually to achieve this. <br>
 * Also note that the database (yaml file) gets loaded into memory, thus changing/retrieving/deleting and generally working
 * with values is a lot faster compared to regular databases, but this also means that its more memory intensive. <br>
 * That's why DreamYamlDB is perfect for working with small to medium amounts of data.
 */
public class YamlDatabase {
    private Yaml yaml;
    public List<YamlTable> tables = new CopyOnWriteArrayList<>();
    public Thread threadAutoSave;

    /**
     * Creates a new yaml file in the current working directory, with a random, unused name.
     */
    public YamlDatabase() {
        String name = "DreamYaml-DB-" + new Random().nextInt(10000000);
        File yamlFile = null;
        for (int i = 1; i < 11; i++) {
            try {
                yamlFile = new File(System.getProperty("user.dir") + "/" + name + ".yml");
                if (!yamlFile.exists())
                    break;
                else
                    name = "DreamYaml-DB-" + new Random().nextInt(10000000);
            } catch (Exception ignored) {
            }
        }
        init(yamlFile);
    }

    /**
     * Creates a yml file in the current working directory with the provided name
     * and uses that as the database. <br>
     *
     * @param name of the database.
     */
    public YamlDatabase(String name) {
        init(new File(System.getProperty("user.dir") + "/" + name + ".yml"));
    }

    public YamlDatabase(Path yamlFilePath) {
        init(yamlFilePath.toFile());
    }

    public YamlDatabase(File yamlFile) {
        init(yamlFile);
    }

    public YamlDatabase(Yaml yaml) {
        init(yaml);
    }

    private void init(File yamlFile) {
        init(new Yaml(yamlFile));
    }

    private void init(Yaml yaml) {
        Objects.requireNonNull(yaml);
        this.yaml = yaml;
        yaml.isRemoveLoadedNullValuesEnabled = false;
    }

    /**
     * Starts a thread that executes {@link #save()} periodically. <br>
     * @param msIntervall the amount of time in milliseconds, for the thread to wait until attempting a save operation.
     */
    public void initAutoSaveThread(long msIntervall, Consumer<Exception> onException){
        if(threadAutoSave==null || threadAutoSave.isInterrupted())
            threadAutoSave = new Thread(() -> {
               try{
                   while (true){
                       Thread.sleep(msIntervall);
                       save();
                   }
               } catch (Exception e) {
                   onException.accept(e);
               }
            });
    }

    public Yaml getYaml() {
        return yaml;
    }

    /**
     * This is the first thing you should do after initialising. <br>
     * See {@link Yaml#load()} for details.
     */
    public YamlDatabase load() throws IOException, DuplicateKeyException, YamlReaderException, IllegalListException {
        yaml.load();
        return this;
    }

    /**
     * See {@link Yaml#save()} for details.
     */
    public YamlDatabase save() throws YamlWriterException, IOException, DuplicateKeyException, YamlReaderException, IllegalListException {
        yaml.save();
        return this;
    }

    /**
     * See {@link Yaml#saveAndLoad()} for details.
     */
    public YamlDatabase saveAndLoad() throws YamlWriterException, IOException, DuplicateKeyException, YamlReaderException, IllegalListException {
        yaml.saveAndLoad();
        return this;
    }

    /**
     * Note that this triggers {@link #saveAndLoad()}, to ensure the database/yaml-file, <br>
     * as well as the parent/child modules of this {@link YamlDatabase} object are up-to-date. <br>
     * See {@link Yaml#add(String...)} for details.
     */
    public YamlTable addTable(String name) throws NotLoadedException, IllegalKeyException, DuplicateKeyException, YamlWriterException, IOException, YamlReaderException, IllegalListException {
        YamlTable table = new YamlTable(yaml.add("tables", name));
        yaml.saveAndLoad();
        tables.add(table); // Exception is thrown above if already exists.
        return table;
    }

    /**
     * Note that this triggers {@link #saveAndLoad()}, to ensure the database/yaml-file, <br>
     * as well as the parent/child modules of this {@link YamlDatabase} object are up-to-date. <br>
     * See {@link Yaml#put(String...)} for details.
     */
    public YamlTable putTable(String name) throws NotLoadedException, IllegalKeyException, YamlWriterException, IOException, DuplicateKeyException, YamlReaderException, IllegalListException {
        yaml.saveAndLoad();
        YamlTable tExisting = null;
        for (YamlTable t : tables) {
            if(t.getName().equals(name)){
                tExisting = t;
                break;
            }
        }
        if(tExisting == null){
            YamlTable newTable = new YamlTable(yaml.put("tables", name));
            tables.add(newTable);
            return newTable;
        } else{
            return tExisting;
        }
    }

    /**
     * Note that this triggers {@link #saveAndLoad()}, to ensure the database/yaml-file, <br>
     * as well as the parent/child modules of this {@link YamlDatabase} object are up-to-date. <br>
     * See {@link Yaml#remove(YamlSection)} for details. <br>
     */
    public YamlDatabase removeTable(YamlTable table) {
        Objects.requireNonNull(table);
        yaml.remove("tables", table.getName());
        YamlTable tRemove = null;
        for (YamlTable t : tables) {
            if(table.equals(t) || t.getName().equals(table.getName())){
                tRemove = t;
                break;
            }
        }
        if(tRemove != null) tables.remove(tRemove);
        return this;
    }

    /**
     * Note that the returned {@link YamlTable} object is not persistent. <br>
     * That means that when you call this method for the same table again,
     * another {@link YamlTable} object is returned. <br>
     */
    public YamlTable getTable(String name) {
        List<YamlTable> tables = getTables();
        for (YamlTable t :
                tables) {
            if (t.getName().equals(name))
                return t;
        }
        return null;
    }

    public YamlTable getTableAtIndex(int index) {
        return getTables().get(index);
    }

    public List<YamlTable> getTables() {
        return tables;
    }

}
