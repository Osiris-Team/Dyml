package com.osiris.dyml.examples;

import com.osiris.dyml.DYModule;
import com.osiris.dyml.DreamYaml;
import com.osiris.dyml.watcher.DYAction;
import com.osiris.dyml.watcher.DYWatcher;
import org.junit.jupiter.api.Test;

import java.nio.file.StandardWatchEventKinds;

class DYWatcherExample {

    @Test
    void test() throws Exception{

        // First we create two yaml files with some data
        DreamYaml yaml1 = new DreamYaml(System.getProperty("user.dir")+"/src/test/watcher-1-example.yml");
        yaml1.load();
        DYModule firstName1 = yaml1.add("name").setDefValue("John");
        yaml1.save();

        DreamYaml yaml2 = new DreamYaml(System.getProperty("user.dir")+"/src/test/watcher-2-example.yml");
        yaml2.load();
        DYModule firstName2 = yaml2.add("name").setDefValue("John");
        yaml2.save();


        // Create a watcher. Note that by default it will watch the complete, user directory in which this jar is located.
        DYWatcher watcher = new DYWatcher();  // You can specify a custom directory to watch by: DYWatcher watcher = new DYWatcher("C:your/custom/directory/path/here");
        watcher.start(); // Starts the watcher in its own new thread.

        // Add the files we want to watch
        watcher.addYaml(yaml1);
        watcher.addYaml(yaml2);

        // Create the action we want to perform when these files change
        DYAction action1 = new DYAction();
        action1.setRunnable(()->{
            // This action will reload every config watched by the watcher when its changed
            try {
                action1.getYaml().load();
                System.out.println("The "+action1.getYaml().getFile().getName()+" file was modified! Event kind: "+action1.getEventKind());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        // Besides, you can create an action for a specific yaml file, by simply adding that file to the actions constructor
        DYAction action2 = new DYAction(yaml2);
        action2.setRunnable(()->{
            // Displays a message when the file gets modified. For more events see StandardWatchEventKinds.
            if (action2.getEventKind().equals(StandardWatchEventKinds.ENTRY_MODIFY))
                System.out.println("This is a specific message for the file yaml2("+action2.getYaml().getFile().getName()+"), that it was modified!");
        });

        // Add the actions to the watcher
        watcher.addAction(action1);
        watcher.addAction(action2);

        // That's it. Now we run some test to see if it works:
        System.out.println("\nUser modifies yaml1:");
        firstName1.setValue("Pete"); // Imagine that this change is done by a person
        yaml1.save(); // In this moment the file gets modified

        System.out.println("\nUser modifies yaml2:");
        firstName2.setValue("Pete"); // Imagine that this change is done by a person
        yaml2.save(); // In this moment the file gets modified

        watcher.printDetails();
    }

}