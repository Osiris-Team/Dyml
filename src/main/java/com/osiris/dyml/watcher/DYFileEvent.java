package com.osiris.dyml.watcher;

import com.osiris.dyml.DreamYaml;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.util.List;

public class DYFileEvent {
    private final DYRegisteredFile parentDirectory;
    private final WatchEvent watchEvent;
    private final File file;
    private final Path path;

    public DYFileEvent(DYRegisteredFile parentDirectory, WatchEvent<?> watchEvent) {
        this.parentDirectory = parentDirectory;
        this.watchEvent = watchEvent;
        this.file = new File(parentDirectory+"/"+watchEvent.context());
        this.path = file.toPath();
    }

    /**
     * Note that this will return null, if you did pass over null for 'yaml' at <br>
     * {@link DYWatcher#addListeners(File, List, boolean, DreamYaml)}.
     */
    public DreamYaml getYaml() {
        return parentDirectory.getYaml();
    }

    /**
     * Returns the file that caused this event.
     */
    public DYRegisteredFile getParentDirectory() {
        return parentDirectory;
    }

    /**
     * Returns the low-level {@link WatchEvent}.
     */
    public WatchEvent getWatchEvent() {
        return watchEvent;
    }

    /**
     * Convenience method for returning the {@link WatchEvent#kind()}. <br>
     * For all event kinds see {@link StandardWatchEventKinds}.
     */
    public WatchEvent.Kind<?> getWatchEventKind() {
        return this.watchEvent.kind();
    }

    public File getFile() {
        return file;
    }

    public Path getPath() {
        return path;
    }

    /**
     * Convenience method for returning the {@link WatchEvent#context()}.
     */
    public Object getWatchEventContext() {
        return this.watchEvent.context();
    }

    /**
     * Convenience method for returning the {@link WatchEvent#count()}.
     */
    public int getWatchEventCount() {
        return this.watchEvent.count();
    }

}
