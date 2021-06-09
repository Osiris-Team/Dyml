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
import com.osiris.dyml.utils.UtilsFile;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * The in-memory representation of the full yaml file
 * that contains all of the default and loaded modules.
 */
public class DreamYaml {
    private final UtilsDreamYaml utilsDreamYaml = new UtilsDreamYaml(this);
    private final UtilsDYModule utilsDYModule = new UtilsDYModule();
    private final UtilsFile utilsFile = new UtilsFile();
    private final String filePath;
    /**
     * A final list, that contains {@link DYModule}s that. <br>
     * In contrary to the {@link #loadedModules} list, this list doesn't get cleared <br>
     * and its {@link DYModule}s stay the same, no matter how often you call {@link #load()}. <br>
     */
    private final List<DYModule> inEditModules = new ArrayList<>();
    /**
     * A final list, that contains loaded {@link DYModule}s. <br>
     * It gets cleared and refilled with new {@link DYModule}s in {@link #load()}. <br>
     */
    private final List<DYModule> loadedModules = new ArrayList<>();
    private File file;
    private boolean isDebugEnabled;
    private boolean isAutoLoadEnabled;

    // Post-Processing:
    private boolean isPostProcessingEnabled;
    private boolean isTrimLoadedValuesEnabled = true;
    private boolean isRemoveQuotesFromLoadedValuesEnabled = true;
    private boolean isRemoveLoadedNullValuesEnabled = true;
    private boolean isTrimCommentsEnabled = true;

    // Modules:
    private boolean isReturnDefaultWhenValueIsNullEnabled = true;
    private boolean isWriteDefaultValuesWhenEmptyEnabled = true;
    private boolean isWriteDefaultCommentsWhenEmptyEnabled = true;

