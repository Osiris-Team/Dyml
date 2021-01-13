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
    public static DYModule FIRST_NAME;
    public static DYModule LAST_NAME;
    public static DYModule AGE;
    public static DYModule PROFESSION;
    // If you prefer encapsulating the modules you can do so, but remember that you will have to load your yaml file every time you create this class
    private DYModule firstName;
    private DYModule lastName;
    private DYModule age;
    private DYModule work;

    @Test
    void test() throws Exception {
        DreamYaml yaml = new DreamYaml(System.getProperty("user.dir")+"/src/test/coding-style-example.yml");
        yaml.load();

        FIRST_NAME = yaml.add("name").setDefValue("John");
        LAST_NAME = yaml.add("last-name").setDefValue("Goldman");
        AGE = yaml.add("age").setDefValue("29");
        PROFESSION = yaml.add("work").setDefValue("Reporter");

        firstName = yaml.add("encapsulated","name").setDefValue("John");
        lastName = yaml.add("encapsulated","last-name").setDefValue("Goldman");
        age = yaml.add("encapsulated","age").setDefValue("29");
        work = yaml.add("encapsulated","work").setDefValue("Reporter");

        yaml.save();

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
