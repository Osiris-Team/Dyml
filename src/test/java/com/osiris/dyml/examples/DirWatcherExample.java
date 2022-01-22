package com.osiris.dyml.examples;

import com.osiris.dyml.Yaml;
import com.osiris.dyml.YamlSection;
import com.osiris.dyml.watcher.DirWatcher;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.StandardWatchEventKinds;
import java.util.concurrent.atomic.AtomicBoolean;

class DirWatcherExample {

    @Test
    void test() throws Exception {

        // Dev note, do not include in your code
        System.out.println("Note that this test won't print the wanted results, because Junit doesn't allow correct multithreading!");

        // First we create two yaml files with some data
        Yaml yaml = new Yaml(System.getProperty("user.dir") + "/src/test/watcher-example.yml");
        yaml.load();
        YamlSection firstName1 = yaml.put("name").setDefValues("John");
        yaml.save(true);

        Thread.sleep(1000); // So that the above save doesn't trigger an event
        yaml.addFileEventListener(event -> {
            try {
                if (event.getWatchEventKind().equals(StandardWatchEventKinds.ENTRY_MODIFY)) {
                    yaml.lockFile();
                    yaml.load();
                    yaml.unlockFile();
                    System.out.println("Reloaded yaml file '" + event.parentDirectory.getName() +
                            "' because of '" + event.getWatchEventKind() + "' event.");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        // Wait 1 sec to ensure the watcher thread has started
        Thread.sleep(1000);

        // That's it. Now we run some test to see if it works:
        System.out.println("User modifies yaml");
        firstName1.setValues("Pete"); // Imagine that this change is done by a person
        yaml.save(); // In this moment the file gets modified;
        yaml.save(); // In this moment the file gets modified
        yaml.save(); // In this moment the file gets modified
        yaml.save(); // In this moment the file gets modified
        yaml.save(); // In this moment the file gets modified
        // This should trigger 5 events.
        // It could happen that it misses one or two of them, because the save methods are so close to each other (timely)

        Thread.sleep(1000); // Wait 1 sec for watcher to receive event

        // Example for other files:
        AtomicBoolean changed = new AtomicBoolean(false);
        File example2 = new File(System.getProperty("user.dir") + "/src/test/watcher-example2.txt");
        if (!example2.exists()) example2.createNewFile();
        DirWatcher.get(example2, false).addListeners(fileChangeEvent -> {
            System.out.println("Event'" + fileChangeEvent.parentDirectory.getName() +
                    "' because of '" + fileChangeEvent.getWatchEventKind() + "' event.");
            changed.set(true);
        });
        example2.delete();
        while (!changed.get())
            Thread.sleep(100);
    }

}