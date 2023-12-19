package examples.yaml;

import com.osiris.dyml.Yaml;
import com.osiris.dyml.YamlSection;
import com.osiris.dyml.watcher.DirWatcher;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.StandardWatchEventKinds;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

class DirWatcherExample {

    @Test
    void test() throws Exception {

        // First we create two yaml files with some data
        Yaml yaml = new Yaml(System.getProperty("user.dir") + "/src/test/watcher-example.yml");
        yaml.load();
        YamlSection firstName1 = yaml.put("name").setDefValues("John");
        yaml.save(true);

        Thread.sleep(1000); // So that the above save doesn't trigger an event
        AtomicInteger counterYamlSaves = new AtomicInteger(0);
        yaml.addFileEventListener(event -> {
            try {
                if (event.getWatchEventKind().equals(StandardWatchEventKinds.ENTRY_MODIFY)) {
                    int count = counterYamlSaves.incrementAndGet();
                    System.out.println("Got event "+event.getWatchEventKind()+" "+count+" for "+yaml.file.getName()+" with id: "+event.id);
                    yaml.lockFile();
                    yaml.load();
                    System.out.println("Executed reload for event "+count);
                    yaml.unlockFile();
                    //System.out.println("Reloaded yaml file '" + event.parentDirectory.getName() +
                    //        "' because of '" + event.getWatchEventKind() + "' "+counterYamlSaves.get()+" event.");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        // Wait 1 sec to ensure the watcher thread has started
        Thread.sleep(1000);

        // That's it. Now we run some test to see if it works:
        firstName1.setValues("Pete"); // Imagine that this change is done by a person
        int expectedSaveEventsCount = 5;
        yaml.lockFile();
        for (int i = 0; i < expectedSaveEventsCount; i++) {
            System.out.println("User modifies yaml");
            yaml.save(); // In this moment the file gets modified;
            Thread.sleep(1000);
        }
        yaml.unlockFile();
        // ISSUE: When locks outside the loop only 2 modify events?
        // FIX (added in 9.8.2): This is because locks are also used in the event listener code above, thus the first event blocks
        // until all other save operations are finished. Since the directory listener is single threaded this makes
        // it miss the other events. To fix this DirWatcher now executes all event listeners asynchronously
        // and each Event gets an ID if strict order is still required.
        // ISSUE: When locks inside the loop, 9 modify events?
        // FIX (added in 9.8.2): Prevent receiving two separate ENTRY_MODIFY events: file modified
        // and timestamp updated. Instead, receive one ENTRY_MODIFY event
        // with two counts.
        // Thread.sleep(50);

        // This should trigger 5 events.
        // It could happen that it misses one or two of them, because the save methods are so close to each other (timely)

        for (int i = 0; i < 30; i++) {
            Thread.sleep(1000);
            if(counterYamlSaves.get() == expectedSaveEventsCount) break;
        }
        if(counterYamlSaves.get() != expectedSaveEventsCount)
            throw new Exception("Failed to receive "+expectedSaveEventsCount+" after 30 seconds!");
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