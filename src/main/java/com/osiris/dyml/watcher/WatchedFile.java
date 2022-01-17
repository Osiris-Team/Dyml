/*
 *  Copyright Osiris Team
 *  All rights reserved.
 *
 *  This software is licensed work.
 *  Please consult the file "LICENSE" for details.
 */

package com.osiris.dyml.watcher;

import com.osiris.dyml.Yaml;

import java.io.File;
import java.net.URI;
import java.util.List;

public class WatchedFile extends File {
    private final FileWatcher watcher;

    public WatchedFile(String pathname, FileWatcher watcher) {
        super(pathname);
        this.watcher = watcher;
    }

    public WatchedFile(String parent, String child, FileWatcher watcher) {
        super(parent, child);
        this.watcher = watcher;
    }

    public WatchedFile(File parent, String child, FileWatcher watcher) {
        super(parent, child);
        this.watcher = watcher;
    }

    public WatchedFile(URI uri, FileWatcher watcher) {
        super(uri);
        this.watcher = watcher;
    }

    public Yaml getYaml() {
        return watcher.getYaml();
    }

    public FileWatcher getWatcher() {
        return watcher;
    }

    public List<FileEventListener<FileEvent>> getListeners() {
        return watcher.getListeners();
    }

    public void printDetails() {
        System.out.println("Details for '" + this + "':");
        System.out.println("Watcher: " + watcher);
        System.out.println("Yaml: " + watcher.getYaml());
        System.out.println("Listeners: " + watcher.getListeners().toString());
    }
}
