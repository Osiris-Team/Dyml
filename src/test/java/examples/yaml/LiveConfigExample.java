/*
 *  Copyright Osiris Team
 *  All rights reserved.
 *
 *  This software is licensed work.
 *  Please consult the file "LICENSE" for details.
 */

package examples.yaml;

import com.osiris.dyml.Yaml;
import com.osiris.dyml.YamlSection;
import com.osiris.dyml.watcher.DirWatcher;
import org.junit.jupiter.api.Test;

import java.nio.file.StandardWatchEventKinds;

/**
 * Quick access to config values across
 * all your code. Values are always up-to-date, which means
 * that if the user changes something in the yaml file
 * we get notified and update the in-memory values.
 */
public class LiveConfigExample {
    public static YamlSection FIRST_NAME;
    public static YamlSection LAST_NAME;
    public static YamlSection AGE;
    public static YamlSection PROFESSION;

    @Test
    void test() throws Exception {
        Yaml yaml = new Yaml(System.getProperty("user.dir") + "/src/test/live-config-example.yml");
        yaml.load();

        FIRST_NAME = yaml.put("name").setDefValues("John");
        LAST_NAME = yaml.put("last-name").setDefValues("Goldman");
        AGE = yaml.put("age").setDefValues("29");
        PROFESSION = yaml.put("work").setDefValues("Reporter");

        yaml.save(true);

        DirWatcher.get(yaml.file.getParentFile(), false)
                .watchFile(yaml.file, fileEvent -> {
                    if(fileEvent.getWatchEventKind() == StandardWatchEventKinds.ENTRY_DELETE)
                        return;

                    try{
                        yaml.load(); // Reloads the yaml file and updates the values
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });

        /*
name: John
last-name: Goldman
age: 29
work: Reporter
         */
    }
}
