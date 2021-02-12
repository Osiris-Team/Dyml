package com.osiris.dyml;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DreamYamlTest {

    @Test
    void getAddedModuleByKeys() throws Exception{
        DreamYaml yaml = new DreamYaml(System.getProperty("user.dir") + "/tests.yml");
        yaml.load();
        yaml.add("hello").setDefValue("world");
        yaml.save();
        assertEquals("world", yaml.getAddedModuleByKeys("hello").asString());
    }

    @Test
    void getLoadedModuleByKeys() throws Exception {
        DreamYaml yaml = new DreamYaml(System.getProperty("user.dir") + "/tests.yml");
        yaml.load();
        yaml.add("hello").setDefValue("world");
        yaml.save();
        yaml.load(); // Reload the config so we got the loaded Module
        assertEquals("world", yaml.getLoadedModuleByKeys("hello").asString());
    }
}