    // Logging:
    private DYDebugLogger debugLogger = new DYDebugLogger(System.out);

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
     * @param filePath                Your yaml files path.
     * @param isPostProcessingEnabled Enabled by default. <br>
     *                                You can also enable/disable specific post-processing options individually: <br>
     *                                See {@link #isPostProcessingEnabled()} for details.
     * @param isDebugEnabled          Disabled by default. Shows debugging stuff.
     * @param isAutoLoadEnabled       Enabled by default. Calls {@link #load()} inside of the constructor.
     */
    public DreamYaml(String filePath, boolean isPostProcessingEnabled, boolean isDebugEnabled, boolean isAutoLoadEnabled) throws IOException, DYReaderException, IllegalListException, DuplicateKeyException {
        this.filePath = filePath;
        this.isPostProcessingEnabled = isPostProcessingEnabled;
        this.isDebugEnabled = isDebugEnabled;
        this.isAutoLoadEnabled = isAutoLoadEnabled;
        if (isAutoLoadEnabled) load();
    }

    /**
     * Loads the file into memory by parsing it into modules({@link DYModule}). <br>
     * Creates a new file if it didn't exist already. <br>
     * You can return the list of modules with {@link #getAllLoaded()}. <br>
     * Remember, that this updates your added modules values. <br>
     * Also note that it post-processes the 'loaded modules'. <br>
     * You can also enable/disable specific post-processing options individually: <br>
     * See {@link #isPostProcessingEnabled()} for details.
     */
    public DreamYaml load() throws IOException, DYReaderException, IllegalListException, DuplicateKeyException {
        if (this.isDebugEnabled) System.out.println("Executing load()");
        file = new File(filePath);
        if (!file.exists()) file.createNewFile();
        new DYReader().parse(this);
        return this;
    }

    /**
     * Caution! This method will completely reset/remove all information from your yaml file, but not delete it.
     * To delete, use {@link File#delete()} instead. You can get the file via {@link #getFile()}.
     * Also the {@link #getAllLoaded()} list is empty after this operation.
     * The {@link #getAllInEdit()} list is not affected.
     */
    public DreamYaml reset() throws IOException, DuplicateKeyException, DYReaderException, IllegalListException, DYWriterException {
        if (this.isDebugEnabled) debugLogger.log(this, "Executing reset()");
        if (file == null) this.load();
        new DYWriter().parse(this, true, true);
        this.load();
        return this;
    }

    /**
     * Convenience method for saving and loading afterwards. <br>
     * See {@link #save(boolean)} and {@link #load()} for details.
     */
    public DreamYaml saveAndLoad() throws IOException, DuplicateKeyException, DYReaderException, IllegalListException, DYWriterException {
        if (file == null) this.load();
        this.save();
        this.load();
        return this;
    }

    /**
     * For more details see: {@link #save(boolean)}
     */
    public DreamYaml save() throws DYWriterException, IOException, DuplicateKeyException, DYReaderException, IllegalListException {
        this.save(false);
        return this;
    }

    /**
     * <p style="color:red;">IMPORTANT: Stuff that isn't supported by DreamYaml (see features.yml) wont be parsed and thus removed from the file after you save it!</p>
     * Parses and saves the current modules to the provided yaml file. <br>
     * Note that this method won't reload the file after. Use {@link #saveAndLoad()} instead. <br>
     * It's recommended to keep {@link #load()} and {@link #save()} timely close to each other, so the user  <br>
     * can't change the values in the meantime. <br>
     * If the yaml file is missing some 'added modules', these get created using their values/default values.<br>
     * More info on this topic: <br>
     * {@link #isWriteDefaultValuesWhenEmptyEnabled()} <br>
     * {@link #createUnifiedList(List, List)} <br>
     * {@link DYModule#setDefValues(List)} <br>
     *
     * @param overwrite false by default.
     *                  If true, the yaml file gets overwritten with only modules from the 'added modules list'.
     *                  That means that everything that wasn't added via {@link #add(String...)} (loaded modules) will not exist in the file.
     */
    public DreamYaml save(boolean overwrite) throws IOException, DuplicateKeyException, DYReaderException, IllegalListException, DYWriterException {
        if (this.isDebugEnabled) debugLogger.log(this, "Executing save()");
        if (file == null) this.load();
        new DYWriter().parse(this, overwrite, false);
        return this;
    }

    /**
     * Returns the {@link DYModule} with matching keys or null. <br>
     * Details: <br>
     * Searches the {@link #inEditModules} list, and the {@link #loadedModules} list for
     * the matching {@link DYModule}
     * and returns it. Null if no matching {@link DYModule} for the provided keys could be found. <br>
     * If the {@link DYModule} was found in the {@link #loadedModules} list, it gets removed from there and
     * added to the {@link #inEditModules} list. <br>
     */
    public DYModule get(String... keys) {
        Objects.requireNonNull(keys);
        if (this.isDebugEnabled) debugLogger.log(this, "Executing get(" + keys.toString() + ")");
        DYModule module = utilsDYModule.getExisting(Arrays.asList(keys), inEditModules);
        if (module == null) {
            module = utilsDYModule.getExisting(Arrays.asList(keys), loadedModules);
            if (module != null) {
                inEditModules.add(module);
            }
        }
        return module;
    }

    /**
     * Returns the existing {@link DYModule} with matching keys, or adds a new one. <br>
     * Details: <br>
     * Searches for duplicate in the {@link #inEditModules}, <br>
     * and the {@link #loadedModules} list and returns it if could find one. <br>
     * Otherwise, it creates a new {@link DYModule} from the <br>
     * provided keys, adds it to the {@link #inEditModules} list and returns it. <br>
     */
    public DYModule put(String... keys) throws NotLoadedException, IllegalKeyException {
        Objects.requireNonNull(keys);
        if (this.isDebugEnabled) debugLogger.log(this, "Executing add(" + keys.toString() + ")");
        DYModule module = utilsDYModule.getExisting(Arrays.asList(keys), inEditModules);
        if (module == null) {
            module = utilsDYModule.getExisting(Arrays.asList(keys), loadedModules);
            if (module != null) {
                inEditModules.add(module);
            } else
                try {
                    module = add(keys);
                } catch (NotLoadedException | IllegalKeyException e) {
                    throw e;
                } catch (DuplicateKeyException ignored) {
                    ignored.printStackTrace();
                }
        }
        return module;
    }


    /**
     * Creates a new {@link DYModule}, with the provided keys, adds it to the modules list and returns it. <br>
     * See {@link #add(DYModule)} for details.
     */
    public DYModule add(String... keys) throws NotLoadedException, IllegalKeyException, DuplicateKeyException {
        Objects.requireNonNull(keys);
        List<String> list = new ArrayList<>(Arrays.asList(keys));
        return add(list, null, null, null);
    }

    /**
     * Creates a new {@link DYModule}, with the provided keys, adds it and returns it. <br>
     * See {@link #add(DYModule)} for details.
     */
    public DYModule add(List<String> keys, List<DYValue> defaultValues, List<DYValue> values, List<String> comments) throws NotLoadedException, IllegalKeyException, DuplicateKeyException {
        return add(new DYModule(this, keys, defaultValues, values, comments));
    }

    /**
     * Adds the provided {@link DYModule} or throws exception if it already exists. <br>
     * Note that null or duplicate KEYS are not allowed. <br>
     * Details: <br>
     * Searches for duplicates in the {@link #inEditModules}, and the {@link #loadedModules} list and throws
     * {@link DuplicateKeyException} if it could find one. Otherwise, it creates a new {@link DYModule} from the
     * provided keys, adds it to the {@link #inEditModules} list and returns it.
     *
     * @param module module to add.
     * @return the added module.
     * @throws NotLoadedException    if the yaml file has not been loaded once yet
     * @throws DuplicateKeyException if another module with the same keys already exists
     */
    public DYModule add(DYModule module) throws IllegalKeyException, NotLoadedException, DuplicateKeyException {
        Objects.requireNonNull(module);
        Objects.requireNonNull(module.getKeys());
        if (this.isDebugEnabled) debugLogger.log(this, "Executing add(" + module.getKeys().toString() + ")");
        if (module.getKeys().isEmpty()) throw new IllegalKeyException("Keys list of this module cannot be empty!");
        if (file == null) throw new NotLoadedException(); // load() should've been called at least once before
        if (module.getKeys().contains(null))
            throw new IllegalKeyException("The provided keys list contains null key(s)! This is not allowed!");

        if (utilsDYModule.getExisting(module, this.inEditModules) != null)
            throw new DuplicateKeyException(file.getName(), module.getKeys().toString());

        if (utilsDYModule.getExisting(module, this.loadedModules) != null)
            throw new DuplicateKeyException(file.getName(), module.getKeys().toString());

        this.inEditModules.add(module);
        return module;
    }

    /**
     * Replaces {@link DYModule}, with the provided {@link DYModule}. <br>
     * Details: <br>
     * Searches the {@link #inEditModules} list, and the {@link #loadedModules} list for the {@link DYModule} to replace. <br>
     * Replaces it and returns the replacement, or null if {@link DYModule} to replace couldn't be found. <br>
     * If the {@link DYModule} to replace was found in the {@link #loadedModules} list, it gets removed from there and <br>
     * the replacement gets added to the {@link #inEditModules} list. <br>
     */
    public DYModule replace(DYModule moduleToReplace, DYModule newModule) {
        if (this.isDebugEnabled) debugLogger.log(this, "Executing replace()");
        Objects.requireNonNull(moduleToReplace);
        Objects.requireNonNull(newModule);
        DYModule module = utilsDYModule.getExisting(moduleToReplace, inEditModules);
        if (module == null) {
            module = utilsDYModule.getExisting(moduleToReplace, loadedModules);
            if (module != null) {
                inEditModules.add(newModule);
            }
        } else {
            int i = inEditModules.indexOf(moduleToReplace);
            inEditModules.remove(moduleToReplace);
            inEditModules.add(i, newModule);
        }
        return module;
    }

    /**
     * Removes the module from the yaml file once {@link #save()} was called. <br>
     */
    public DreamYaml remove(String... keys) throws NotLoadedException, IllegalKeyException, DuplicateKeyException {
        Objects.requireNonNull(keys);
        remove(new DYModule(this, keys));
        return this;
    }

    /**
     * Removes the module from the yaml file once {@link #save()} was called. <br>
     */
    public DreamYaml remove(DYModule module) {
        if (this.isDebugEnabled) debugLogger.log(this, "Executing remove()");
        DYModule addedM = utilsDYModule.getExisting(module, inEditModules);
        if (addedM != null)
            this.inEditModules.remove(addedM);
        DYModule loadedM = utilsDYModule.getExisting(module, loadedModules);
        if (loadedM != null)
            this.loadedModules.remove(loadedM);
        return this;
    }

    /**
     * This method returns a new unified list containing the loaded and added modules merged together. <br>
     * The loaded modules list is used as 'base' and is overwritten/extended by the added modules list. <br>
     * This ensures, that the structure(hierarchies) of the loaded file stay the same <br>
     * and that new modules are inserted in the correct position. <br>
     * Logic: <br>
     * 1. If the loaded modules list is empty, nothing needs to be done! Return added modules. <br>
     * 2. Else go through the loaded modules and compare each module with the added modules list.
     * If there is an added module with the same keys, add it to the unified list instead of the loaded module. <br>
     * 3. If there are NEW modules in the added modules list, insert them into the right places of unified list. <br>
     *
     * @return a fresh unified list containing loaded modules extended by added modules.
     */
    public synchronized List<DYModule> createUnifiedList(List<DYModule> inEditModules, List<DYModule> loadedModules) {
        if (loadedModules.isEmpty()) return inEditModules;

        if (isDebugEnabled) {
            debugLogger.log(this, "### CREATE UNIFIED LIST ###");
            debugLogger.log(this, "This process creates a single list out of the 'inEditModules' and 'loadedModules' lists.");
            debugLogger.log(this, "Printing contents of both lists:");
            for (DYModule m :
                    inEditModules) {
                debugLogger.log(this, "inEditModules: KEY" + m.getKeys());
            }

            for (DYModule m :
                    loadedModules) {
                debugLogger.log(this, "loadedModules: KEY" + m.getKeys());
            }
        }

        List<DYModule> copyInEditModules = new CopyOnWriteArrayList<>(inEditModules);
        List<DYModule> unifiedList = new ArrayList<>();
        // Go through the loadedModules list and take its structure.
        if (isDebugEnabled) {
            debugLogger.log(this, "Create the unified list: ");
            debugLogger.log(this, "We go thorough the loadedModules list, to keep its structure and");
            debugLogger.log(this, "add its modules to the unified list. If there is a inEditModule that has the");
            debugLogger.log(this, "same keys as the loadedModule, it gets added instead.");
        }
        for (DYModule loadedModule :
                loadedModules) {
            // Check if there is the same 'added module' available
            DYModule existing = utilsDYModule.getExisting(loadedModule, copyInEditModules);
            if (existing != null) {
                unifiedList.add(existing);
                // Also remove it from its own list, so at the end there are only 'new' modules in that list
                copyInEditModules.remove(existing);
                if (isDebugEnabled) debugLogger.log(this, "+ inEditModule " + existing.getKeys().toString() + " to unified.");
            } else {
                unifiedList.add(loadedModule);
                if (isDebugEnabled) debugLogger.log(this, "+ loadedModule +" + loadedModule.getKeys().toString() + "+ to unified.");
            }
        }

        if (isDebugEnabled) {
            debugLogger.log(this, "Now go through the copyInEditModules(" + copyInEditModules.size() + ") list, which");
            debugLogger.log(this, "should now only contain new modules, that didn't exist in the loadedModules list.");
            debugLogger.log(this, "Add those modules, to");
        }
        //System.out.println("");
        //System.out.println("Go through the copyInEditModules("+copyInEditModules.size()+") list and add NEW ones to unified: ");


        // The copyInEditModules, now only contains completely new modules.
        // Go through that list, add G0 modules to the end of the unifiedModules list and
        // other generations to their respective parents, as first module.
        for (DYModule inEditModule :
                copyInEditModules) {

            if (inEditModule.getKeys().size() > 1) {
                DYModule parent = new UtilsDYModule().getExisting(inEditModule.getKeys().subList(0, inEditModule.getKeys().size() - 1), loadedModules);
                if (parent != null) {
                    int parentIndex = 0;
                    for (DYModule uM :
                            unifiedList) {
                        if (uM.getKeys().equals(parent.getKeys())) { // Do this to find the parents position in the unified list
                            // Add the inEditModule, to the list.
                            // Example:
                            // g0:
                            //   g1-1:
                            //   g1-2: <--- Remember to count the child modules before too!
                            if (!parent.getChildModules().isEmpty())
                                parentIndex = parentIndex + parent.getChildModules().size() - 1;
                            unifiedList.add(parentIndex + 1, inEditModule);
                            parent.getChildModules().add(inEditModule);
                            break;
                        }
                        parentIndex++;
                    }
                } else {
                    unifiedList.add(inEditModule); // Can be a completely new >G0 module
                }
            } else {
                unifiedList.add(inEditModule); // G0 modules get added to the end of the file
            }
        }
        return unifiedList;
    }


    /**
     * Returns a fresh unified, ordered list with {@link #loadedModules} and {@link #inEditModules} merged together. <br>
     * Note that this is not the original list, but a copy and thus any changes to it, won't have affect and changes to the original
     * won't be reflected in this copy. <br>
     * This list is the one, that gets written to the yaml file. <br>
     * See {@link #createUnifiedList(List, List)} for details.
     */
    public List<DYModule> getAll() {
        return createUnifiedList(this.inEditModules, this.loadedModules);
    }

    /**
     * <p style="color:red;">Do not modify this list directly, unless you know what you are doing!</p>
     * Returns a list containing all loaded modules. <br>
     * This is the original list. Note that its modules get updated every time {@link #load()} is called.
     * Its modules, do not contain default values.
     */
    public List<DYModule> getAllLoaded() {
        return loadedModules;
    }

    /**
     * Convenience method for returning the last module from the {@link #inEditModules} list.
     */
    public DYModule getLastLoadedModule() {
        return loadedModules.get(loadedModules.size() - 1);
    }

    /**
     * <p style="color:red;">Do not modify this list directly, unless you know what you are doing!</p>
     * Returns a list containing all {@link DYModule}s that are being edited.
     * Modules should only be added by {@link #put(String...)}/{@link #add(String...)} and never by this lists own add() method.
     * This list is not affected by {@link #load()}, unlike the
     * 'loaded modules' list, which can be returned by {@link #getAllLoaded()}.
     */
    public List<DYModule> getAllInEdit() {
        return inEditModules;
    }

    /**
     * Convenience method for returning the last module from the {@link #inEditModules} list.
     */
    public DYModule getLastInEditModule() {
        return inEditModules.get(inEditModules.size() - 1);
    }

    /**
     * Prints out all lists.
     */
    public DreamYaml printAll() {
        printLoaded();
        printInEdit();
        printUnified();
        System.out.println();
        return this;
    }

    /**
     * Prints out all modules in the loaded list.
     * For more info see {@link UtilsDreamYaml#printLoaded(PrintStream)}}.
     */
    public DreamYaml printLoaded() {
        utilsDreamYaml.printLoaded(System.out);
        return this;
    }

    /**
     * Prints out all modules in the added list.
     * For more info see {@link UtilsDreamYaml#printInEdit(PrintStream)}}.
     */
    public DreamYaml printInEdit() {
        utilsDreamYaml.printInEdit(System.out);
        return this;
    }

    /**
     * Prints out all modules in the unified list.
     * For more info see {@link #createUnifiedList(List, List)} and {@link UtilsDreamYaml#printUnified(PrintStream)}}.
     */
    public DreamYaml printUnified() {
        utilsDreamYaml.printUnified(System.out);
        return this;
    }

    /**
     * Prints out the files content.
     */
    public DreamYaml printFile() {
        Objects.requireNonNull(file);
        utilsFile.printFile(file);
        return this;
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

    public DreamYaml setDebugEnabled(boolean debugEnabled) {
        this.isDebugEnabled = debugEnabled;
        return this;
    }

    /**
     * Enabled by default. Convenience method for toggling post-processing.<br>
     * When disabled none of the post-processing options gets run, no matter if they are enabled/disabled. <br>
     * Post-Processing happens inside {@link #load()}. If you want to change them, <br>
     * its recommended to disable autoLoad ({@link #isAutoLoadEnabled}) in the constructor,
     * so you don't have to load the file twice.
     * All available options are: <br>
     * {@link #setTrimLoadedValuesEnabled(boolean)} <br>
     * {@link #setRemoveQuotesFromLoadedValuesEnabled(boolean)} <br>
     * {@link #setRemoveLoadedNullValuesEnabled(boolean)} <br>
     */
    public boolean isPostProcessingEnabled() {
        return isPostProcessingEnabled;
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
    public DreamYaml setPostProcessingEnabled(boolean postProcessingEnabled) {
        this.isPostProcessingEnabled = postProcessingEnabled;
        return this;
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
    public DreamYaml setAutoLoadEnabled(boolean autoLoadEnabled) {
        this.isAutoLoadEnabled = autoLoadEnabled;
        return this;
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
    public DreamYaml setTrimLoadedValuesEnabled(boolean trimLoadedValuesEnabled) {
        isTrimLoadedValuesEnabled = trimLoadedValuesEnabled;
        return this;
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
    public DreamYaml setRemoveQuotesFromLoadedValuesEnabled(boolean removeQuotesFromLoadedValuesEnabled) {
        isRemoveQuotesFromLoadedValuesEnabled = removeQuotesFromLoadedValuesEnabled;
        return this;
    }

    /**
     * Enabled by default. Part of post-processing. <br>
     * If {@link DYValue#asString()} returns null, the whole {@link DYValue} gets removed from the modules values list. <br>
     */
    public boolean isRemoveLoadedNullValuesEnabled() {
        return isRemoveLoadedNullValuesEnabled;
    }

    /**
     * Enabled by default. Part of post-processing. <br>
     * If {@link DYValue#asString()} returns null, the whole {@link DYValue} gets removed from the modules values list. <br>
     */
    public DreamYaml setRemoveLoadedNullValuesEnabled(boolean removeLoadedNullValuesEnabled) {
        isRemoveLoadedNullValuesEnabled = removeLoadedNullValuesEnabled;
        return this;
    }

    public UtilsFile getUtilsFile() {
        return utilsFile;
    }

    /**
     * Enabled by default. Part of post-processing. <br>
     * Trims the loaded key-/value-comments. Example: <br>
     * <pre>
     * String before: '  hello there  '
     * String after: 'hello there'
     * Result: removed 4 spaces.
     * </pre>
     */
    public boolean isTrimCommentsEnabled() {
        return isTrimCommentsEnabled;
    }

    /**
     * Enabled by default. Part of post-processing. <br>
     * Trims the loaded key-/value-comments. Example: <br>
     * <pre>
     * String before: '  hello there  '
     * String after: 'hello there'
     * Result: removed 4 spaces.
     * </pre>
     */
    public void setTrimCommentsEnabled(boolean trimCommentsEnabled) {
        isTrimCommentsEnabled = trimCommentsEnabled;
    }


    // CONFIGS FOR MODULES


    /**
     * Enabled by default. <br>
     * Null values return their default values as fallback.<br>
     * See {@link DYModule#getValueByIndex(int)} for details.
     */
    public boolean isReturnDefaultWhenValueIsNullEnabled() {
        return isReturnDefaultWhenValueIsNullEnabled;
    }

    /**
     * Enabled by default. <br>
     * Null values return their default values as fallback. <br>
     * See {@link      * See {@link DYModule#getValueByIndex(int)} for details.#getValueByIndex(int)} for details.
     */
    public DreamYaml setReturnDefaultWhenValueIsNullEnabled(boolean returnDefaultWhenValueIsNullEnabled) {
        this.isReturnDefaultWhenValueIsNullEnabled = returnDefaultWhenValueIsNullEnabled;
        return this;
    }

    /**
     * Enabled by default. <br>
     * If there are no values to write, write the default values.
     */
    public boolean isWriteDefaultValuesWhenEmptyEnabled() {
        return isWriteDefaultValuesWhenEmptyEnabled;
    }

    /**
     * Enabled by default. <br>
     * If there are no values to write, write the default values.
     */
    public DreamYaml setWriteDefaultValuesWhenEmptyEnabled(boolean writeDefaultValuesWhenEmptyEnabled) {
        isWriteDefaultValuesWhenEmptyEnabled = writeDefaultValuesWhenEmptyEnabled;
        return this;
    }

    /**
     * Enabled by default. <br>
     * If there are no comments to write, write the default comments.
     */
    public boolean isWriteDefaultCommentsWhenEmptyEnabled() {
        return isWriteDefaultCommentsWhenEmptyEnabled;
    }

    /**
     * Enabled by default. <br>
     * If there are no comments to write, write the default comments.
     */
    public void setWriteDefaultCommentsWhenEmptyEnabled(boolean writeDefaultCommentsWhenEmptyEnabled) {
        isWriteDefaultCommentsWhenEmptyEnabled = writeDefaultCommentsWhenEmptyEnabled;
    }

    public DYDebugLogger getDebugLogger() {
        return debugLogger;
    }

    public void setDebugLogger(DYDebugLogger debugLogger) {
        this.debugLogger = debugLogger;
    }
}

