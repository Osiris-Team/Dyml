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
 * Create a simple configuration file
 * with random person data.
 */
public class SimpleExample {

    @Test
    void test() throws Exception {
        DreamYaml yaml = new DreamYaml(System.getProperty("user.dir") + "/src/test/simple-example.yml"); // You can find every examples yaml file here https://github.com/Osiris-Team/Dream-Yaml/tree/main/src/test
       yaml.load();

        // Your file must have been loaded at least once before adding any modules.
        DYModule firstName = yaml.put("name")         .setDefValues("John");
        DYModule lastName  = yaml.put("last-name")    .setDefValues("Goldman");
        DYModule age       = yaml.put("age")          .setDefValues("29");
        DYModule work      = yaml.put("work")         .setDefValues("Reporter");
        DYModule pending   = yaml.put("pending-tasks").setDefValues("do research", "buy food", "start working");

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
