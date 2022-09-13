/*
 *  Copyright Osiris Team
 *  All rights reserved.
 *
 *  This software is licensed work.
 *  Please consult the file "LICENSE" for details.
 */

package com.osiris.dyml;

import com.osiris.dyml.exceptions.*;
import com.osiris.dyml.utils.UtilsFile;
import com.osiris.dyml.utils.UtilsYaml;
import com.osiris.dyml.utils.UtilsYamlSection;
import com.osiris.dyml.watcher.DirWatcher;
import com.osiris.dyml.watcher.FileEvent;

import java.io.*;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;

/**
 * The in-memory representation of the full yaml file
 * that contains all of the default and loaded modules.
 */
@SuppressWarnings("ALL")
public class Yaml {

    // Thread safety:
    /**
     * See {@link #lockFile()} and {@link #unlockFile()} for details.
     */
    public static final Map<String, ReentrantLock> pathsAndLocks = new HashMap<>();
    /**
     * A final list, that contains {@link YamlSection}s that are in editing. <br>
     * In contrary to the {@link #loadedModules} list, this list doesn't get cleared <br>
     * and its {@link YamlSection}s stay the same, no matter how often you call {@link #load()}. <br>
     * {@link YamlSection}s get added to this list, by {@link #get(String...)}, {@link #put(String...)}, {@link #add(String...)} or {@link #replace(YamlSection, YamlSection)}.
     */
    public final List<YamlSection> inEditModules = new ArrayList<>();
    /**
     * A final list, that contains loaded {@link YamlSection}s. <br>
     * It gets cleared and refilled with new {@link YamlSection}s in {@link #load()}. <br>
     */
    public final List<YamlSection> loadedModules = new ArrayList<>();
    // Utils:
    public final UtilsYaml utilsYaml = new UtilsYaml(this);
    public final UtilsYamlSection utilsYamlSection = new UtilsYamlSection();
    public final UtilsFile utilsFile = new UtilsFile();
    // Yaml-Content:
    public File file;
    public InputStream inputStream;
    public OutputStream outputStream;
    public String inString;
    public String outString;
    // General:
    /**
     * True if {@link #load()} was called successfully once.
     */
    public boolean isLoaded = false;
    public boolean isIgnoreNotLoadedException = false;
    // Post-Processing:
    /**
     * Enabled by default. Convenience method for toggling post-processing.<br>
     * When disabled none of the post-processing options gets run, no matter if they are enabled/disabled. <br>
     * Post-Processing happens inside {@link #load()}. <br>
     * Some available options are: <br>
     * {@link #isTrimCommentsEnabled} <br>
     * {@link #isTrimLoadedValuesEnabled} <br>
     * {@link #isRemoveQuotesFromLoadedValuesEnabled} <br>
     * etc...
     */
    public boolean isPostProcessingEnabled = true;
    /**
     * Enabled by default. Part of post-processing. <br>
     * Trims the loaded {@link YamlValue}. Example: <br>
     * <pre>
     * String before: '  hello there  '
     * String after: 'hello there'
     * Result: removed 4 spaces.
     * </pre>
     */
    public boolean isTrimLoadedValuesEnabled = true;
    /**
     * Enabled by default. Part of post-processing. <br>
     * Removes quotation marks ("" or '') from the loaded {@link YamlValue}. Example: <br>
     * <pre>
     * String before: "hello there"
     * String after: hello there
     * Result: removed 2 quotation-marks.
     * </pre>
     */
    public boolean isRemoveQuotesFromLoadedValuesEnabled = true;
    /**
     * Enabled by default. Part of post-processing. <br>
     * If {@link YamlValue#asString()} returns null, the whole {@link YamlValue} gets removed from the modules values list. <br>
     */
    public boolean isRemoveLoadedNullValuesEnabled = true;
    /**
     * Enabled by default. Part of post-processing. <br>
     * Trims the comments. Example: <br>
     * <pre>
     * String before: '    hello there  '
     * String after: 'hello there'
     * Result: removed 4 spaces.
     * </pre>
     */
    public boolean isTrimCommentsEnabled = true;
    // Modules:
    /**
     * Enabled by default. <br>
     * Null values return their default values as fallback.<br>
     * See {@link YamlSection#getValueAt(int)} for details.
     */
    public boolean isReturnDefaultWhenValueIsNullEnabled = true;
    /**
     * Enabled by default. <br>
     * If there are no values to write, write the default values.
     */
    public boolean isWriteDefaultValuesWhenEmptyEnabled = true;
    /**
     * Enabled by default. <br>
     * If there are no comments to write, write the default comments.
     */
    public boolean isWriteDefaultCommentsWhenEmptyEnabled = true;
    // Watcher:
    public DirWatcher watcher = null;
    // Logging:
    public DYDebugLogger debugLogger;

