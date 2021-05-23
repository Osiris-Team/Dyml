/*
 *  Copyright Osiris Team
 *  All rights reserved.
 *
 *  This software is licensed work.
 *  Please consult the file "LICENSE" for details.
 */

package com.osiris.dyml;

import com.osiris.dyml.exceptions.DYReaderException;
import com.osiris.dyml.exceptions.DuplicateKeyException;
import com.osiris.dyml.exceptions.IllegalListException;
import com.osiris.dyml.exceptions.NotLoadedException;
import com.osiris.dyml.utils.UtilsDYModule;
import com.osiris.dyml.utils.UtilsDreamYaml;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * The in-memory representation of the full yaml file
 * that contains all of the default and loaded modules.
 */
public class DreamYaml {
    //private List<DYModule> unifiedModules;
    private final UtilsDreamYaml utils = new UtilsDreamYaml(this);
    private final String filePath;
    private final List<DYModule> addedModules;
    private File file;
    private List<DYModule> loadedModules;
    private boolean postProcessingEnabled;
    private boolean debugEnabled;
    private boolean autoLoadEnabled;


    /**
     * Initialises the {@link DreamYaml} object with useful features enabled. <br>
     * See {@link #DreamYaml(String, boolean, boolean, boolean)} for details.
     */
    public DreamYaml(File file) throws IOException, DYReaderException, IllegalListException {
        this(file.getAbsolutePath());
    }

    /**
     * Initialises the {@link DreamYaml} object with useful features enabled. <br>
     * See {@link #DreamYaml(String, boolean, boolean, boolean)} for details.
     */
    public DreamYaml(File file, boolean debugEnabled) throws IOException, DYReaderException, IllegalListException {
        this(file.getAbsolutePath(), true, debugEnabled, true);
    }

    /**
     * Initialises the {@link DreamYaml} object with useful features enabled. <br>
     * See {@link #DreamYaml(String, boolean, boolean, boolean)} for details.
     */
    public DreamYaml(String filePath) throws IOException, DYReaderException, IllegalListException {
        this(filePath, true, false, true);
    }

    /**
     * Initialises the {@link DreamYaml} object with useful features enabled. <br>
     * See {@link #DreamYaml(String, boolean, boolean, boolean)} for details.
     */
    public DreamYaml(String filePath, boolean debugEnabled) throws IOException, DYReaderException, IllegalListException {
        this(filePath, true, debugEnabled, true);
    }

    /**
     * Initialises the {@link DreamYaml} object.
     *
     * @param filePath              Your yaml files path.
     * @param postProcessingEnabled Enabled by default.
     *                              Responsible for removing "" and '' from your values.
     * @param debugEnabled          Disabled by default. Shows debugging stuff.
     * @param autoLoadEnabled       Enabled by default. Calls {@link #load()} inside of the constructor.
     */
    public DreamYaml(String filePath, boolean postProcessingEnabled, boolean debugEnabled, boolean autoLoadEnabled) throws IOException, DYReaderException, IllegalListException {
        this.filePath = filePath;
        this.addedModules = new ArrayList<>();
        this.postProcessingEnabled = postProcessingEnabled;
        this.debugEnabled = debugEnabled;
        this.autoLoadEnabled = autoLoadEnabled;
        if (autoLoadEnabled) load();
    }

