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
 * Recommendation how to use the DreamYaml api.
 */
public class CodingStyleExample {

    // If you only got a few modules and you want quick access across your code, you can add them as static fields and load your yaml file once at startup
    // If you also want to keep this values up to date you can add a DYWatcher with an DYAction which does that.
    public static DYModule FIRST_NAME;
    public static DYModule LAST_NAME;
    public static DYModule AGE;
    public static DYModule PROFESSION;

    // If you prefer encapsulating the modules you can do so, but remember that you will have to load your yaml file every time you create this class
    // This will ensure you always are using the latest values.
    private DYModule firstName;
    private DYModule lastName;
    private DYModule age;
    private DYModule work;

    @Test
    void test() throws Exception {
        DreamYaml yaml = new DreamYaml(System.getProperty("user.dir") + "/src/test/coding-style-example.yml");
        //yaml.load(); // We don't need to call this, since autoLoad is enabled by default.
        yaml.reset(); // DO NOT CALL THIS IN PRODUCTION, IT WILL REMOVE ALL THE INFORMATION FROM YOUR YAML FILE!
        // I am doing this only for the sake of testing!

        FIRST_NAME = yaml.put("name").setDefValues("John");
        LAST_NAME = yaml.put("last-name").setDefValues("Goldman");
        AGE = yaml.put("age").setDefValues("29");
        PROFESSION = yaml.put("work").setDefValues("Reporter");

        firstName = yaml.put("encapsulated", "name").setDefValues("John");
        lastName = yaml.put("encapsulated", "last-name").setDefValues("Goldman");
        age = yaml.put("encapsulated", "age").setDefValues("29");
        work = yaml.put("encapsulated", "work").setDefValues("Reporter");

        yaml.save(true);

        /*
name: John
last-name: Goldman
age: 29
work: Reporter
encapsulated:
  name: John
  last-name: Goldman
  age: 29
  work: Reporter
         */
    }

    // Getters for encapsulated modules:

    public DYModule getFirstName() {
        return firstName;
    }

    public DYModule getLastName() {
        return lastName;
    }

    public DYModule getAge() {
        return age;
    }

    public DYModule getWork() {
        return work;
    }
}
