/*
 *  Copyright Osiris Team
 *  All rights reserved.
 *
 *  This software is licensed work.
 *  Please consult the file "LICENSE" for details.
 */

package com.osiris.dyml;

import com.osiris.dyml.utils.UtilsDYModule;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class DYWriterTest {

    @Test
    void alreadyExists() {
        DYModule queryModule1 = new DYModule().setKeys("p1", "c2"); // Should be found
        DYModule queryModule2 = new DYModule().setKeys("random", "not matching stuff"); // Should not be found
        List<DYModule> list = new ArrayList<>();
        list.add(new DYModule().setKeys("p1", "c1"));
        list.add(new DYModule().setKeys("p1", "c2"));

        assertNotNull(new UtilsDYModule().getExisting(queryModule1, list));
        assertNull(new UtilsDYModule().getExisting(queryModule2, list));
    }

    /*
    @Deprecated
    @Test
    void writeDYModuleListInsideAnotherDYModule() throws Exception {
        DreamYaml yaml = new DreamYaml(System.getProperty("user.dir") + "/src/test/complicated-stuff.yml", true);
        yaml.printAll();
        yaml.add("single module inside of another module").addValue(
                new DYValue(new DYModule("I am a module inside another module").setDefValues("yay")));

        yaml.add("multiple modules inside of another module").addValues(
                new DYValue(new DYModule("inside-module-1").setDefValue("hello")),
                new DYValue(new DYModule("inside-module-2").setDefValue("there")),
                new DYValue(new DYModule("inside-module-3").setDefValue("my friend")));

        yaml.add("multiple modules and values inside of another module").addValues(
                new DYValue(new DYModule("inside-module-1").setDefValues("hello")),
                new DYValue("there"),
                new DYValue(new DYModule("inside-module-2").setDefValues("my friend")));

        yaml.add("single module with list, inside of another module").addValues(
                new DYValue(new DYModule("inside-module-1").setDefValues("hello", "there", "my friend")));

        yaml.add("multiple modules with lists and values, inside of another module").addValues(
                new DYValue(new DYModule("inside-module-1").setDefValues("hello", "there", "my friend")),
                new DYValue("hello"),
                new DYValue(new DYModule("its meeee").setDefValue("from the other sideeee!")));

        yaml.add("module inside module, inside module, inside another module").addValues(
                new DYValue(new DYModule("inside-module-1").setDefValues(new DYValue("hello"), new DYValue("there"),
                        new DYValue(new DYModule("inside-module-2").setValue(new DYValue(new DYModule("inside-module-3")))))));
        // Well this gets pretty fucked up pretty quickly...

        yaml.add("module inside module, inside module, inside another module with comments")
                .addComments("Multilined", "comment")
                .addValues(
                        new DYValue(new DYModule("inside-module-1").addComment("dammn son").setDefValues(new DYValue("hello").setComment("this is a side-comment"), new DYValue("there"),
                                new DYValue(new DYModule("inside-module-2").setValue(new DYValue(new DYModule("inside-module-3")))))));

        yaml.printAll();
        yaml.save(true);
    }

     */
}