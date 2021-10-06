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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

import static java.nio.file.StandardWatchEventKinds.*;


/**
 * A {@link DYWatcher} is able to watch multiple files/directories <br>
 * (as well as subdirectories) and notify <br>
 * their listeners if an event happens.
 */
@SuppressWarnings("ALL")
public class DYWatcher extends Thread implements AutoCloseable {
    public static final List<DYWatcher> activeWatchers = new CopyOnWriteArrayList<>();
    /**
     * A list containing files, that notify their listeners when an event happens. <br>
     * Note that this list also contains the files parent directories.
     */
    private DYRegisteredFile registeredFile;
    private WatchService watchService;
    private boolean isWatchSubDirs = false;
    private WatchKey watchKey;
    private DreamYaml yaml;
    private List<DYFileEventListener<DYFileEvent>> listeners;
    private List<DYWatcher> subDirectoriesWatchers = new ArrayList<>();

    private DYWatcher(DreamYaml yaml, boolean isWatchSubDirs) throws IOException {
        this.yaml = yaml;
        init(yaml.getFile().toPath(), isWatchSubDirs);
    }

    /**
     * <p style="color:red">Its recommended to use the static method {@link DYWatcher#getForPath(Path)} to get a {@link DYWatcher} instead!</p>
     * <p style="color:red">The reason for that is performance.</p>
     * Initialises a new {@link DYWatcher} and watches the provided path. <br>
     * Note that this method will also call {@link #start()} to start its thread. <br>
     * You can register more files to it via {@link #addListeners(File, List)}.
     *
     * @param path Can be a file or a directory.
     */
    private DYWatcher(Path path, boolean watchSubdirectories) throws IOException {
        init(path, watchSubdirectories);
    }

    private void init(Path path, boolean watchSubdirectories) throws IOException {
        Objects.requireNonNull(path);
        this.watchService = FileSystems.getDefault().newWatchService();
        this.isWatchSubDirs = isWatchSubDirs();
        if (this.yaml==null) this.yaml = isYamlFile(path);
        this.registeredFile = new DYRegisteredFile(path.toString(), this);
        if (!path.toFile().isDirectory())
            watchFile(path.getParent(), watchSubdirectories);
        else
            watchFile(path, watchSubdirectories);
        start();
    }

    private DreamYaml isYamlFile(Path path) {
        if (path.toString().endsWith(".yml")||path.toString().endsWith(".yaml"))
            return new DreamYaml(path.toFile());
        return null;
    }

    /**
     * See {@link #getForPath(Path)} for details.
     */
    public static synchronized DYWatcher getForFile(File file, boolean isWatchSubDirs) throws IOException {
        return getForPath(file.toPath(), isWatchSubDirs);
    }

    /**
     * Checks if there is an existing {@link DYWatcher} for <br>
     * the provided path and returns it. If the path is a file, it checks for the parent directory. <br>
     * Otherwise creates a new {@link DYWatcher} for the provided path and returns it. <br>
     */
    public static synchronized DYWatcher getForPath(Path path, boolean isWatchSubDirs) throws IOException {
        Path dirPath = path;
        if (!path.toFile().isDirectory()) dirPath = path.getParent();
        for (DYWatcher watcher :
                activeWatchers) {
            if (watcher.getRegisteredFile().toPath().equals(path) && watcher.isAlive())
                return watcher;
        }
        return new DYWatcher(path, isWatchSubDirs);
    }

    @Override
    public void run() {
        super.run();
        try {
            WatchKey key;
            while ((key = watchService.take()) != null) {
                watchKey = key;
                for (WatchEvent<?> event :
                        key.pollEvents()) {
                    if (this.listeners!=null && !((Path) event.context()).toFile().isDirectory()) // Sub-directories have their own watchers
                        for (DYFileEventListener<DYFileEvent> listener :
                                listeners) {
                            listener.runOnEvent(new DYFileEvent(registeredFile, event));
                        }
                }
                key.reset();
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("DYWatcher crashed! Please restart it to keep using its functions!");
        }
    }

    @Override
    public void close() throws Exception {
        if (watchKey!=null) watchKey.cancel();
        watchService.close();
        activeWatchers.remove(this);
    }

    private void watchFile(Path path, boolean watchSubdirectories) throws IOException {
        if (!path.toFile().exists())
            throw new IOException("File '"+path.getFileName()+"' does not exist! Full path: "+path);

        WatchKey watchKey = path.register(watchService, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY, OVERFLOW);
        if (watchSubdirectories) { // Add subdirectories if enabled
            Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
                int pos = 0;
                @Override
                public FileVisitResult preVisitDirectory(Path path, BasicFileAttributes attrs)
                        throws IOException {
                    if (pos!=0) // First directory visit is always the current directory that is already beeing watched
                    {
                        subDirectoriesWatchers.add(DYWatcher.getForPath(path, true)
                                .addListeners(listeners)); // Remember to add the listeners
                    }

                    pos++;
                    return FileVisitResult.CONTINUE;
                }
            });
        }
    }

    public void setListeners(DYFileEventListener<DYFileEvent>... listeners) throws IOException {
        Objects.requireNonNull(listeners);
        setListeners(Arrays.asList(listeners));
    }

    /**
     * See {@link #addListeners(File, List, boolean, DreamYaml)} for details. <br>
     */
    public DYWatcher setListeners(List<DYFileEventListener<DYFileEvent>> listeners) throws IOException {
        this.listeners = listeners;
        for (DYWatcher watcher :
                subDirectoriesWatchers) {
            watcher.setListeners(listeners);
        }
        return this;
    }

    public DYWatcher addListeners(DYFileEventListener<DYFileEvent>... listeners) throws IOException {
        if (listeners!=null) addListeners(Arrays.asList(listeners));
        return this;
    }

    public DYWatcher addListeners(List<DYFileEventListener<DYFileEvent>> listeners) throws IOException {
        if (this.listeners==null) this.listeners = new ArrayList<>();
        if (listeners!=null) this.listeners.addAll(listeners);
        for (DYWatcher watcher :
                subDirectoriesWatchers) {
            watcher.addListeners(listeners);
        }
        return this;
    }

    public DYWatcher removeListeners(DYFileEventListener<DYFileEvent>... listeners) throws Exception {
        Objects.requireNonNull(listeners);
        removeListeners(Arrays.asList(listeners));
        return this;
    }

    public DYWatcher removeListeners(List<DYFileEventListener<DYFileEvent>> listeners) throws Exception {
        this.listeners.removeAll(listeners);
        if (this.listeners.isEmpty())
            close();
        for (DYWatcher watcher :
                subDirectoriesWatchers) {
            watcher.removeListeners(listeners);
        }
        return this;
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
        if (registeredFile.isDirectory())
            System.out.println("Registered dir: "+registeredFile);
        else
            System.out.println("Registered file: "+registeredFile);
    }

    public DYRegisteredFile getRegisteredFile() {
        return registeredFile;
    }

    public WatchKey getWatchKey() {
        return watchKey;
    }

    public DreamYaml getYaml() {
        return yaml;
    }

    public List<DYFileEventListener<DYFileEvent>> getListeners() {
        return listeners;
    }
}