    /**
     * Initialises the {@link Yaml} object with useful features enabled. <br>
     * See {@link #Yaml(InputStream, OutputStream, boolean, boolean)} for details.
     */
    public Yaml(InputStream inputStream, OutputStream outputStream) {
        this(inputStream, outputStream, true, false);
    }

    /**
     * Initialises the {@link Yaml} object with useful features enabled. <br>
     * See {@link #Yaml(InputStream, OutputStream, boolean, boolean)} for details.
     */
    public Yaml(InputStream inputStream, OutputStream outputStream, boolean isDebugEnabled) {
        this(inputStream, outputStream, true, isDebugEnabled);
    }

    /**
     * Initialises the {@link Yaml} object.
     *
     * @param inputStream             Yaml content input. Is read from at {@link #load()}. If null, {@link #load()} will do nothing.
     * @param outputStream            Yaml content output. Is written to at {@link #save()}. If null, {@link #save()} will do nothing.
     * @param isPostProcessingEnabled Enabled by default. <br>
     *                                You can also enable/disable specific post-processing options individually: <br>
     *                                See {@link #isPostProcessingEnabled} for details.
     * @param isDebugEnabled          Disabled by default. Shows debugging stuff.
     */
    public Yaml(InputStream inputStream, OutputStream outputStream, boolean isPostProcessingEnabled, boolean isDebugEnabled) {
        this.inputStream = inputStream;
        this.outputStream = outputStream;
        init(isPostProcessingEnabled, isDebugEnabled);
    }


    /**
     * Initialises the {@link Yaml} object with useful features enabled. <br>
     * See {@link #Yaml(String, String, boolean, boolean)} for details.
     */
    public Yaml(String inString, String outString) {
        this(inString, outString, true, false);
    }

    /**
     * Initialises the {@link Yaml} object with useful features enabled. <br>
     * See {@link #Yaml(String, String, boolean, boolean)} for details.
     */
    public Yaml(String inString, String outString, boolean isDebugEnabled) {
        this(inString, outString, true, isDebugEnabled);
    }

    /**
     * Initialises the {@link Yaml} object.
     *
     * @param inString                Yaml content input as String. Is read from at {@link #load()}. If null, {@link #load()} will do nothing.
     * @param outString               Yaml content output as String. Is written to at {@link #save()}. If null, {@link #save()} will do nothing.
     * @param isPostProcessingEnabled Enabled by default. <br>
     *                                You can also enable/disable specific post-processing options individually: <br>
     *                                See {@link #isPostProcessingEnabled} for details.
     * @param isDebugEnabled          Disabled by default. Shows debugging stuff.
     */
    public Yaml(String inString, String outString, boolean isPostProcessingEnabled, boolean isDebugEnabled) {
        this.inString = inString;
        this.outString = outString;
        init(isPostProcessingEnabled, isDebugEnabled);
    }


    /**
     * Initialises the {@link Yaml} object with useful features enabled. <br>
     * See {@link #Yaml(File, boolean, boolean)} for details.
     */
    public Yaml(File file) {
        this(file, true, false);
    }

    /**
     * Initialises the {@link Yaml} object with useful features enabled. <br>
     * See {@link #Yaml(File, boolean, boolean)} for details.
     */
    public Yaml(File file, boolean isDebugEnabled) {
        this(file, true, isDebugEnabled);
    }

    /**
     * Initialises the {@link Yaml} object.
     *
     * @param file                    Your yaml file. If null, {@link #load()} and {@link #save()} will do nothing.
     * @param isPostProcessingEnabled Enabled by default. <br>
     *                                You can also enable/disable specific post-processing options individually: <br>
     *                                See {@link #isPostProcessingEnabled} for details.
     * @param isDebugEnabled          Disabled by default. Shows debugging stuff.
     */
    public Yaml(File file, boolean isPostProcessingEnabled, boolean isDebugEnabled) {
        this.file = file;
        init(isPostProcessingEnabled, isDebugEnabled);
    }


    /**
     * Initialises the {@link Yaml} object with useful features enabled. <br>
     * See {@link #Yaml(String, boolean, boolean)} for details.
     */
    public Yaml(String filePath) {
        this(filePath, true, false);
    }

    /**
     * Initialises the {@link Yaml} object with useful features enabled. <br>
     * See {@link #Yaml(String, boolean, boolean)} for details.
     */
    public Yaml(String filePath, boolean isDebugEnabled) {
        this(filePath, true, isDebugEnabled);
    }

