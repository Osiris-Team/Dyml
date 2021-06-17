package com.osiris.dyml.watcher;

import com.osiris.dyml.DreamYaml;

import java.io.File;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.util.List;

public class DYFileEvent {
    private final DYRegisteredFile file;
    private final WatchEvent parentWatchEvent;

    public DYFileEvent(DYRegisteredFile file, WatchEvent<?> parentWatchEvent) {
        this.file = file;
        this.parentWatchEvent = parentWatchEvent;
    }

    /**
     * Note that this will return null, if you did pass over null for 'yaml' at <br>
     * {@link DYWatcher#addFileAndListeners(File, List, boolean, DreamYaml)}.
     */
    public DreamYaml getYaml() {
        return file.getYaml();
    }

    /**
     * Returns the file that caused this event.
     */
    public DYRegisteredFile getFile() {
        return file;
    }

    /**
     * Returns the low-level {@link WatchEvent}.
     */
    public WatchEvent getParentWatchEvent() {
        return parentWatchEvent;
    }

    /**
     * Convenience method for returning the {@link WatchEvent#kind()}. <br>
     * For all event kinds see {@link StandardWatchEventKinds}.
     */
    public WatchEvent.Kind<?> getWatchEventKind() {
        return this.parentWatchEvent.kind();
    }

    /**
     * Convenience method for returning the {@link WatchEvent#context()}.
     */
    public Object getWatchEventContext() {
        return this.parentWatchEvent.context();
    }

    /**
     * Convenience method for returning the {@link WatchEvent#count()}.
     */
    public int getWatchEventCount() {
        return this.parentWatchEvent.count();
    }

}
