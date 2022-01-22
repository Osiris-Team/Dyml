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
import java.util.function.Consumer;

public class WatchedDir extends File {
    public final DirWatcher watcher;
    public Yaml yaml = null;

    public WatchedDir(String pathname, DirWatcher watcher) {
        super(pathname);
        this.watcher = watcher;
    }

    public WatchedDir(String parent, String child, DirWatcher watcher) {
        super(parent, child);
        this.watcher = watcher;
    }

    public WatchedDir(File parent, String child, DirWatcher watcher) {
        super(parent, child);
        this.watcher = watcher;
    }

    public WatchedDir(URI uri, DirWatcher watcher) {
        super(uri);
        this.watcher = watcher;
    }

    public List<Consumer<FileEvent>> getListeners() {
        return watcher.getListeners();
    }

    public void printDetails() {
        System.out.println("Details for '" + this + "':");
        System.out.println("Watcher: " + watcher);
        System.out.println("Listeners: " + watcher.getListeners().toString());
    }
}
