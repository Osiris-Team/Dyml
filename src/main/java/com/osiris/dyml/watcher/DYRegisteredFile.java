/*
 *  Copyright Osiris Team
 *  All rights reserved.
 *
 *  This software is licensed work.
 *  Please consult the file "LICENSE" for details.
 */

package com.osiris.dyml.watcher;

import com.osiris.dyml.DreamYaml;

import java.io.File;
import java.net.URI;
import java.nio.file.WatchKey;
import java.util.List;

public class DYRegisteredFile extends File {
    private DYWatcher watcher;
    private WatchKey parentWatchKey;
    private DreamYaml yaml;
    private List<DYFileEventListener<DYFileEvent>> listeners;

    public DYRegisteredFile(String pathname, DYWatcher watcher, WatchKey parentWatchKey, List<DYFileEventListener<DYFileEvent>> listeners) {
        super(pathname);
        init(watcher, parentWatchKey, listeners);
    }

    public DYRegisteredFile(String parent, String child, DYWatcher watcher, WatchKey parentWatchKey, List<DYFileEventListener<DYFileEvent>> listeners) {
        super(parent, child);
        init(watcher, parentWatchKey, listeners);
    }

    public DYRegisteredFile(File parent, String child, DYWatcher watcher, WatchKey parentWatchKey, List<DYFileEventListener<DYFileEvent>> listeners) {
        super(parent, child);
        init(watcher, parentWatchKey, listeners);
    }

    public DYRegisteredFile(URI uri, DYWatcher watcher, WatchKey parentWatchKey, List<DYFileEventListener<DYFileEvent>> listeners) {
        super(uri);
        init(watcher, parentWatchKey, listeners);
    }

    private void init(DYWatcher watcher, WatchKey watchKey, List<DYFileEventListener<DYFileEvent>> listeners) {
        this.watcher = watcher;
        this.parentWatchKey = watchKey;
        this.listeners = listeners;
    }

    public DreamYaml getYaml() {
        return yaml;
    }

    public void setYaml(DreamYaml yaml) {
        this.yaml = yaml;
    }

    public DYWatcher getWatcher() {
        return watcher;
    }

    public WatchKey getParentWatchKey() {
        return parentWatchKey;
    }

    public List<DYFileEventListener<DYFileEvent>> getListeners() {
        return listeners;
    }

    public void printDetails() {
        System.out.println("Details for '" + this + "':");
        System.out.println("Watcher: " + watcher);
        System.out.println("Yaml: " + yaml);
        System.out.println("Listeners: " + listeners.toString());
    }
}
