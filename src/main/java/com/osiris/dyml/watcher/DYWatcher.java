/*
 * Copyright Osiris Team
 * All rights reserved.
 *
 * This software is copyrighted work licensed under the terms of the
 * AutoPlug License.  Please consult the file "LICENSE" for details.
 */

package com.osiris.dyml.watcher;

import com.osiris.dyml.DreamYaml;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

import static java.nio.file.StandardWatchEventKinds.*;


/**
 * A {@link DYWatcher} is able to watch multiple files/directories <br>
 * (as well as subdirectories) and notify <br>
 * their listeners if an event happens.
 */
public class DYWatcher extends Thread {
    public static List<DYWatcher> activeWatchers = new CopyOnWriteArrayList<>();

    /**
     * See {@link #getForPath(Path)} for details.
     */
    public static synchronized DYWatcher getForFile(File file) throws IOException {
        return getForPath(file.toPath());
    }

    /**
     * Checks if there is an existing {@link DYWatcher} for <br>
     * the provided path and returns it. If the path is a file, it checks for the parent directory. <br>
     * Otherwise creates a new {@link DYWatcher} for the provided path and returns it. <br>
     */
    public static synchronized DYWatcher getForPath(Path path) throws IOException {
        Path dirPath = path;
        if (path.toFile().isFile()) dirPath = path.getParent();
        for (DYWatcher watcher :
                activeWatchers) {
            for (File file :
                    watcher.registeredFiles) {
                if (file.isDirectory())
                    if (file.toPath().equals(dirPath))
                        return watcher;
            }
        }
        return new DYWatcher(path);
    }

    /**
     * A list containing files, that notify their listeners when an event happens. <br>
     * Note that this list also contains the files parent directories.
     */
    private final List<DYRegisteredFile> registeredFiles = new CopyOnWriteArrayList<>();
    private final Map<Path, WatchKey> directoriesAndWatchKeys = new HashMap<>();
    private final WatchService watchService;
    private boolean isWatchSubDirs = false;

    /**
     * <p style="color:red">Its recommended to use the static method {@link DYWatcher#getForPath(Path)} to get a {@link DYWatcher} instead!</p>
     * See {@link #DYWatcher(Path)} for details.
     */
    public DYWatcher() throws IOException {
        this(null);
    }

    /**
     * <p style="color:red">Its recommended to use the static method {@link DYWatcher#getForPath(Path)} to get a {@link DYWatcher} instead!</p>
     * <p style="color:red">The reason for that is performance.</p>
     * Initialises a new {@link DYWatcher} and watches the provided path. <br>
     * Note that this method will also call {@link #start()} to start its thread. <br>
     * You can register more files to it via {@link #addFileAndListeners(File, List)}.
     *
     * @param path Can be a file or a directory.
     */
    public DYWatcher(Path path) throws IOException {
        this.watchService = FileSystems.getDefault().newWatchService();
        if (path != null)
            addFileAndListeners(path.toFile(), null);
        start();
    }

