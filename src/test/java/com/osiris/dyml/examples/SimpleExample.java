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

/**
 * Create a simple yaml file
 * with random person data.
 */
public class SimpleExample {

    @Test
    void test() throws Exception {
        DreamYaml yaml = new DreamYaml(System.getProperty("user.dir")+"/src/test/simple-example.yml"); // You can find every examples yaml file here https://github.com/Osiris-Team/Dream-Yaml/tree/main/src/test
        yaml.load(); // Creates a new file if it didn't exist already and loads it into memory by parsing it into modules

        // Make sure to call load() before adding any modules.
        DYModule firstName = yaml.add("name")         .setDefValue("John");
        DYModule lastName  = yaml.add("last-name")    .setDefValue("Goldman");
        DYModule age       = yaml.add("age")          .setDefValue("29");
        DYModule work      = yaml.add("work")         .setDefValue("Reporter");
        DYModule pending   = yaml.add("pending-tasks").setDefValues("do research", "buy food", "start working");

        yaml.save(); // Saves the default values to the file. Already existing modules won't be overwritten. Missing modules will be created.

        /*
name: John
last-name: Goldman
age: 29
work: Reporter
pending-tasks:
  - do research
  - buy food
  - start working
         */

    }
}
