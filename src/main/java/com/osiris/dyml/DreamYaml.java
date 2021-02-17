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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * The in-memory representation of the full yaml file
 * that contains all of the default and loaded modules.
 */
public class DreamYaml {
    private String filePath;
    private File file;
    private List<DYModule> loadedModules;
    private List<DYModule> addedModules;
    //private List<DYModule> unifiedModules;
    private boolean debug;

    /**
     * See {@link #DreamYaml(String, boolean)} for details.
     */
    public DreamYaml(File file){
        this(file.getAbsolutePath());
    }

    /**
     * See {@link #DreamYaml(String, boolean)} for details.
     */
    public DreamYaml(File file, boolean debug){
        this(file.getAbsolutePath(), debug);
    }

    /**
     * See {@link #DreamYaml(String, boolean)} for details.
     */
    public DreamYaml(String filePath) {
        this(filePath,false);
    }

    /**
     * Creates a new DreamYaml object.
     * Next step would be to {@link #load()} your file into memory.
     * @param filePath your yaml files path.
     * @param debug show debugging stuff. Default is false.
     */
    public DreamYaml(String filePath, boolean debug) {
        this.filePath = filePath;
        this.addedModules = new ArrayList<>();
        this.debug = debug;
    }

    /**
     * Loads the file into memory by parsing
     * it into modules({@link DYModule}). Creates a new file if it didn't exist already.
     * You can return the list of modules with {@link #getAllLoaded()}.
     * Remember, that this updates your added modules values.
     */
    public DreamYaml load() throws Exception {
        this.loadedModules = new ArrayList<>();
        file = new File(filePath);
        if (!file.exists()) file.createNewFile();
        new DYReader().parse(this);
        return this;
    }

    /**
     * Caution! This method will completely reset your yaml file, but not delete it.
     * To delete, use {@link File#delete()} instead. You can get the file via {@link #getFile()}.
     * Also the {@link #getAllLoaded()} list is empty after this operation.
     * The {@link #getAllAdded()} list is not affected.
     */
    public DreamYaml reset() throws Exception{
        if (file==null) this.load();
        new DYWriter().parse(this, true, true);
        this.load();
        return this;
    }

    /**
     * Convenience method for saving and loading afterwards.
     */
    public DreamYaml reload() throws Exception{
        if (file==null) this.load();
        this.save();
        this.load();
        return this;
    }

    /**
     * Parses and saves the current modules to the provided yaml file.
     * If the file misses modules, these get created using their default values.
     * See {@link DYModule#setDefValues(List)} and {@link UtilsForModules#createUnifiedList(List, List)} for more details.
     * It's recommended to keep {@link #load()} and {@link #save()} timely close to each other, so the user
     * can't change the values in the meantime.
     * IMPORTANT: Stuff that isn't supported by DreamYaml (see features.yml) wont be parsed and thus removed from the file after you save it!
     * @param overwrite If true the yaml file gets overwritten with modules from the 'added modules list'.
     *                 That means that everything that wasn't added via {@link #add(String...)} will not exist in the file.
     *                  Default is false.
     */
    public DreamYaml save(boolean overwrite) throws Exception {
        if (file==null) this.load();
        new DYWriter().parse(this, overwrite, false);
        return this;
    }

    /**
     * For more details see: {@link #save(boolean)}
     */
    public DreamYaml save() throws Exception{
        this.save(false);
        return this;
    }

    /**
     * Creates a new {@link DYModule}, adds it to the modules list and returns it.
     * See {@link #add(DYModule)} for details.
     */
    public DYModule add(String... keys) throws Exception {
        if (keys==null) throw new Exception("Keys of this module cannot be null!");
        List<String> list = new ArrayList<>();
        list.addAll(Arrays.asList(keys));
        return add(list,null, null, null);
    }

    /**
     * Creates a new {@link DYModule}, adds it to the modules list and returns it.
     * See {@link #add(DYModule)} for details.
     */
    public DYModule add(List<String> keys, List<String> defaultValues, List<String> values, List<String> comments) throws Exception {
        return add(new DYModule(keys, defaultValues, values, comments));
    }