    @Override
    public void run() {
        super.run();
        try {
            WatchKey key;
            while ((key = watchService.take()) != null) {
                List<DYRegisteredFile> registeredFiles = getRegisteredFilesByParentWatchKey(key);
                for (WatchEvent<?> event :
                        key.pollEvents()) {
                    for (DYRegisteredFile file :
                            registeredFiles) {
                        if (file.getName().equals(event.context().toString())) // event.context() returns the file name for its event
                            for (DYFileEventListener<DYFileEvent> listener :
                                    file.getListeners()) {
                                listener.runOnEvent(new DYFileEvent(file, event));
                            }
                    }
                }
                key.reset();
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("DYWatcher crashed! Please restart it to keep using its functions!");
        }
    }

    /**
     * Stops this watchers thread and its service.
     */
    public void terminate() throws IOException {
        if (!this.isInterrupted()) this.interrupt();
        watchService.close();
        activeWatchers.remove(this);
    }

    public void watchDir(Path dirPath, boolean watchSubdirectories) throws IOException {
        // Check if the dir already exists
        Path existingDir = null;
        for (Path dir :
                directoriesAndWatchKeys.keySet()) { // Check for existing registered file
            if (dir.equals(dirPath)) {
                existingDir = dir;
                break;
            }
        }

        if (existingDir == null) {
            WatchKey watchKey = dirPath.register(watchService, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY, OVERFLOW);
            directoriesAndWatchKeys.put(dirPath, watchKey);
            if (watchSubdirectories) { // Add subdirectories if enabled
                Files.walkFileTree(dirPath, new SimpleFileVisitor<Path>() {
                    @Override
                    public FileVisitResult preVisitDirectory(Path path, BasicFileAttributes attrs)
                            throws IOException {
                        watchDir(path, true);
                        return FileVisitResult.CONTINUE;
                    }
                });
            }
        }
    }

    /**
     * Note that currently there is no way be the provided <br>
     * underlying files API to unwatch a directory. <br>
     * There could be workarounds to achieve this though. <br>
     */
    private void unwatchDir() {
        // TODO
    }

    /**
     * Register the provided {@link Path} to this {@link DYWatcher}. <br>
     * Now the {@link DYWatcher} is able to listen for events on that path. <br>
     * If the provided filePath is a file, its parent directory gets registered. <br>
     * Also checks if the provided path already exists in the {@link #registeredFiles} list. <br>
     * If it does, that path will be used and the provided listeners will get added to the already existing listeners list <br>
     * and then the already existing {@link DYRegisteredFile} gets returned. <br>
     * Otherwise we register the provided path, add the newly created {@link DYRegisteredFile} object to the <br>
     * {@link #registeredFiles} list and return it. <br>
     *
     * @param filePath Can be a file or a directory.
     * @return Null if the filePath is a directory, otherwise a {@link DYRegisteredFile}.
     */
    private DYRegisteredFile registerFilePath(Path filePath,
                                              List<DYFileEventListener<DYFileEvent>> listeners,
                                              boolean watchSubdirectories,
                                              DreamYaml yaml) throws IOException {
        Objects.requireNonNull(filePath);
        Objects.requireNonNull(listeners);

        WatchKey watchKey;
        if (filePath.toFile().isDirectory()) {
            watchDir(filePath, watchSubdirectories);
            return null;
        } else {
            watchDir(filePath.getParent(), watchSubdirectories);
            WatchKey parentWatchKey = directoriesAndWatchKeys.get(filePath.getParent());
            DYRegisteredFile existingRegisteredFile = getRegisteredFileByPath(filePath);
            if (existingRegisteredFile != null) { // This means the path already was registered and we don't have to do it again
                // instead we only add the provided listeners to it.
                existingRegisteredFile.getListeners().addAll(listeners);
                existingRegisteredFile.setYaml(yaml); // However update the yaml
                return existingRegisteredFile;
            } else {
                // This means there is no file registered yet. Do that.
                DYRegisteredFile registeredFile = new DYRegisteredFile(
                        filePath.toString(),
                        this,
                        parentWatchKey,
                        listeners);
                registeredFile.setYaml(yaml); // Can be null
                registeredFiles.add(registeredFile);
                return registeredFile;
            }
        }
    }

    private void unregisterPath(Path path) {
        DYRegisteredFile file = getRegisteredFileByPath(path);
        if (file != null) {
            file.getParentWatchKey().cancel();
            registeredFiles.remove(file);
        }
    }

    /**
     * Null when no {@link DYRegisteredFile} was found with the provided path, <br>
     * in the {@link #registeredFiles} list.
     */
    private DYRegisteredFile getRegisteredFileByPath(Path path) {
        for (DYRegisteredFile registeredFileFromList :
                registeredFiles) { // Check for existing registered file
            if (registeredFileFromList.toPath().equals(path)) {
                return registeredFileFromList;
            }
        }
        return null;
    }

    private List<DYRegisteredFile> getRegisteredFilesByParentWatchKey(WatchKey watchKey) {
        List<DYRegisteredFile> files = new ArrayList<>();
        for (DYRegisteredFile registeredFileFromList :
                registeredFiles) { // Check for existing registered file
            if (registeredFileFromList.getParentWatchKey().equals(watchKey)) {
                files.add(registeredFileFromList);
            }
        }
        return files;
    }

    /**
     * See {@link #addFileAndListeners(File, List, boolean)} for details.
     */
    public void addYamlAndListeners(DreamYaml yaml,
                                    List<DYFileEventListener<DYFileEvent>> listeners,
                                    boolean watchSubdirectories) throws IOException {
        addFileAndListeners(yaml.getFile(), listeners, watchSubdirectories);

    }

    /**
     * Uses the boolean value of {@link #isWatchSubDirs}. Its true by default. <br>
     * See {@link #addFileAndListeners(File, List, boolean, DreamYaml)} for details. <br>
     */
    public void addFileAndListeners(File fileToWatch,
                                    List<DYFileEventListener<DYFileEvent>> listeners) throws IOException {
        addFileAndListeners(fileToWatch, listeners, isWatchSubDirs);
    }

    /**
     * See {@link #addFileAndListeners(File, List, boolean, DreamYaml)} for details. <br>
     */
    public void addFileAndListeners(File fileToWatch,
                                    List<DYFileEventListener<DYFileEvent>> listeners,
                                    boolean watchSub) throws IOException {
        addFileAndListeners(fileToWatch, listeners, watchSub, null);
    }

    /**
     * Creates a {@link DYRegisteredFile} object out of the provided information, and adds it to the {@link #registeredFiles} list. <br>
     * After that your listeners will be able to receive {@link DYFileEvent}s. <br>
     * Note that if the provided file/path already exists in that list, <br>
     * we wont register it, but add the provided listeners to the existing file. <br>
     * See {@link #registerFilePath(Path, List, boolean, DreamYaml)} for details. <br>
     * The event kinds, that trigger an event: <br>
     * {@link StandardWatchEventKinds#ENTRY_CREATE} <br>
     * {@link StandardWatchEventKinds#ENTRY_DELETE} <br>
     * {@link StandardWatchEventKinds#ENTRY_MODIFY} <br>
     * {@link StandardWatchEventKinds#OVERFLOW} <br>
     *
     * @param fileToWatch Can be a file or directory.
     * @param listeners   A list containing listeners, that receive this files events.
     *                    If null we create an empty {@link ArrayList}.
     * @param watchSub    Disabled by default. <br>
     *                    If enabled however, automatically registers this files parent directories, subdirectories.
     *                    Note that the boolean value of {@link #isWatchSubDirs} is ignored.
     * @param yaml        If this is a file is a yaml file, you should pass over its {@link DreamYaml} object.
     *                    Can be null, if its not.
     */
    public void addFileAndListeners(File fileToWatch,
                                    List<DYFileEventListener<DYFileEvent>> listeners,
                                    boolean watchSub,
                                    DreamYaml yaml) throws IOException {
        Objects.requireNonNull(fileToWatch);
        if (listeners == null) {
            registerFilePath(fileToWatch.toPath(), new ArrayList<>(0), watchSub, yaml);
        } else {
            registerFilePath(fileToWatch.toPath(), listeners, watchSub, yaml);
        }
    }

    public void removeFileAndListeners(File fileToRemove) {
        unregisterPath(fileToRemove.toPath());
    }

    public boolean isWatchSubDirs() {
        return isWatchSubDirs;
    }

    /**
     * Watch all subdirectories or not. Default is true.
     */
    public void setWatchSubDirs(boolean watchSubDirs) {
        this.isWatchSubDirs = watchSubDirs;
    }

    public WatchService getWatchService() {
        return watchService;
    }

    public void printDetails() {
        System.out.println("Watcher: " + this);
        StringBuilder dirs = new StringBuilder("Dirs:");
        for (DYRegisteredFile file :
                registeredFiles) {
            if (file.isDirectory())
                dirs.append(" \"" + file.getName() + "\"");
        }
        System.out.println(dirs);
        StringBuilder dirsWithPath = new StringBuilder("Dirs-Paths:");
        for (DYRegisteredFile file :
                registeredFiles) {
            if (file.isDirectory())
                dirsWithPath.append(" \"" + file.getAbsolutePath() + "\"");
        }
        System.out.println(dirsWithPath);
        StringBuilder files = new StringBuilder("Files:");
        for (DYRegisteredFile file :
                registeredFiles) {
            if (file.isFile())
                files.append(" \"" + file.getName() + "\"");
        }
        System.out.println(files);
        StringBuilder filesWithPath = new StringBuilder("Files-Paths:");
        for (DYRegisteredFile file :
                registeredFiles) {
            if (file.isFile())
                filesWithPath.append(" \"" + file.getAbsolutePath() + "\"");
        }
        System.out.println(filesWithPath);

    }
}
