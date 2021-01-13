/*
 *  Copyright Osiris Team
 *  All rights reserved.
 *
 *  This software is licensed work.
 *  Please consult the file "LICENSE" for details.
 */

package com.osiris.dyml.examples;

import com.osiris.dyml.DYModule;
import com.osiris.dyml.DreamYaml;
import org.junit.jupiter.api.Test;

import java.util.List;

/**
 * Everything about getting values.
 */
public class GettingValuesExample {

    @Test
    void test() throws Exception {
        DreamYaml yaml = new DreamYaml(System.getProperty("user.dir")+"/src/test/getting-values-example.yml");
        yaml.load();

        DYModule firstName    = yaml.add("name")         .setDefValue("John").setComment("Everything about getting values.");
        DYModule lastName     = yaml.add("last-name")    .setDefValue("Goldman");
        DYModule age          = yaml.add("age")          .setDefValue("29");
        DYModule work         = yaml.add("work")         .setDefValue("Reporter");
        DYModule pendingTasks = yaml.add("pending-tasks").setDefValues("research", "1234", "start working");

        yaml.save();

        // Getting module details
        String key           = firstName.getKey(); // name // Returns the first key.
        String keyI          = firstName.getKeyByIndex(0); // name // Returns the key by given index. More on this in later examples.
        Object value         = firstName.getValue(); // John // Returns the 'real' value from the yaml file at the time when load() was called.
        Object valueI        = firstName.getValueByIndex(0); // John // Returns the value by given index.
        Object defaultValue  = firstName.getDefaultValue(); // John // Returns the default value
        Object defaultValueI = firstName.getDefaultValueByIndex(0); // John // Returns the default value
        String comment       = firstName.getComment(); // Everything about... // Returns the first comment.
        String commentI      = firstName.getCommentByIndex(0); // Everything about... // Returns the comment by given index.

        // All the methods below return the 'real' values at the time when load() was called.
        Object firstNameAsObject         = firstName.getValue();
        String firstNameAsString         = firstName.asString();
        int ageAsInt                     = age.asInt();
        List<String> pendingTasksObjects = pendingTasks.getValues();
        List<String> pendingTasksStrings = pendingTasks.asStringList();
        // You can also get each value from the list as an independent object
        String listIndex0                = pendingTasks.asString(0);
        int listIndex1                   = pendingTasks.asInt(1);
        char[] listIndex2                = pendingTasks.asCharArray(2);

        // Getting a module by its keys (not recommended)
        DYModule firstNameModuleByKeys = yaml.getModuleByKeys("name");

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