    /**
     * Initialises the {@link Yaml} object.
     *
     * @param filePath                Your yaml files path. If null, {@link #load()} and {@link #save()} will do nothing.
     * @param isPostProcessingEnabled Enabled by default. <br>
     *                                You can also enable/disable specific post-processing options individually: <br>
     *                                See {@link #isPostProcessingEnabled} for details.
     * @param isDebugEnabled          Disabled by default. Shows debugging stuff.
     */
    public Yaml(String filePath, boolean isPostProcessingEnabled, boolean isDebugEnabled) {
        this.file = new File(filePath);
        init(isPostProcessingEnabled, isDebugEnabled);
    }


    private void init(boolean isPostProcessingEnabled, boolean isDebugEnabled) {
        this.isPostProcessingEnabled = isPostProcessingEnabled;
        if (isDebugEnabled)
            debugLogger = new DYDebugLogger(System.out);
        else
            debugLogger = new DYDebugLogger(null);
    }

    /**
     * Loads the files' or streams' contents into memory by parsing it into modules({@link YamlSection}). <br>
     * Creates a new file and its parent directories if they didn't exist already. <br>
     * You can return the list of modules with {@link #getAllLoaded()}. <br>
     * Remember, that this updates your {@link #inEditModules} values and parent/child modules. <br>
     * Also note that it post-processes the 'loaded modules'. <br>
     * You can also enable/disable specific post-processing options individually: <br>
     * See {@link #isPostProcessingEnabled()} for details.
     */
    public Yaml load() throws IOException, YamlReaderException, IllegalListException, DuplicateKeyException {
        debugLogger.log(this, "Executing load()");
        if (file != null && !file.exists()) {
            if (file.getParentFile() != null) file.getParentFile().mkdirs();
            file.createNewFile();
        }
        new YamlReader().parse(this);
        isLoaded = true;
        return this;
    }

    /**
     * If you access the same yaml file from multiple threads, its recommended to lock the file before loading it. <br>
     * Remember to {@link #unlockFile()} so that other threads can work with the file too. <br>
     * If you don't do that, other threads will stay stuck at {@link #lockFile()} forever. <br>
     * Example: <br>
     * <pre>
     *     DreamYaml yaml = new DreamYaml("example.yml");
     *     yaml.lockFile();
     *     yaml.load();
     *     // Do changes to file here
     *     yaml.save();
     *     yaml.unlockFile();
     * </pre>
     */
    public void lockFile() {
        if (file != null) {
            ReentrantLock lock;
            synchronized (pathsAndLocks) {
                if (pathsAndLocks.containsKey(file.getAbsolutePath()))
                    lock = pathsAndLocks.get(file.getAbsolutePath());
                else {
                    lock = new ReentrantLock();
                    pathsAndLocks.put(file.getAbsolutePath(), lock);
                }
            }
            lock.lock(); // If another thread has already the locked, the current thread will wait at this position until it gets unlocked
        }
    }


    /**
     * If you access the same yaml file from multiple threads, its recommended to lock the file before loading it. <br>
     * Remember to {@link #unlockFile()} so that other threads can work with the file too. <br>
     * If you don't do that, other threads will stay stuck at {@link #lockFile()} forever. <br>
     * Example: <br>
     * <pre>
     *     DreamYaml yaml = new DreamYaml("example.yml");
     *     yaml.lockFile();
     *     yaml.load();
     *     // Do changes to file here
     *     yaml.save();
     *     yaml.unlockFile();
     * </pre>
     */
    public void unlockFile() {
        if (file != null) {
            ReentrantLock lock;
            synchronized (pathsAndLocks) {
                if (pathsAndLocks.containsKey(file.getAbsolutePath())) {
                    lock = pathsAndLocks.get(file.getAbsolutePath()); // If another thread has already the locked, the current thread will wait until it gets unlocked
                    lock.unlock();
                    if (!lock.hasQueuedThreads())
                        pathsAndLocks.remove(file.getAbsolutePath());
                }
            }
        }
    }

    /**
     * Caution! This method will completely reset/remove all information from your yaml file, but not delete it.
     * To delete, use {@link File#delete()} instead. You can get the file via {@link #getFile()}.
     * Also the {@link #getAllLoaded()} list is empty after this operation.
     * The {@link #getAllInEdit()} list is not affected.
     */
    public Yaml reset() throws IOException, DuplicateKeyException, YamlReaderException, IllegalListException, YamlWriterException {
        debugLogger.log(this, "Executing reset()");
        if (inputStream == null) {
            if (!isLoaded) this.load();
            new YamlWriter().parse(this, true, true);
            this.load();
        }
        return this;
    }

