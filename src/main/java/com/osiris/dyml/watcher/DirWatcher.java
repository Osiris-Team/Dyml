/*
 * Copyright Osiris Team
 * All rights reserved.
 *
 * This software is copyrighted work licensed under the terms of the
 * AutoPlug License.  Please consult the file "LICENSE" for details.
 */

package com.osiris.dyml.watcher;

import com.osiris.dyml.Yaml;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

import static java.nio.file.StandardWatchEventKinds.*;


/**
 * A {@link DirWatcher} is able to watch the provided directory <br>
 * (as well as its subdirectories) and notify <br>
 * their listeners if an event happens.
 */
@SuppressWarnings("ALL")
public class DirWatcher extends Thread implements AutoCloseable {
    private static final List<DirWatcher> activeWatchers = new ArrayList<>();

    private WatchedDir registeredFile;
    private WatchService watchService;
    private boolean isWatchSubDirs = false;
    private WatchKey watchKey;
    private List<Consumer<FileEvent>> listeners;
    private List<DirWatcher> subDirectoriesWatchers = new ArrayList<>();

    /**
     * <p style="color:red">Its recommended to use the static method {@link DirWatcher#get(File, boolean)} to get a instance of this class instead!</p>
     * <p style="color:red">The reason for that is performance.</p>
     * Initialises a new {@link FileWatcher} and watches the provided path. <br>
     * Note that this method will also call {@link #start()} to start its thread. <br>
     * You can register more files to it via {@link #addListeners(File, List)}.
     *
     * @param dirPath Can be a file or a directory.
     */
    private DirWatcher(Path dirPath, boolean watchSubdirectories) throws IOException {
        init(dirPath, watchSubdirectories);
    }

    /**
     * See {@link #watchDirectory(Path, boolean)} for details.
     */
    public static synchronized DirWatcher get(File file, boolean isWatchSubDirs) throws IOException {
        return get(file.toPath(), isWatchSubDirs);
    }

    /**
     * Checks if there is an existing {@link DirWatcher} for <br>
     * the provided path and returns it. If the path is a file, it checks for the files' parent directory. <br>
     * Otherwise creates a new {@link DirWatcher} for the provided path or its parent directory if its a file, and returns it. <br>
     */
    public static synchronized DirWatcher get(Path path, boolean isWatchSubDirs) throws IOException {
        Path dirPath = path;
        if (!path.toFile().isDirectory()) dirPath = path.getParent();
        for (DirWatcher watcher :
                activeWatchers) {
            if (watcher.getRegisteredFile().toPath().equals(path) && watcher.isAlive())
                return watcher;
        }
        return new DirWatcher(path, isWatchSubDirs);
    }

    private void init(Path path, boolean watchSubdirectories) throws IOException {
        Objects.requireNonNull(path);
        this.watchService = FileSystems.getDefault().newWatchService();
        this.isWatchSubDirs = isWatchSubDirs();
        this.registeredFile = new WatchedDir(path.toString(), this);
        if (!path.toFile().isDirectory())
            watchDir(path.getParent(), watchSubdirectories);
        else
            watchDir(path, watchSubdirectories);
        start();
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
                    if (this.listeners != null && !((Path) event.context()).toFile().isDirectory()) // Sub-directories have their own watchers
                        for (Consumer<FileEvent> listener :
                                listeners) {
                            listener.accept(new FileEvent(registeredFile, event));
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
        if (watchKey != null) watchKey.cancel();
        watchService.close();
        activeWatchers.remove(this);
    }

    private void watchDir(Path path, boolean watchSubdirectories) throws IOException {
        if (!path.toFile().exists())
            throw new IOException("File '" + path.getFileName() + "' does not exist! Full path: " + path);

        WatchKey watchKey = path.register(watchService, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY, OVERFLOW);
        if (watchSubdirectories) { // Add subdirectories if enabled
            Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
                int pos = 0;

                @Override
                public FileVisitResult preVisitDirectory(Path path, BasicFileAttributes attrs)
                        throws IOException {
                    if (pos != 0) // First directory visit is always the current directory that is already beeing watched
                    {
                        subDirectoriesWatchers.add(DirWatcher.get(path, true)
                                .addListeners(listeners)); // Remember to add the listeners
                    }

                    pos++;
                    return FileVisitResult.CONTINUE;
                }
            });
        }
    }

    /**
     * Watches a single file
     *
     * @param file
     * @param listener
     * @return
     */
    public DirWatcher watchFile(File file, Consumer<FileEvent> listener) throws IOException {
        Consumer<FileEvent> actualListener = new Consumer<FileEvent>() {
            @Override
            public void accept(FileEvent event) {
                if (event.path.equals(file.toPath())) // To make sure that only events are thrown for the actual yaml file
                    listener.accept(event);
            }
        };
        this.addListeners(actualListener);
        return this;
    }

    public DirWatcher addListeners(Consumer<FileEvent>... listeners) throws IOException {
        addListeners(Arrays.asList(listeners));
        return this;
    }

    public DirWatcher addListeners(List<Consumer<FileEvent>> listeners) throws IOException {
        if (this.listeners == null) this.listeners = new ArrayList<>();
        if (listeners != null) this.listeners.addAll(listeners);
        for (DirWatcher subWatcher :
                subDirectoriesWatchers) {
            subWatcher.addListeners(listeners);
        }
        return this;
    }

    public DirWatcher removeListeners(Consumer<FileEvent>... listeners) throws Exception {
        Objects.requireNonNull(listeners);
        removeListeners(Arrays.asList(listeners));
        return this;
    }

    public DirWatcher removeListeners(List<Consumer<FileEvent>> listeners) throws Exception {
        this.listeners.removeAll(listeners);
        for (DirWatcher subWatcher :
                subDirectoriesWatchers) {
            subWatcher.removeListeners(listeners);
        }
        return this;
    }

    public DirWatcher removeAllListeners(boolean alsoRemoveSubDirListeners) {
        this.listeners.clear();
        if (alsoRemoveSubDirListeners)
            for (DirWatcher subWatcher :
                    subDirectoriesWatchers) {
                subWatcher.removeAllListeners(true);
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
            System.out.println("Registered dir: " + registeredFile);
        else
            System.out.println("Registered file: " + registeredFile);
    }

    public WatchedDir getRegisteredFile() {
        return registeredFile;
    }

    public WatchKey getWatchKey() {
        return watchKey;
    }

    public List<Consumer<FileEvent>> getListeners() {
        return listeners;
    }

    public void setListeners(Consumer<FileEvent>... listeners) throws IOException {
        Objects.requireNonNull(listeners);
        setListeners(Arrays.asList(listeners));
    }

    /**
     * See {@link #addListeners(File, List, boolean, Yaml)} for details. <br>
     */
    public DirWatcher setListeners(List<Consumer<FileEvent>> listeners) throws IOException {
        this.listeners = listeners;
        for (DirWatcher watcher :
                subDirectoriesWatchers) {
            watcher.setListeners(listeners);
        }
        return this;
    }
}
