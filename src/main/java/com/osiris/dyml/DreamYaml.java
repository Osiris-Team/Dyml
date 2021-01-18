/*
 *  Copyright Osiris Team
 *  All rights reserved.
 *
 *  This software is licensed work.
 *  Please consult the file "LICENSE" for details.
 */

package com.osiris.dyml;

import com.osiris.dyml.exceptions.DuplicateKeyException;
import com.osiris.dyml.exceptions.NotLoadedException;
import com.osiris.dyml.utils.UtilsForModules;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * The in-memory representation of the full yaml file
 * that contains all of the default and loaded modules.
 */
public class DreamYaml {
    private String filePath;
    private File file;
    private List<DYModule> loadedModules;
    private List<DYModule> defaultModules;
    //private List<DYModule> unifiedModules;
    private boolean debug;


    public DreamYaml(File file){
        this(file.getAbsolutePath());
    }

    public DreamYaml(File file, boolean debug){
        this(file.getAbsolutePath(), debug);
    }

    public DreamYaml(String filePath) {
        this(filePath,false);
    }

    public DreamYaml(String filePath, boolean debug) {
        this.filePath = filePath;
        this.defaultModules = new ArrayList<>();
        this.debug = debug;
    }

    /**
     * Loads the file into memory by parsing
     * it into modules({@link DYModule}). Creates a new file if it didn't exist already.
     * You can return the list of modules with {@link #getAllLoaded()}.
     * @throws IOException
     */
    public DreamYaml load() throws IOException {
        this.loadedModules = new ArrayList<>();
        file = new File(filePath);
        if (!file.exists()) file.createNewFile();
        new DYReader().parse(this);
        return this;
    }

    /**
     * Saves the current modules and their values to the provided yaml file.
     * Everything gets overwritten and stuff that couldn't be parsed into modules gets removed from the file.
     * Missing modules in the file, get created using their default values.
     * See {@link DYModule#setDefValues(List)} and {@link UtilsForModules#createUnifiedList(List, List)} for more details.
     * It's recommended to keep {@link #load()} and {@link #save()} timely close to each other, so the user
     * can't change the values in the meantime.
     * @throws NotLoadedException
     */
    public DreamYaml save() throws NotLoadedException {
        if (file==null) throw new NotLoadedException();
        new DYWriter().parse(this);
        return this;
    }

    /**
     * Creates a new {@link DYModule} and adds it to the modules list.
     * {@link #add(DYModule)}
     * @return the newly created module.
     */
    public DYModule add(String... keys) throws Exception {
        if (keys==null) throw new Exception("Keys of this module cannot be null!");
        List<String> list = new ArrayList<>();
        list.addAll(Arrays.asList(keys));
        return add(list,null, null, null);
    }

    /**
     * Creates a new {@link DYModule} and adds it to this config file.
     * {@link #add(DYModule)}
     */
    public DYModule add(List<String> keys, List<String> defaultValues, List<String> values, List<String> comments) throws Exception {
        return add(new DYModule(keys, defaultValues, values, comments));
    }

    /**
     * Adds a module to the list, which will get parsed and written to file by {@link #save()}.
     * Doing changes to this modules values and saving them, will affect the original yaml file.
     * @param module module to add.
     * @return the added module.
     * @throws NotLoadedException if the yaml file has not been loaded once yet
     * @throws DuplicateKeyException if another module with the same keys already exists
     */
    public DYModule add(DYModule module) throws Exception {
        if (module.getKeys()==null) throw new Exception("Keys of this module cannot be null!");
        if (file==null) throw new NotLoadedException(); // load() should've been called at least once before
        UtilsForModules utils = new UtilsForModules();
        if (utils.getExisting(module, this.defaultModules)!=null) // Check for the same keys in the defaultModules list. Same keys are not allowed.
            throw new DuplicateKeyException(file.getName(), module.getKeys().toString());

        DYModule loaded = utils.getExisting(module, this.loadedModules);
        if (loaded!=null) {
            module.setValues(loaded.getValues());
        }

        this.defaultModules.add(module);
        return module;
    }

    /**
     * Removes a module the module.
     * If you call {@link #save()} after this, the module should also
     * be removed from the yaml file.
     * @param module the module to remove.
     */
    public void remove(DYModule module){
        this.defaultModules.remove(module);
    }

    /**
     * Returns a list containing all loaded modules.
     * It is a temporary list which gets refreshed every time {@link #load()} is called.
     */
    public List<DYModule> getAllLoaded() {
        return loadedModules;
    }

    public DYModule getLastLoadedModule(){
        return loadedModules.get(loadedModules.size()-1);
    }

    /**
     * Returns a list containing all modules,
     * which should be added by {@link #add(String...)} and not this lists own add() method.
     * This list is not affected by {@link #load()}, unlike the
     * 'loaded modules' list, which can be returned by {@link #getAllLoaded()}.
     */
    public List<DYModule> getAll() {
        return defaultModules;
    }

    public DYModule getLastDefModule(){
        return defaultModules.get(defaultModules.size()-1);
    }

    public void printAll(){
        System.out.println(" ");
        System.out.println("Printing loaded modules from '"+file.getName()+"' file:");
        for (DYModule module :
                getAllLoaded()) {
            System.out.println("KEYS: " + module.getKeys().toString() + " VALUES: " + module.getValues().toString() + " COMMENTS: " + module.getComments().toString());
        }
        System.out.println("Printing default modules from '"+file.getName()+"' file:");
        for (DYModule module :
                getAll()) {
            System.out.println("KEYS: " + module.getKeys().toString() + " VALUES: " + module.getValues().toString() + " COMMENTS: " + module.getComments().toString());
        }
        System.out.println(" ");
    }

    public String getFilePath() {
        return filePath;
    }

    public File getFile() {
        return file;
    }

    /**
     * Returns the yml files name without its extension.
     */
    public String getFileNameWithoutExt() throws NotLoadedException{
        if (file==null) throw new NotLoadedException();
        return file.getName().replaceFirst("[.][^.]+$",""); // Removes the file extension
    }

    public boolean isDebug() {
        return debug;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    private void duplicateCheck(List<DYModule> modules, DYModule queryModule) throws DuplicateKeyException {
        for (DYModule listModule :
                modules) {
            if (listModule.getKeys().equals(queryModule.getKeys()))
                throw new DuplicateKeyException(file.getName(), queryModule.getKey());
        }
    }

    public DYModule getModuleByKeys(String... keys) {
        List<String> list = new ArrayList<>();
        list.addAll(Arrays.asList(keys));
        if (!list.isEmpty())
            return getModuleByKeys(list);
        else
            return null;
    }

    public DYModule getModuleByKeys(List<String> keys) {
        return new UtilsForModules().getExisting(keys, getAll());
    }
}