    /**
     * Convenience method for saving and loading afterwards. <br>
     * See {@link #save(boolean)} and {@link #load()} for details.
     */
    public Yaml saveAndLoad() throws IOException, DuplicateKeyException, YamlReaderException, IllegalListException, YamlWriterException {
        if (!isLoaded) this.load();
        this.save();
        this.load();
        return this;
    }

    /**
     * For more details see: {@link #save(boolean)}
     */
    public Yaml save() throws YamlWriterException, IOException, DuplicateKeyException, YamlReaderException, IllegalListException {
        this.save(false);
        return this;
    }

    /**
     * <p style="color:red;">IMPORTANT: Stuff that isn't supported by DreamYaml (see features.yml) wont be parsed and thus removed from the file after you save it!</p>
     * Parses and saves the current modules to the provided yaml file. <br>
     * Note that this method won't reload the file after. Use {@link #saveAndLoad()} instead. <br>
     * It's recommended to keep {@link #load()} and {@link #save()} timely close to each other, so the user  <br>
     * can't change the values in the meantime. <br>
     * If the yaml file is missing some {@link #inEditModules}, these get created using their values/default values.<br>
     * More info on this topic: <br>
     * {@link #isWriteDefaultValuesWhenEmptyEnabled()} <br>
     * {@link #createUnifiedList(List, List)} <br>
     * {@link YamlSection#setDefValues(List)} <br>
     *
     * @param overwrite false by default.
     *                  If true, the yaml file gets overwritten with only modules from the {@link #inEditModules} list.
     *                  That means that everything that wasn't added to that list (loaded modules) will not exist in the file.
     */
    public Yaml save(boolean overwrite) throws IOException, DuplicateKeyException, YamlReaderException, IllegalListException, YamlWriterException {
        debugLogger.log(this, "Executing save()");
        if (!isLoaded) this.load();
        new YamlWriter().parse(this, overwrite, false);
        return this;
    }

    /**
     * Returns the {@link YamlSection} with matching keys or null. <br>
     * Details: <br>
     * Searches the {@link #inEditModules} list, and the {@link #loadedModules} list for
     * the matching {@link YamlSection}
     * and returns it. Null if no matching {@link YamlSection} for the provided keys could be found. <br>
     * If the {@link YamlSection} was found in the {@link #loadedModules} list, it gets removed from there and
     * added to the {@link #inEditModules} list. <br>
     */
    public YamlSection get(String... keys) {
        Objects.requireNonNull(keys);
        return get(Arrays.asList(keys));
    }

    /**
     * Returns the {@link YamlSection} with matching keys or null. <br>
     * Details: <br>
     * Searches the {@link #inEditModules} list, and the {@link #loadedModules} list for
     * the matching {@link YamlSection}
     * and returns it. Null if no matching {@link YamlSection} for the provided keys could be found. <br>
     * If the {@link YamlSection} was found in the {@link #loadedModules} list, it gets removed from there and
     * added to the {@link #inEditModules} list. <br>
     */
    public YamlSection get(List<String> keys) {
        Objects.requireNonNull(keys);
        debugLogger.log(this, "Executing get(" + keys.toString() + ")");
        YamlSection module = utilsYamlSection.getExisting(keys, inEditModules);
        if (module == null) {
            module = utilsYamlSection.getExisting(keys, loadedModules);
            if (module != null) {
                inEditModules.add(module);
            }
        }
        return module;
    }

    public YamlSection put(String... keys) throws NotLoadedException, IllegalKeyException {
        Objects.requireNonNull(keys);
        List<String> list = new ArrayList<>(Arrays.asList(keys));
        return put(list);
    }

