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
        DYModule queryModule1 = new DYModule().setKeys("p1","c2"); // Should be found
        DYModule queryModule2 = new DYModule().setKeys("random","not matching stuff"); // Should not be found
        List<DYModule> list = new ArrayList<>();
        list.add(new DYModule().setKeys("p1","c1"));
        list.add(new DYModule().setKeys("p1","c2"));

        assertNotNull(new UtilsDYModule().getExisting(queryModule1, list));
        assertNull(new UtilsDYModule().getExisting(queryModule2, list));
    }

    @Test
    void writeDYModuleInsideAnotherDYModule() throws Exception {
        DreamYaml yaml = new DreamYaml(System.getProperty("user.dir") + "/src/test/writeDYModuleInsideAnotherDYModule.yml");
        yaml.load();
        yaml.add("module1").setValues(new DYValue(new DYModule("module2").setDefValues("module2s value")));
        yaml.save(true);
    }

    @Test
    void writeDYModuleListInsideAnotherDYModule() throws Exception {
        DreamYaml yaml = new DreamYaml(System.getProperty("user.dir") + "/src/test/writeDYModuleListInsideAnotherDYModule.yml");
        yaml.load();
        yaml.add("module1").setValues(new DYValue(new DYModule("module2").setDefValues("hello", "there", "my guy")));
        yaml.save(true);
    }
}