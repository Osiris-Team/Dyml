package com.osiris.dyml.watcher;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;

public class FileEvent {
    public final WatchedDir parentDirectory;
    public final WatchEvent watchEvent;
    public final File file;
    public final Path path;

    public FileEvent(WatchedDir parentDirectory, WatchEvent<?> watchEvent) {
        this.parentDirectory = parentDirectory;
        this.watchEvent = watchEvent;
        this.file = new File(parentDirectory + "/" + watchEvent.context());
        this.path = file.toPath();
    }

    /**
     * Convenience method for returning the {@link WatchEvent#kind()}. <br>
     * For all event kinds see {@link StandardWatchEventKinds}.
     */
    public WatchEvent.Kind<?> getWatchEventKind() {
        return this.watchEvent.kind();
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