    /**
     * Returns the existing {@link YamlSection} with matching keys, or adds a new one. <br>
     * Details: <br>
     * Searches for duplicate in the {@link #inEditModules}, <br>
     * and the {@link #loadedModules} list and returns it if could find one. <br>
     * Otherwise, it creates a new {@link YamlSection} from the <br>
     * provided keys, adds it to the {@link #inEditModules} list and returns it. <br>
     * Note: <br>
     * If you have a populated yaml file and add a completely new {@link YamlSection}, it gets added to the bottom of the hierarchy. <br>
     * Example yaml file before adding the new {@link YamlSection} with keys [g0, g1-new]:
     * <pre>
     * g0:
     *   g1-m1:
     *   g1-m2:
     * </pre>
     * Example yaml file after adding the new {@link YamlSection} with keys [g0, g1-new]:
     * <pre>
     * g0:
     *   g1-m1:
     *   g1-m2:
     *   g1-new:
     * </pre>
     */
    public YamlSection put(List<String> keys) throws NotLoadedException, IllegalKeyException {
        Objects.requireNonNull(keys);
        debugLogger.log(this, "Executing add(" + keys.toString() + ")");

        YamlSection section = utilsYamlSection.getExisting(keys, inEditModules);
        if (section != null)
            return section;
        section = utilsYamlSection.getExisting(keys, loadedModules);
        if (section != null) {
            inEditModules.add(section);
            return section;
        }
        try {
            return add(keys);
        } catch (NotLoadedException | IllegalKeyException e) {
            throw e;
        } catch (DuplicateKeyException ignored) {
            debugLogger.log(this, "This shouldn't happen! Error while adding " + keys.toString() + " Message: " + ignored.getMessage());
        }
        return section;
    }

    public YamlSection add(String... keys) throws NotLoadedException, IllegalKeyException, DuplicateKeyException {
        Objects.requireNonNull(keys);
        List<String> list = new ArrayList<>(Arrays.asList(keys));
        return add(list);
    }


    /**
     * Creates a new {@link YamlSection}, with the provided keys, adds it to the modules list and returns it. <br>
     * See {@link #add(YamlSection)} for details.
     */
    public YamlSection add(List<String> keys) throws NotLoadedException, IllegalKeyException, DuplicateKeyException {
        Objects.requireNonNull(keys);
        return add(keys, null, null, null);
    }

    /**
     * Creates a new {@link YamlSection}, with the provided keys, adds it and returns it. <br>
     * See {@link #add(YamlSection)} for details.
     */
    public YamlSection add(List<String> keys, List<SmartString> defaultValues, List<SmartString> values, List<String> comments) throws NotLoadedException, IllegalKeyException, DuplicateKeyException {
        return add(new YamlSection(this, keys, defaultValues, values, comments));
    }

    /**
     * Adds the provided {@link YamlSection} or throws exception if it already exists. <br>
     * Note that null or duplicate KEYS are not allowed. <br>
     * Details: <br>
     * Searches for duplicates in the {@link #inEditModules}, and the {@link #loadedModules} list and throws
     * {@link DuplicateKeyException} if it could find one. Otherwise, it creates a new {@link YamlSection} from the
     * provided keys, adds it to the {@link #inEditModules} list and returns it. <br>
     * Note: <br>
     * If you have a populated yaml file and add a completely new {@link YamlSection}, it gets added to the bottom of the hierarchy. <br>
     * Example yaml file before adding the new {@link YamlSection} with keys [g0, g1-new]:
     * <pre>
     * g0:
     *   g1-m1:
     *   g1-m2:
     * </pre>
     * Example yaml file after adding the new {@link YamlSection} with keys [g0, g1-new]:
     * <pre>
     * g0:
     *   g1-m1:
     *   g1-m2:
     *   g1-new:
     * </pre>
     *
     * @param module module to add.
     * @return the added module.
     * @throws NotLoadedException    if the yaml file has not been loaded once yet
     * @throws DuplicateKeyException if another module with the same keys already exists
     */
    public YamlSection add(YamlSection module) throws IllegalKeyException, NotLoadedException, DuplicateKeyException {
        Objects.requireNonNull(module);
        Objects.requireNonNull(module.getKeys());
        debugLogger.log(this, "Executing add(" + module.getKeys().toString() + ")");
        if (module.getKeys().isEmpty()) throw new IllegalKeyException("Keys list of this module cannot be empty!");
        if (!isLoaded) throw new NotLoadedException(); // load() should've been called at least once before
        if (module.getKeys().contains(null))
            throw new IllegalKeyException("The provided keys list contains null key(s)! This is not allowed!");

        if (utilsYamlSection.getExisting(module, this.inEditModules) != null)
            throw new DuplicateKeyException(getSource().toString(), module.getKeys().toString());

        if (utilsYamlSection.getExisting(module, this.loadedModules) != null)
            throw new DuplicateKeyException(getSource().toString(), module.getKeys().toString());

        int closestParentIndex = utilsYamlSection.getClosestParentIndex(module.getKeys(), inEditModules);
        if (closestParentIndex == -1) {
            this.inEditModules.add(module);
            return module;
        }
        if (closestParentIndex + 1 <= inEditModules.size()) {
            this.inEditModules.add(closestParentIndex + 1, module);
            return module;
        }
        this.inEditModules.add(module);
        return module;
    }

