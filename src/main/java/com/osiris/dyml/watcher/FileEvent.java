package com.osiris.dyml.watcher;

import com.osiris.dyml.Yaml;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.util.List;

public class FileEvent {
    private final WatchedFile parentDirectory;
    private final WatchEvent watchEvent;
    private final File file;
    private final Path path;

    public FileEvent(WatchedFile parentDirectory, WatchEvent<?> watchEvent) {
        this.parentDirectory = parentDirectory;
        this.watchEvent = watchEvent;
        this.file = new File(parentDirectory + "/" + watchEvent.context());
        this.path = file.toPath();
    }

    /**
     * Note that this will return null, if you did pass over null for 'yaml' at <br>
     * {@link FileWatcher#addListeners(File, List, boolean, Yaml)}.
     */
    public Yaml getYaml() {
        return parentDirectory.getYaml();
    }

    /**
     * Returns the file that caused this event.
     */
    public WatchedFile getParentDirectory() {
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
