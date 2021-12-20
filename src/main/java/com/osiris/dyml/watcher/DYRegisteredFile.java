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
import java.util.List;

public class DYRegisteredFile extends File {
    private final DYWatcher watcher;

    public DYRegisteredFile(String pathname, DYWatcher watcher) {
        super(pathname);
        this.watcher = watcher;
    }

    public DYRegisteredFile(String parent, String child, DYWatcher watcher) {
        super(parent, child);
        this.watcher = watcher;
    }

    public DYRegisteredFile(File parent, String child, DYWatcher watcher) {
        super(parent, child);
        this.watcher = watcher;
    }

    public DYRegisteredFile(URI uri, DYWatcher watcher) {
        super(uri);
        this.watcher = watcher;
    }

    public DreamYaml getYaml() {
        return watcher.getYaml();
    }

    public DYWatcher getWatcher() {
        return watcher;
    }

    public List<DYFileEventListener<DYFileEvent>> getListeners() {
        return watcher.getListeners();
    }

    public void printDetails() {
        System.out.println("Details for '" + this + "':");
        System.out.println("Watcher: " + watcher);
        System.out.println("Yaml: " + watcher.getYaml());
        System.out.println("Listeners: " + watcher.getListeners().toString());
    }
}