    /**
     * Loads the file into memory by parsing
     * it into modules({@link DYModule}). Creates a new file if it didn't exist already.
     * You can return the list of modules with {@link #getAllLoaded()}.
     * Remember, that this updates your added modules values.
     */
    public DreamYaml load() throws IOException, DYReaderException, IllegalListException {
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
    public DreamYaml reset() throws Exception {
        if (file == null) this.load();
        new DYWriter().parse(this, true, true);
        this.load();
        return this;
    }

    /**
     * Convenience method for saving and loading afterwards.
     */
    public DreamYaml reload() throws Exception {
        if (file == null) this.load();
        this.save();
        this.load();
        return this;
    }

    /**
     * Parses and saves the current modules to the provided yaml file.
     * If the file misses modules, these get created using their default values.
     * See {@link DYModule#setDefValues(List)} and {@link UtilsDYModule#createUnifiedList(List, List)} for more details.
     * It's recommended to keep {@link #load()} and {@link #save()} timely close to each other, so the user
     * can't change the values in the meantime.
     * IMPORTANT: Stuff that isn't supported by DreamYaml (see features.yml) wont be parsed and thus removed from the file after you save it!
     *
     * @param overwrite Enable/Disable overwriting the yaml file. Disabled by default.
     *                  If true the yaml file gets overwritten with modules from the 'added modules list'.
     *                  That means that everything that wasn't added via {@link #add(String...)} will not exist in the file.
     */
    public DreamYaml save(boolean overwrite) throws Exception {
        if (file == null) this.load();
        new DYWriter().parse(this, overwrite, false);
        return this;
    }

    /**
     * For more details see: {@link #save(boolean)}
     */
    public DreamYaml save() throws Exception {
        this.save(false);
        return this;
    }

    /**
     * Creates a new {@link DYModule}, adds it to the modules list and returns it.
     * See {@link #add(DYModule)} for details.
     */
    public DYModule add(String... keys) throws Exception {
        if (keys == null) throw new Exception("Keys of this module cannot be null!");
        List<String> list = new ArrayList<>();
        list.addAll(Arrays.asList(keys));
        return add(list, null, null, null);
    }

    /**
     * Creates a new {@link DYModule}, adds it to the modules list and returns it.
     * See {@link #add(DYModule)} for details.
     */
    public DYModule add(List<String> keys, List<DYValue> defaultValues, List<DYValue> values, List<String> comments) throws Exception {
        return add(new DYModule(keys, defaultValues, values, comments));
    }

    /**
     * Adds the given module to the modules list, which will get parsed and written to file by {@link #save()}.
     * Doing changes to this modules values and saving them, will affect the original yaml file.
     * Note that null KEYS are not allowed!
     *
     * @param module module to add.
     * @return the added module.
     * @throws NotLoadedException    if the yaml file has not been loaded once yet
     * @throws DuplicateKeyException if another module with the same keys already exists
     */
    public DYModule add(DYModule module) throws Exception {
        if (module.getKeys() == null || module.getKeys().isEmpty())
            throw new Exception("Keys list of this module is null or empty!");
        if (file == null) throw new NotLoadedException(); // load() should've been called at least once before
        if (module.getKeys().contains(null)) throw new Exception("Null keys are not allowed!");
        UtilsDYModule utils = new UtilsDYModule();
        if (utils.getExisting(module, this.addedModules) != null) // Check for the same keys in the defaultModules list. Same keys are not allowed.
            throw new DuplicateKeyException(file.getName(), module.getKeys().toString());

        DYModule loaded = utils.getExisting(module, this.loadedModules);
        if (loaded != null) {
            module.setValues(loaded.getValues());
        }

        this.addedModules.add(module);
        return module;
    }

    /**
     * Convenience method for removing a module from the 'added modules list'.
     * If you call {@link #save()} after this, the module should also
     * be removed from the yaml file.
     *
     * @param module the module to remove.
     */
    public void remove(DYModule module) {
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
    public DYModule getLastLoadedModule() {
        return loadedModules.get(loadedModules.size() - 1);
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
    public DYModule getLastAddedModule() {
        return addedModules.get(addedModules.size() - 1);
    }

    /**
     * Prints out all lists.
     */
    public void printAll() {
        printLoaded();
        printAdded();
        printUnified();
        System.out.println(" ");
    }

    /**
     * Prints out all modules in the loaded list.
     * For more info see {@link UtilsDreamYaml#printLoaded(PrintStream)}}.
     */
    public void printLoaded() {
        utils.printLoaded(System.out);
    }

    /**
     * Prints out all modules in the added list.
     * For more info see {@link UtilsDreamYaml#printAdded(PrintStream)}}.
     */
    public void printAdded() {
        utils.printAdded(System.out);
    }

    /**
     * Prints out all modules in the unified list.
     * For more info see {@link UtilsDYModule#createUnifiedList(List, List)} and {@link UtilsDreamYaml#printUnified(PrintStream)}}.
     */
    public void printUnified() {
        utils.printUnified(System.out);
    }


    public String getFilePath() {
        return filePath;
    }

    public File getFile() {
        return file;
    }

    public UtilsDreamYaml getUtils() {
        return utils;
    }

    /**
     * Returns the yml files name without its extension.
     */
    public String getFileNameWithoutExt() throws NotLoadedException {
        if (file == null) throw new NotLoadedException();
        return file.getName().replaceFirst("[.][^.]+$", ""); // Removes the file extension
    }

    public boolean isDebugEnabled() {
        return debugEnabled;
    }

    public void setDebugEnabled(boolean debugEnabled) {
        this.debugEnabled = debugEnabled;
    }

    /**
     * Returns the module with same keys from the 'added modules list'.
     * Details: {@link #getAllAdded()}
     *
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
     *
     * @return {@link DYModule} or null if no module found with same keys
     */
    public DYModule getAddedModuleByKeys(List<String> keys) {
        return new UtilsDYModule().getExisting(keys, addedModules);
    }

    /**
     * Returns the module with same keys from the 'loaded modules list'.
     * Details: {@link #getAllLoaded()}
     *
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
     *
     * @return {@link DYModule} or null if no module found with same keys
     */
    public DYModule getLoadedModuleByKeys(List<String> keys) {
        return new UtilsDYModule().getExisting(keys, loadedModules);
    }

    /**
     * Responsible for removing "" and '' from your values.
     */
    public boolean isPostProcessingEnabled() {
        return postProcessingEnabled;
    }

    /**
     * Responsible for removing "" and '' from your values.
     */
    public void setPostProcessingEnabled(boolean postProcessingEnabled) {
        this.postProcessingEnabled = postProcessingEnabled;
    }

    /**
     * Calls {@link #load()} inside the constructor.
     */
    public boolean isAutoLoadEnabled() {
        return autoLoadEnabled;
    }

    /**
     * Calls {@link #load()} inside the constructor.
     */
    public void setAutoLoadEnabled(boolean autoLoadEnabled) {
        this.autoLoadEnabled = autoLoadEnabled;
    }
}