    /**
     * Replaces {@link YamlSection}, with the provided {@link YamlSection}. <br>
     * Details: <br>
     * Searches the {@link #inEditModules} list, and the {@link #loadedModules} list for the {@link YamlSection} to replace. <br>
     * Replaces it and returns the replacement, or null if {@link YamlSection} to replace couldn't be found. <br>
     * If the {@link YamlSection} to replace was found in the {@link #loadedModules} list, it gets removed from there and <br>
     * the replacement gets added to the {@link #inEditModules} list. <br>
     */
    public YamlSection replace(YamlSection moduleToReplace, YamlSection newModule) {
        debugLogger.log(this, "Executing replace()");
        Objects.requireNonNull(moduleToReplace);
        Objects.requireNonNull(newModule);
        YamlSection module = utilsYamlSection.getExisting(moduleToReplace, inEditModules);
        if (module == null) {
            module = utilsYamlSection.getExisting(moduleToReplace, loadedModules);
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
    public Yaml remove(String... keys) {
        Objects.requireNonNull(keys);
        remove(new YamlSection(this, keys));
        return this;
    }

    public void removeAll() {
        inEditModules.clear();
        loadedModules.clear();
    }

    /**
     * Removes the module from the yaml file once {@link #save()} was called. <br>
     */
    public Yaml remove(YamlSection module) {
        debugLogger.log(this, "Executing remove()");
        YamlSection addedM = utilsYamlSection.getExisting(module, inEditModules);
        if (addedM != null)
            this.inEditModules.remove(addedM);
        YamlSection loadedM = utilsYamlSection.getExisting(module, loadedModules);
        if (loadedM != null)
            this.loadedModules.remove(loadedM);
        return this;
    }


    // WATCHER:


    /**
     * Registers this {@link #file} and adds the provided {@link Consumer} to the list. <br>
     * Once a file event happens, the listeners method gets executed. <br>
     * Details: <br>
     * If {@link #watcher} is null, this method creates and starts a new {@link DirWatcher}.
     */
    public Yaml addFileEventListener(Consumer<FileEvent> listener) throws IOException {
        if (watcher == null) watcher = DirWatcher.get(file, false);
        watcher.watchFile(file, listener);
        return this;
    }

    public Yaml removeFileEventListener(Consumer<FileEvent> listener) throws Exception {
        Objects.requireNonNull(watcher);
        watcher.removeListeners(listener);
        return this;
    }

    /**
     * This method returns a new unified list containing the {@link #loadedModules} and {@link #inEditModules} merged together. <br>
     * The loaded modules list is used as 'base' and is overwritten/extended by the {@link #inEditModules} list. <br>
     * This ensures, that the structure(hierarchies) of the loaded file stay the same <br>
     * and that new modules are inserted in the correct position. <br>
     * Logic: <br>
     * 1. If the loaded modules list is empty, nothing needs to be done! Return {@link #inEditModules}. <br>
     * 2. Else go through the loaded modules and compare each module with the {@link #inEditModules} list.
     * If there is an inEdit module with the same keys, add it to the unified list instead of the loaded module. <br>
     * 3. If there are NEW modules in the {@link #inEditModules} list, insert them into the right places of unified list. <br>
     *
     * @return a fresh unified list containing loaded modules extended by {@link #inEditModules}.
     */
    public List<YamlSection> createUnifiedList(List<YamlSection> inEditModules, List<YamlSection> loadedModules) {
        if (loadedModules.isEmpty()) return inEditModules;

        debugLogger.log(this, "### CREATE UNIFIED LIST ###");
        debugLogger.log(this, "This process creates a single list out of the 'inEditModules' and 'loadedModules' lists.");
        debugLogger.log(this, "Printing contents of both lists:");
        debugLogger.log(this, "EM: inEditModule, LM: loadedModule.");
        for (YamlSection m :
                inEditModules) {
            debugLogger.log(this, "EM: " + m.getKeys());
        }

        for (YamlSection m :
                loadedModules) {
            debugLogger.log(this, "LM: " + m.getKeys());
        }

        List<YamlSection> copyInEditModules = new CopyOnWriteArrayList<>(inEditModules);
        List<YamlSection> unifiedList = new ArrayList<>();
        // Go through the loadedModules list and take its structure.
        debugLogger.log(this, "Create the unified list: ");
        debugLogger.log(this, "We go thorough the loadedModules list, to keep its structure and");
        debugLogger.log(this, "add its modules to the unified list. If there is a inEditModule that has the");
        debugLogger.log(this, "same keys as the loadedModule, it gets added instead.");
        debugLogger.log(this, "EM: inEditModule, LM: loadedModule.");
        for (YamlSection loadedModule :
                loadedModules) {
            // Check if there is the same 'inEdit module' available
            YamlSection existing = utilsYamlSection.getExisting(loadedModule, copyInEditModules);
            if (existing != null) {
                unifiedList.add(existing);
                // Also remove it from its own list, so at the end there are only 'new' modules in that list
                copyInEditModules.remove(existing);
                debugLogger.log(this, "+ EM " + existing.getKeys().toString() + " to unified.");
            } else {
                unifiedList.add(loadedModule);
                debugLogger.log(this, "+ LM " + loadedModule.getKeys().toString() + " to unified.");
            }
        }

        debugLogger.log(this, "Now go through the copyInEditModules(" + copyInEditModules.size() + ") list, which");
        debugLogger.log(this, "should now only contain new modules, that didn't exist in the loadedModules list.");
        debugLogger.log(this, "Insert those modules, into the unified list, at the right place:");
        debugLogger.log(this, "NM: newModule.");


        // The copyInEditModules, now only contains completely new modules.
        // Go through that list, add G0 modules to the end of the unifiedModules list and
        // other generations to their respective parents, also at the last position.
        for (YamlSection newModule :
                copyInEditModules) {

            if (newModule.getKeys().size() > 1) {
                // Find the module with the most matching keys, to determine its position in the hierarchy
                int currentIndex = 0; // The current index in the loop below
                int bestMatchIndex = 0; // The index of the module, with the highest count of matching keys
                int highestCountOfMatchingKeys = 0;
                for (YamlSection unifiedModule : // Compare each unified modules keys against the new modules keys
                        unifiedList) {
                    for (int j = 0; j < unifiedModule.getKeys().size(); j++) {
                        if (unifiedModule.getKeys().get(j).equals(newModule.getKeys().get(j))) {
                            // Not >= because we don't want to get child modules. In the example below we are searching for [g0, g1]:
                            // g0:
                            //   g1: <--- Keys as list: [g0, g1]
                            //     g2: <--- Keys as list: [g0, g1, g2]
                            // As you can see above, both contain g0 and g1 in their keys list.
                            // With >= we ensure, that NOT only the first module is picked as best match, but
                            // we get to the last child modules position.
                            // This ensures, that the new module is added to the end of that generation.
                            // g0:
                            //   g1-1:
                            //   g1-2: <---
                            if (j + 1 >= highestCountOfMatchingKeys) { // Since j is the index, add +1 to get the actual count
                                debugLogger.log(this, "Set bestMatchIndex to " + currentIndex + " because of " + (j + 1) + "x similar keys found in unifiedModule, while searching for " + newModule.getKeys().toString());
                                bestMatchIndex = currentIndex;
                                highestCountOfMatchingKeys = j + 1; // Since j is the index, add +1 to get the actual count
                            }
                        } else
                            break;
                    }
                    currentIndex++;
                }


                if (highestCountOfMatchingKeys != newModule.getKeys().size() - 1) { // -1 Because we want the parent module
                    // This means that the 'parent' module has less matching keys and thus cannot be the direct parent.
                    // Lets imagine we added a new module with these keys [g0, g1, g2, g3], but the loaded file looks like this:
                    // g0:
                    //   g1: <---
                    // In this case the highest count of matching keys is 2: [g0, g1], but the parent should be [g0, g1, g2], aka
                    // the highest count of matching keys should be 3.
                    // Thus we need to create and add those missing modules in between, as fillers.
                    YamlSection beforeFillerModule = null;
                    for (int i = highestCountOfMatchingKeys; i < newModule.getKeys().size() - 1; i++) { // -1 Because we want the parent module
                        YamlSection fillerModule = new YamlSection(this, newModule.getKeys().subList(0, i), null, null, null);
                        bestMatchIndex++; // So that the new filler module gets added in the right position
                        unifiedList.add(bestMatchIndex, fillerModule);
                        if (beforeFillerModule != null) {
                            beforeFillerModule.addChildSections(fillerModule);
                            fillerModule.setParentSection(beforeFillerModule);
                        }
                        debugLogger.log(this, "+ Filler at index " + bestMatchIndex + " " + fillerModule.getKeys().toString());
                        beforeFillerModule = fillerModule;
                    }
                }

                try {
                    YamlSection parent = unifiedList.get(bestMatchIndex);
                    bestMatchIndex++; // +1 because we currently got the index for the parent, before this module.
                    unifiedList.add(bestMatchIndex, newModule);
                    parent.addChildSections(newModule);
                    newModule.setParentSection(parent);
                    debugLogger.log(this, "> Insert at index " + bestMatchIndex + " " + newModule.getKeys().toString());
                } catch (Exception e) {
                    debugLogger.log(this, "! Failed to find parent for: " + newModule.getKeys().toString() + " Probably a new module.");
                    unifiedList.add(0, newModule); // Can be a completely new >G0 module
                    debugLogger.log(this, "+ NM " + newModule.getKeys().toString());
                }
            } else {
                unifiedList.add(0, newModule); // G0 modules get added to the end of the file
                debugLogger.log(this, "+ NM at G0 " + newModule.getKeys().toString());
            }
        }

        debugLogger.log(this, "Finished creation of unified list. Quick overview of the result:");
        debugLogger.log(this, "UM: unifiedModule.");
        for (YamlSection m :
                unifiedList) {
            debugLogger.log(this, "UM: " + m.getKeys().toString());
        }
        debugLogger.log(this, "### FINISHED CREATE UNIFIED LIST ###");
        return unifiedList;
    }


    /**
     * Returns a fresh unified, ordered list with {@link #loadedModules} and {@link #inEditModules} merged together. <br>
     * Note that this is not the original list, but a copy and thus any changes to it, won't have affect and changes to the original
     * won't be reflected in this copy. <br>
     * This list is the one, that gets written to the yaml file. <br>
     * See {@link #createUnifiedList(List, List)} for details.
     */
    public List<YamlSection> getAll() {
        return createUnifiedList(this.inEditModules, this.loadedModules);
    }

    /**
     * <p style="color:red;">Do not modify this list directly, unless you know what you are doing!</p>
     * Returns a list containing all loaded modules. <br>
     * This is the original list. Note that its modules get updated every time {@link #load()} is called.
     * Its modules, do not contain default values.
     */
    public List<YamlSection> getAllLoaded() {
        return loadedModules;
    }

    /**
     * Convenience method for returning the last module from the {@link #loadedModules} list.
     */
    public YamlSection getLastLoadedModule() {
        return loadedModules.get(loadedModules.size() - 1);
    }

    /**
     * <p style="color:red;">Do not modify this list directly, unless you know what you are doing!</p>
     * Returns a list containing all {@link YamlSection}s that are being edited.
     * Modules should only be added by {@link #put(String...)}/{@link #add(String...)} and never by this lists own add() method.
     * This list is not affected by {@link #load()}, unlike the
     * 'loaded modules' list, which can be returned by {@link #getAllLoaded()}.
     */
    public List<YamlSection> getAllInEdit() {
        return inEditModules;
    }

    /**
     * Convenience method for returning the last module from the {@link #inEditModules} list.
     */
    public YamlSection getLastInEditModule() {
        return inEditModules.get(inEditModules.size() - 1);
    }

    /**
     * Prints out all lists.
     */
    public Yaml printAll() {
        printLoaded();
        printInEdit();
        printUnified();
        System.out.println();
        return this;
    }

    /**
     * Prints out all modules in the loaded list.
     * For more info see {@link UtilsYaml#printLoaded(PrintStream)}}.
     */
    public Yaml printLoaded() {
        utilsYaml.printLoaded(System.out);
        return this;
    }

    /**
     * Prints out all modules in the added list.
     * For more info see {@link UtilsYaml#printInEdit(PrintStream)}}.
     */
    public Yaml printInEdit() {
        utilsYaml.printInEdit(System.out);
        return this;
    }

    /**
     * Prints out all modules in the unified list.
     * For more info see {@link #createUnifiedList(List, List)} and {@link UtilsYaml#printUnified(PrintStream)}}.
     */
    public Yaml printUnified() {
        utilsYaml.printUnified(System.out);
        return this;
    }

    /**
     * Prints out the files content.
     */
    public Yaml printFile() {
        Objects.requireNonNull(file);
        utilsFile.printFile(file);
        return this;
    }

    public String getFilePath() {
        return file != null ? file.getAbsolutePath() : "";
    }

    public File getFile() {
        return file;
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    public OutputStream getOutputStream() {
        return outputStream;
    }

    public UtilsYaml getUtilsDreamYaml() {
        return utilsYaml;
    }

    /**
     * Returns the yml files name without its extension.
     */
    public String getFileNameWithoutExt() throws NotLoadedException {
        if (inputStream != null) return "<InputStream>";
        if (!isLoaded) throw new NotLoadedException();
        return file.getName().replaceFirst("[.][^.]+$", ""); // Removes the file extension
    }

    public Object getSource() {
        if (file != null) return file;
        else if (inString != null) return inString;
        else if (inputStream != null) return inputStream;
        else return null;
    }
}

