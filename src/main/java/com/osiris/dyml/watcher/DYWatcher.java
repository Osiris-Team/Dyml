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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import static java.nio.file.StandardWatchEventKinds.*;


/**
 * Detects changes to the given yaml files and performs actions
 * on their corresponding in-memory representations ({@link DreamYaml}).
 */
public class DYWatcher extends Thread{
    private List<DreamYaml> dyList = new CopyOnWriteArrayList<>();
    private List<DYAction> actions = new CopyOnWriteArrayList<>();
    private File dir;
    private boolean registerSubDirs = true;
    private int subDirCount;
    private WatchService watchService;
    private Map<WatchKey,Path> keys;
    private boolean trace = false;

    /**
     * {@link #DYWatcher(List, String, boolean)}
     */
    public DYWatcher() {
        this("");
    }

    /**
     * {@link #DYWatcher(List, String, boolean)}
     */
    public DYWatcher(boolean registerSubDirs) {
        this(null, null, registerSubDirs);
    }

    /**
     * {@link #DYWatcher(List, String, boolean)}
     */
    public DYWatcher(String dir, boolean registerSubDirs) {
        this(null, dir, registerSubDirs);
    }

    /**
     * {@link #DYWatcher(List, String, boolean)}
     */
    public DYWatcher(List<DreamYaml> dyList) {
        this(dyList, null, true);
    }

    /**
     * {@link #DYWatcher(List, String, boolean)}
     */
    public DYWatcher(DreamYaml... dy) throws Exception {
        if (dy == null)
            throw new Exception("Yaml files list cannot be null!");
        init(Arrays.asList(dy), null, true);
    }


    /**
     * {@link #DYWatcher(List, String, boolean)}
     */
    public DYWatcher(String dir) {
        init(null, dir, true);
    }

    /**
     * Create a new DreamYamlWatcher/YamlFilesWatcher within a new thread.
     * @param dyList a list containing the yaml files to be watched
     * @param dir the directory path where to listen for changes. If null/empty the user-dir will be used.
     */
    public DYWatcher(List<DreamYaml> dyList, String dir, boolean registerSubDirs) {
        init(dyList, dir, registerSubDirs);
    }

    private void init(List<DreamYaml> dyList, String dir, boolean registerSubDirs) {
        if (dir==null) dir = System.getProperty("user.dir");
        if (dir!=null && dir.isEmpty()) dir = System.getProperty("user.dir");
        this.dir = new File(dir);
        if (dyList!=null && !dyList.isEmpty()) this.dyList.addAll(dyList);
        this.registerSubDirs = registerSubDirs;
        this.keys = new HashMap<>();
    }

    public void printDetails(){
        System.out.println("Watcher: "+this);
        System.out.println("Dir: "+dir.getAbsolutePath());
        System.out.println("SubDirs: "+ registerSubDirs +" ("+subDirCount+") ");
        System.out.println("Watching: ("+dyList.size()+") "+dyList.toString());
        System.out.println("Actions: ("+actions.size()+") "+actions.toString());
    }

    @Override
    public void run() {
        super.run();
        try{
            if (dir == null)
                throw new Exception("Dir cannot be null!");
            if (dir.getPath().isEmpty())
                throw new Exception("Dir cannot be empty!");

            watchService
                    = FileSystems.getDefault().newWatchService();

            if (registerSubDirs)
                registerAll(dir.toPath());
            else
                register(dir.toPath());

            WatchKey key;
            while ((key = watchService.take()) != null) {
                for (WatchEvent<?> event : key.pollEvents()) {

                    final String file_name = event.context().toString();
                    for (DreamYaml yaml :
                            dyList) {
                        // Check if the event is for one of our given yaml files
                        if (yaml.getFile().getName().equals(file_name)){
                            // Perform the actions according to their settings
                            for (DYAction action :
                                    actions) {
                                action.setEventKind(event.kind());
                                if (action.isAffectAll()){
                                    action.setYaml(yaml);
                                    action.run();
                                }
                                else if (action.getYaml().getFile().getName().equals(file_name))
                                    action.run();
                                // Otherwise do nothing
                            }
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
     * Register the given directory, and all its sub-directories, with the
     * WatchService.
     */
    private void registerAll(final Path start) throws IOException {
        // register directory and sub-directories
        Files.walkFileTree(start, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs)
                    throws IOException
            {
                register(dir);
                return FileVisitResult.CONTINUE;
            }
        });
    }

    /**
     * Register the given directory with the WatchService
     */
    private void register(Path dir) throws IOException {
        WatchKey key = dir.register(watchService, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
        if (trace) {
            Path prev = keys.get(key);
            if (prev == null) {
                //System.out.format("register: %s\n", dir);
                subDirCount++;
            } else {
                if (!dir.equals(prev)) {
                    //System.out.format("update: %s -> %s\n", prev, dir);
                }
            }
        }
        keys.put(key, dir);
    }

    /**
     * Lazy method for adding yaml objects.
     */
    public void addYaml(DreamYaml yaml){
        this.dyList.add(yaml);
    }

    /**
     * Lazy method for adding actions.
     */
    public void addAction(DYAction action){
        this.actions.add(action);
    }

    public List<DreamYaml> getDyList() {
        return dyList;
    }

    public void setDyList(List<DreamYaml> dyList) {
        this.dyList = dyList;
    }

    /**
     * Returns a list containing all actions ({@link DYAction}),
     * that run when a FileChangeEvent happens for the given
     * yaml files({@link #getDyList()}).
     */
    public List<DYAction> getActions() {
        return actions;
    }

    public void setActions(List<DYAction> actions) {
        this.actions = actions;
    }

    public boolean isRegisterSubDirs() {
        return registerSubDirs;
    }

    /**
     * Watch all sub-directories or not. Default is true.
     * Note: This must be called before start() was called to take effect.
     */
    public void setRegisterSubDirs(boolean registerSubDirs) {
        this.registerSubDirs = registerSubDirs;
    }
}
