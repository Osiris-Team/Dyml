/*
 *  Copyright Osiris Team
 *  All rights reserved.
 *
 *  This software is licensed work.
 *  Please consult the file "LICENSE" for details.
 */

package com.osiris.dyml;

import com.osiris.dyml.exceptions.*;
import com.osiris.dyml.utils.UtilsDYModule;
import com.osiris.dyml.utils.UtilsDreamYaml;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * The in-memory representation of the full yaml file
 * that contains all of the default and loaded modules.
 */
public class DreamYaml {
    private final UtilsDreamYaml utilsDreamYaml = new UtilsDreamYaml(this);
    private final UtilsDYModule utilsDYModule = new UtilsDYModule();
    private final String filePath;
    private final List<DYModule> addedModules;
    private File file;
    private List<DYModule> loadedModules;

    private boolean isDebugEnabled;
    private boolean isAutoLoadEnabled;

    private boolean isAllPostProcessingEnabled;
    private boolean isTrimLoadedValuesEnabled = true;
    private boolean isRemoveQuotesFromLoadedValuesEnabled = true;
    private boolean isRemoveLoadedNullValuesEnabled = true;


    /**
     * Initialises the {@link DreamYaml} object with useful features enabled. <br>
     * See {@link #DreamYaml(String, boolean, boolean, boolean)} for details.
     */
    public DreamYaml(File file) throws IOException, DYReaderException, IllegalListException, DuplicateKeyException {
        this(file.getAbsolutePath());
    }

    /**
     * Initialises the {@link DreamYaml} object with useful features enabled. <br>
     * See {@link #DreamYaml(String, boolean, boolean, boolean)} for details.
     */
    public DreamYaml(File file, boolean isDebugEnabled) throws IOException, DYReaderException, IllegalListException, DuplicateKeyException {
        this(file.getAbsolutePath(), true, isDebugEnabled, true);
    }

    /**
     * Initialises the {@link DreamYaml} object with useful features enabled. <br>
     * See {@link #DreamYaml(String, boolean, boolean, boolean)} for details.
     */
    public DreamYaml(String filePath) throws IOException, DYReaderException, IllegalListException, DuplicateKeyException {
        this(filePath, true, false, true);
    }

    /**
     * Initialises the {@link DreamYaml} object with useful features enabled. <br>
     * See {@link #DreamYaml(String, boolean, boolean, boolean)} for details.
     */
    public DreamYaml(String filePath, boolean isDebugEnabled) throws IOException, DYReaderException, IllegalListException, DuplicateKeyException {
        this(filePath, true, isDebugEnabled, true);
    }

    /**
     * Initialises the {@link DreamYaml} object.
     *
     * @param filePath                   Your yaml files path.
     * @param isAllPostProcessingEnabled Enabled by default. <br>
     *                                   You can also enable/disable specific post-processing options individually: <br>
     *                                   See {@link #isAllPostProcessingEnabled()} for details.
     * @param isDebugEnabled             Disabled by default. Shows debugging stuff.
     * @param isAutoLoadEnabled          Enabled by default. Calls {@link #load()} inside of the constructor.
     */
    public DreamYaml(String filePath, boolean isAllPostProcessingEnabled, boolean isDebugEnabled, boolean isAutoLoadEnabled) throws IOException, DYReaderException, IllegalListException, DuplicateKeyException {
        this.filePath = filePath;
        this.addedModules = new ArrayList<>();
        this.isAllPostProcessingEnabled = isAllPostProcessingEnabled;
        this.isDebugEnabled = isDebugEnabled;
        this.isAutoLoadEnabled = isAutoLoadEnabled;
        if (isAutoLoadEnabled) load();
    }

    /**
     * Loads the file into memory by parsing
     * it into modules({@link DYModule}). Creates a new file if it didn't exist already.
     * You can return the list of modules with {@link #getAllLoaded()}.
     * Remember, that this updates your added modules values.
     */
    public DreamYaml load() throws IOException, DYReaderException, IllegalListException, DuplicateKeyException {
        this.loadedModules = new ArrayList<>();
        file = new File(filePath);
        if (!file.exists()) file.createNewFile();
        new DYReader().parse(this);
        return this;
    }

