package com.osiris.dyml;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class DYModuleTest {

    @Test
    void nullAndFallbackTests() throws Exception {
        DreamYaml yaml = new DreamYaml(System.getProperty("user.dir") + "/src/test/null-values-test.yml");
        yaml.setReturnDefaultWhenValueIsNullEnabled(false); // Disable to check values
        yaml.reset(); // Make sure the file is empty
        DYModule nullValueModule = yaml.add("i have no value")
                .setValues((String) null)
                .setDefValues((String) null);
        yaml.saveAndLoad();

        assertNotNull(nullValueModule.getValue());
        assertNotNull(nullValueModule.getDefValue());
        assertNull(nullValueModule.getValue().asString());
        assertNull(nullValueModule.getDefValue().asString());

        nullValueModule.setDefValues("hello");
        assertNull(nullValueModule.getValue().asString());
        assertNotNull(nullValueModule.getDefValue().asString());

        yaml.setReturnDefaultWhenValueIsNullEnabled(true); // Enable
        assertNotNull(nullValueModule.getValue().asString());
        assertNotNull(nullValueModule.getDefValue().asString());

        assertNull(yaml.get("skrrrrr")); // This module doesnt exist so it should be null
        yaml.save(true);
    }
}