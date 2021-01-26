package com.osiris.dyml.watcher;

import com.osiris.dyml.DreamYaml;

import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;

public class DYAction {
    private DreamYaml yaml;
    private Runnable runnable;
    private boolean affectAll = true;
    private WatchEvent.Kind<?> eventKind;

    /**
     * Creates an action which runs at any FileChangeEvent of a file in {@link DYWatcher#getDyList()}.
     * By default this action will affect all files in that list.
     * If you want to perform this action only for
     * a specific yaml file and not all, use {@link #DYAction(DreamYaml)} or {@link #setYaml(DreamYaml)}.
     * Do not use {@link #setAffectAll(boolean)} to true alone. You really have to pass a DreamYaml object.
     */
    public DYAction() {
        this(null);
    }

    /**
     * Creates an action which runs at the FileChangeEvent of a specific yaml file.
     */
    public DYAction(DreamYaml yaml) {
        this.yaml = yaml;
        if (yaml!=null) setAffectAll(false);
    }

    public void run(){
        this.runnable.run();
    }


    public DreamYaml getYaml() {
        return yaml;
    }

    public void setYaml(DreamYaml yaml) {
        this.yaml = yaml;
    }

    public Runnable getRunnable() {
        return runnable;
    }

    public void setRunnable(Runnable runnable) {
        this.runnable = runnable;
    }

    public boolean isAffectAll() {
        return affectAll;
    }

    public void setAffectAll(boolean affectAll) {
        this.affectAll = affectAll;
    }

    /**
     * For all event kinds see {@link StandardWatchEventKinds}.
     */
    public WatchEvent.Kind<?> getEventKind() {
        return eventKind;
    }

    public void setEventKind(WatchEvent.Kind<?> eventKind) {
        this.eventKind = eventKind;
    }
}
