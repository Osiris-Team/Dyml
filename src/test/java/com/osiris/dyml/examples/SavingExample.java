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
 * How to save your yaml file the right way.
 */
public class SavingExample {

    @Test
    void test() throws Exception {
        DreamYaml yaml = new DreamYaml(System.getProperty("user.dir") + "/src/test/saving-example.yml");
        //yaml.load(); // Not needed because of autoLoad
        yaml.reset(); // Ignore this!
        yaml.add("work").setDefValues("Reporter"); // Ignore this!

        // SCENARIO 1:
        // Lets imagine this file contains tons of information but we only want to modify/update that one section and keep the rest.
        // For that we simply add that section into memory and edit it: 
        yaml.get("work").setValues("Developer");
        // And save the file:
        yaml.save(); // Note that stuff that isn't supported by DreamYaml wont be parsed and thus removed from the file after you save it!
        // Just as simple as that!

        // SCENARIO 2:
        // Lets imagine another scenario where this file contains a lot of unnecessary stuff we want to get rid of
        // and add other data instead.
        // For that we (again) add the modules first:
        DYModule firstName = yaml.add("name").setDefValues("John");
        DYModule lastName = yaml.add("last-name").setDefValues("Goldman");
        DYModule age = yaml.add("age").setDefValues("29");
        // Then save it with 'overwrite' true:
        yaml.save(true);
        // That's it!
      
    }
}
