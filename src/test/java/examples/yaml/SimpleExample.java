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
import org.junit.jupiter.api.Test;

/**
 * Create a simple configuration file
 * with random person data.
 */
public class SimpleExample {

    @Test
    void test() throws Exception {
        Yaml yaml = new Yaml(System.getProperty("user.dir") + "/src/test/simple-example.yml"); // You can find every examples yaml file here https://github.com/Osiris-Team/Dream-Yaml/tree/main/src/test
        yaml.load();

        // Your file must have been loaded at least once before adding any modules.
        YamlSection firstName = yaml.put("name").setDefValues("John");
        YamlSection lastName = yaml.put("last-name").setDefValues("Goldman");
        YamlSection age = yaml.put("age").setDefValues("29");
        YamlSection work = yaml.put("work").setDefValues("Reporter");
        YamlSection pending = yaml.put("pending-tasks").setDefValues("do research", "buy food", "start working");

        yaml.save(); // Saves the default values to the file. Already existing modules won't be overwritten. Missing modules will be created.

        // name: John
        // last-name: Goldman
        // age: 29
        // work: Reporter
        // pending-tasks:
        //   - do research
        //   - buy food
        //   - start working
    }
}