    /**
     * Caution! This method will completely reset/remove all information from your yaml file, but not delete it.
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
     * Convenience method for saving and loading afterwards. <br>
     * See {@link #save(boolean)} and {@link #load()} for details.
     */
    public DreamYaml saveAndReload() throws Exception {
        if (file == null) this.load();
        this.save();
        this.load();
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
     * <p style="color:red;">IMPORTANT: Stuff that isn't supported by DreamYaml (see features.yml) wont be parsed and thus removed from the file after you save it!</p>
     * Parses and saves the current modules to the provided yaml file. <br>
     * Note that this method won't reload the file after. Use {@link #saveAndReload()} instead. <br>
     * It's recommended to keep {@link #load()} and {@link #save()} timely close to each other, so the user  <br>
     * can't change the values in the meantime. <br>
     * If the yaml file is missing some 'added modules', these get created using their values/default values.<br>
     * More info on this topic: <br>
     * {@link DYModule#isWriteDefaultWhenValuesListIsEmptyEnabled()} <br>
     * {@link DYModule#setDefValues(List)} <br>
     * {@link UtilsDYModule#createUnifiedList(List, List)} <br>
     *
     * @param overwrite false by default.
     *                  If true, the yaml file gets overwritten with only modules from the 'added modules list'.
     *                  That means that everything that wasn't added via {@link #add(String...)} (loaded modules) will not exist in the file.
     */
    public DreamYaml save(boolean overwrite) throws Exception {
        if (file == null) this.load();
        new DYWriter().parse(this, overwrite, false);
        return this;
    }


    /**
     * Creates a new {@link DYModule}, with the provided keys, adds it to the modules list and returns it. <br>
     * See {@link #add(DYModule)} for details.
     */
    public DYModule add(String... keys) throws NotLoadedException, IllegalKeyException, DuplicateKeyException {
        Objects.requireNonNull(keys);
        List<String> list = new ArrayList<>();
        list.addAll(Arrays.asList(keys));
        return add(list, null, null, null);
    }

    /**
     * Creates a new {@link DYModule}, with the provided keys, adds it to the modules list and returns it. <br>
     * See {@link #add(DYModule)} for details.
     */
    public DYModule add(List<String> keys, List<DYValue> defaultValues, List<DYValue> values, List<String> comments) throws NotLoadedException, IllegalKeyException, DuplicateKeyException {
        return add(new DYModule(keys, defaultValues, values, comments));
    }

    /**
     * Adds the provided module to the modules list, which will get parsed and written to file by {@link #save()}.
     * Doing changes to this modules values and saving them, will affect the original yaml file.
     * Note that null KEYS are not allowed!
     *
     * @param module module to add.
     * @return the added module.
     * @throws NotLoadedException    if the yaml file has not been loaded once yet
     * @throws DuplicateKeyException if another module with the same keys already exists
     */
    public DYModule add(DYModule module) throws IllegalKeyException, NotLoadedException, DuplicateKeyException {
        Objects.requireNonNull(module);
        Objects.requireNonNull(module.getKeys());
        if (module.getKeys().isEmpty()) throw new IllegalKeyException("Keys list of this module cannot be empty!");
        if (file == null) throw new NotLoadedException(); // load() should've been called at least once before
        if (module.getKeys().contains(null))
            throw new IllegalKeyException("The provided keys list contains null key(s)! This is not allowed!");

        if (utilsDYModule.getExisting(module, this.addedModules) != null) // Check for the same keys in the defaultModules list. Same keys are not allowed.
            throw new DuplicateKeyException(file.getName(), module.getKeys().toString());

        DYModule loaded = utilsDYModule.getExisting(module, this.loadedModules);
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
     * Returns a fresh unified list with all loaded and added modules merged together. <br>
     * Note that this is not the original list, but a copy and thus any changes to it, won't have affect and changes to the original
     * won't be reflected in this copy. <br>
     * See {@link UtilsDYModule#createUnifiedList(List, List)} for details.
     */
    public List<DYModule> getAll() {
        return new UtilsDYModule().createUnifiedList(this.addedModules, this.loadedModules);
    }

    /**
     * <p style="color:red;">Do not modify this list directly, unless you know what you are doing!</p>
     * Returns a list containing all loaded modules. <br>
     * This is the original list. Note that its values/modules get updated every time {@link #load()} is called.
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
     * <p style="color:red;">Do not modify this list directly, unless you know what you are doing!</p>
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
        utilsDreamYaml.printLoaded(System.out);
    }

    /**
     * Prints out all modules in the added list.
     * For more info see {@link UtilsDreamYaml#printAdded(PrintStream)}}.
     */
    public void printAdded() {
        utilsDreamYaml.printAdded(System.out);
    }

    /**
     * Prints out all modules in the unified list.
     * For more info see {@link UtilsDYModule#createUnifiedList(List, List)} and {@link UtilsDreamYaml#printUnified(PrintStream)}}.
     */
    public void printUnified() {
        utilsDreamYaml.printUnified(System.out);
    }


    public String getFilePath() {
        return filePath;
    }

    public File getFile() {
        return file;
    }

    public UtilsDreamYaml getUtilsDreamYaml() {
        return utilsDreamYaml;
    }

    /**
     * Returns the yml files name without its extension.
     */
    public String getFileNameWithoutExt() throws NotLoadedException {
        if (file == null) throw new NotLoadedException();
        return file.getName().replaceFirst("[.][^.]+$", ""); // Removes the file extension
    }

    public boolean isDebugEnabled() {
        return isDebugEnabled;
    }

    public void setDebugEnabled(boolean debugEnabled) {
        this.isDebugEnabled = debugEnabled;
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
     * Enabled by default.<br>
     * Convenience method for enabling/disabling all post-processing options. <br>
     * Post-Processing happens inside {@link #load()}. If you want to change them, <br>
     * its recommended to disable autoLoad ({@link #isAutoLoadEnabled}) in the constructor,
     * so you don't have to load the file twice.
     * All available options are: <br>
     * {@link #setTrimLoadedValuesEnabled(boolean)} <br>
     * {@link #setRemoveQuotesFromLoadedValuesEnabled(boolean)} <br>
     * {@link #setRemoveLoadedNullValuesEnabled(boolean)} <br>
     */
    public boolean isAllPostProcessingEnabled() {
        return isAllPostProcessingEnabled;
    }

    /**
     * Enabled by default.<br>
     * Convenience method for enabling/disabling all post-processing options. <br>
     * Post-Processing happens inside {@link #load()}. If you want to change them, <br>
     * its recommended to disable autoLoad ({@link #isAutoLoadEnabled}) in the constructor,
     * so you don't have to load the file twice.
     * All available options are: <br>
     * {@link #setTrimLoadedValuesEnabled(boolean)} <br>
     * {@link #setRemoveQuotesFromLoadedValuesEnabled(boolean)} <br>
     * {@link #setRemoveLoadedNullValuesEnabled(boolean)} <br>
     */
    public void setAllPostProcessingEnabled(boolean allPostProcessingEnabled) {
        this.isAllPostProcessingEnabled = allPostProcessingEnabled;
    }

    /**
     * Calls {@link #load()} inside the constructor.
     */
    public boolean isAutoLoadEnabled() {
        return isAutoLoadEnabled;
    }

    /**
     * Calls {@link #load()} inside the constructor.
     */
    public void setAutoLoadEnabled(boolean autoLoadEnabled) {
        this.isAutoLoadEnabled = autoLoadEnabled;
    }

    /**
     * Enabled by default. Part of post-processing.<br>
     * Trims the loaded {@link DYValue}. Example: <br>
     * <pre>
     * String before: '  hello there  '
     * String after: 'hello there'
     * Result: removed 4 spaces.
     * </pre>
     */
    public boolean isTrimLoadedValuesEnabled() {
        return isTrimLoadedValuesEnabled;
    }

    /**
     * Enabled by default. Part of post-processing. <br>
     * Trims the loaded {@link DYValue}. Example: <br>
     * <pre>
     * String before: '  hello there  '
     * String after: 'hello there'
     * Result: removed 4 spaces.
     * </pre>
     */
    public void setTrimLoadedValuesEnabled(boolean trimLoadedValuesEnabled) {
        isTrimLoadedValuesEnabled = trimLoadedValuesEnabled;
    }

    /**
     * Enabled by default. Part of post-processing. <br>
     * Removes quotation marks ("" or '') from the loaded {@link DYValue}. Example: <br>
     * <pre>
     * String before: "hello there"
     * String after: hello there
     * Result: removed 2 quotation-marks.
     * </pre>
     */
    public boolean isRemoveQuotesFromLoadedValuesEnabled() {
        return isRemoveQuotesFromLoadedValuesEnabled;
    }

    /**
     * Enabled by default. Part of post-processing. <br>
     * Removes quotation marks ("" or '') from the loaded {@link DYValue}. Example: <br>
     * <pre>
     * String before: "hello there"
     * String after: hello there
     * Result: removed 2 quotation-marks.
     * </pre>
     */
    public void setRemoveQuotesFromLoadedValuesEnabled(boolean removeQuotesFromLoadedValuesEnabled) {
        isRemoveQuotesFromLoadedValuesEnabled = removeQuotesFromLoadedValuesEnabled;
    }

    /**
     * Enabled by default. Part of post-processing. <br>
     * Removes all null loaded {@link DYValue}s.<br>
     */
    public boolean isRemoveLoadedNullValuesEnabled() {
        return isRemoveLoadedNullValuesEnabled;
    }

    /**
     * Enabled by default. Part of post-processing. <br>
     * Removes all null loaded {@link DYValue}s. <br>
     */
    public void setRemoveLoadedNullValuesEnabled(boolean removeLoadedNullValuesEnabled) {
        isRemoveLoadedNullValuesEnabled = removeLoadedNullValuesEnabled;
    }

}

