/*
 *  Copyright Osiris Team
 *  All rights reserved.
 *
 *  This software is licensed work.
 *  Please consult the file "LICENSE" for details.
 */

package com.osiris.dyml.examples;

import com.osiris.dyml.Yaml;
import com.osiris.dyml.YamlSection;
import com.osiris.dyml.YamlValue;
import org.junit.jupiter.api.Test;

import java.util.List;

/**
 * Everything about getting values.
 */
public class GettingValuesExample {

    @Test
    void test() throws Exception {
        Yaml yaml = new Yaml(System.getProperty("user.dir") + "/src/test/getting-values-example.yml",
                true);
        yaml.load();
        yaml.reset(); // DO NOT CALL THIS IN PRODUCTION, IT WILL REMOVE ALL THE INFORMATION FROM YOUR YAML FILE!
        // I am doing this only for the sake of testing!

        YamlSection firstName = yaml.put("name").setDefValues("John").setComments("Everything about getting values.");
        YamlSection lastName = yaml.put("last-name").setDefValues("Goldman");
        YamlSection age = yaml.put("age").setDefValues("29");
        YamlSection work = yaml.put("work").setDefValues("Reporter");
        YamlSection pendingTasks = yaml.put("pending-tasks").setDefValues("research", "1234", "start working");

        yaml.saveAndLoad(); // Since the file got reset, we need to reload it after saving it

        // Getting module details
        String key = firstName.getFirstKey(); // name // Returns the first key.
        String keyI = firstName.getKeyByIndex(0); // name // Returns the key by given index. More on this in later examples.
        Object value = firstName.getValue(); // John // Returns the 'real' value from the yaml file at the time when load() was called.
        Object valueI = firstName.getValueByIndex(0); // John // Returns the value by given index.
        Object defaultValue = firstName.getDefValue(); // John // Returns the default value
        Object defaultValueI = firstName.getDefValueByIndex(0); // John // Returns the default value
        String comment = firstName.getComment(); // Everything about... // Returns the first comment.
        String commentI = firstName.getCommentByIndex(0); // Everything about... // Returns the comment by given index.

        // All the methods below return the 'real' values at the time when load() was called.
        YamlValue firstNameValue = firstName.getValue(); // This is never null, and acts as a container for the actual string value
        String firstNameAsString = firstName.asString(); // Can be null if there is no actual string value
        int ageAsInt = age.asInt();
        List<YamlValue> pendingTasksValues = pendingTasks.getValues();
        List<String> pendingTasksStrings = pendingTasks.asStringList();
        // You can also get each value from the list as an independent object
        String listIndex0 = pendingTasks.asString(0);
        int listIndex1 = pendingTasks.asInt(1);
        char[] listIndex2 = pendingTasks.asCharArray(2);

        // Finding and getting a module by its keys
        YamlSection firstNameModuleByKeys = yaml.get("name"); // Returns the module from the permanent added modules list
        YamlSection firstNameLoadedModuleByKeys = yaml.get("name"); // Returns the module from the temporary loaded modules list, at the time load() was called

        /*
# Everything about getting values.
name: John
last-name: Goldman
age: 29
work: Reporter
pending-tasks:
  - research
  - 1234
  - start working
         */

    }
}
