package com.osiris.dyml;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class DYModuleTest {

    @Test
    void nullValuesTest() throws Exception {
        DreamYaml yaml = new DreamYaml(System.getProperty("user.dir") + "/src/test/null-values-test.yml"
        , true);
        yaml.reset(); // Make sure the file is empty
        DYModule nullValueModule = yaml.add("i have no value")
                .setValue(null)
                .setDefValue(null);
        yaml.reload();
        assertTrue(null == nullValueModule.asString());
        assertTrue(null == nullValueModule.getDefaultValue());
        DYModule secondModule = yaml.add("im also empty inside");
        yaml.reload();
    }
}