    /**
     * Adds the given module to the modules list, which will get parsed and written to file by {@link #save()}.
     * Doing changes to this modules values and saving them, will affect the original yaml file.
     * Note that null KEYS are not allowed!
     * @param module module to add.
     * @return the added module.
     * @throws NotLoadedException if the yaml file has not been loaded once yet
     * @throws DuplicateKeyException if another module with the same keys already exists
     */
    public DYModule add(DYModule module) throws Exception {
        if (module.getKeys()==null || module.getKeys().isEmpty()) throw new Exception("Keys list of this module is null or empty!");
        if (file==null) throw new NotLoadedException(); // load() should've been called at least once before
        if (module.getKeys().contains(null)) throw new Exception("Null keys are not allowed!");
        UtilsForModules utils = new UtilsForModules();
        if (utils.getExisting(module, this.addedModules)!=null) // Check for the same keys in the defaultModules list. Same keys are not allowed.
            throw new DuplicateKeyException(file.getName(), module.getKeys().toString());

        DYModule loaded = utils.getExisting(module, this.loadedModules);
        if (loaded!=null) {
            module.setValues(loaded.getValues());
        }

        this.addedModules.add(module);
        return module;
    }

    /**
     * Convenience method for removing a module from the 'added modules list'.
     * If you call {@link #save()} after this, the module should also
     * be removed from the yaml file.
     * @param module the module to remove.
     */
    public void remove(DYModule module){
        this.addedModules.remove(module);
    }

    /**
     * Returns a list containing all loaded modules.
     * It is a temporary list which gets refreshed every time {@link #load()} is called.
     * Its modules, do not contain default values.
     */
    public List<DYModule> getAllLoaded() {
        return loadedModules;
    }

    /**
     * Convenience method for returning the last module from the 'loaded modules list'.
     */
    public DYModule getLastLoadedModule(){
        return loadedModules.get(loadedModules.size()-1);
    }

    /**
     * Returns a list containing all currently added modules.
     * Modules should only be added by {@link #add(String...)} and never by this lists own add() method.
     * This list is not affected by {@link #load()}, unlike the
     * 'loaded modules' list, which can be returned by {@link #getAllLoaded()}.
     */
    public List<DYModule> getAllAdded() {
        return addedModules;
    }

    /**
     * Convenience method for returning the last module from the 'added modules list'.
     */
    public DYModule getLastAddedModule(){
        return addedModules.get(addedModules.size()-1);
    }

    /**
     * Prints out all lists.
     */
    public void printAll(){
        printLoaded();
        printAdded();
        printUnified();
        System.out.println(" ");
    }

    /**
     * Prints out all modules in the loaded list.
     * For more info see {@link #getAllLoaded()}.
     */
    public void printLoaded(){
        System.out.println(" ");
        System.out.println("Printing LOADED modules from '"+file.getName()+"' file:");
        for (DYModule module :
                getAllLoaded()) {
            module.print();
        }
    }

    /**
     * Prints out all modules in the added list.
     * For more info see {@link #getAllAdded()}.
     */
    public void printAdded(){
        System.out.println(" ");
        System.out.println("Printing ADDED modules from '"+file.getName()+"' file:");
        for (DYModule module :
                getAllAdded()) {
            module.print();
        }
    }

    /**
     * Prints out all modules in the unified list.
     * For more info see {@link UtilsForModules#createUnifiedList(List, List)}.
     */
    public void printUnified(){
        System.out.println(" ");
        System.out.println("Printing UNIFIED modules from '"+file.getName()+"' file:");
        for (DYModule module :
                new UtilsForModules().createUnifiedList(getAllAdded(),getAllLoaded())) {
            module.print();
        }
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

    /**
     * Returns the module with same keys from the 'added modules list'.
     * Details: {@link #getAllAdded()}
     * @return {@link DYModule} or null if no module found with same keys
     */
    public DYModule getAddedModuleByKeys(String... keys) {
        List<String> list = new ArrayList<>();
        list.addAll(Arrays.asList(keys));
        if (!list.isEmpty())
            return getAddedModuleByKeys(list);
        else
            return null;
    }

    /**
     * Returns the module with same keys from the 'added modules list'.
     * Details: {@link #getAllAdded()}
     * @return {@link DYModule} or null if no module found with same keys
     */
    public DYModule getAddedModuleByKeys(List<String> keys) {
        return new UtilsForModules().getExisting(keys, addedModules);
    }

    /**
     * Returns the module with same keys from the 'loaded modules list'.
     * Details: {@link #getAllLoaded()}
     * @return {@link DYModule} or null if no module found with same keys
     */
    public DYModule getLoadedModuleByKeys(String... keys) {
        List<String> list = new ArrayList<>();
        list.addAll(Arrays.asList(keys));
        if (!list.isEmpty())
            return getLoadedModuleByKeys(list);
        else
            return null;
    }

    /**
     * Returns the module with same keys from the 'loaded modules list'.
     * Details: {@link #getAllLoaded()}
     * @return {@link DYModule} or null if no module found with same keys
     */
    public DYModule getLoadedModuleByKeys(List<String> keys) {
        return new UtilsForModules().getExisting(keys, loadedModules);
    }
}

