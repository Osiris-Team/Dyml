package com.osiris.dyml;

import com.osiris.dyml.exceptions.*;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class DYModuleTest {

    @Test
    void nullAndFallbackTests() throws Exception {
        DreamYaml yaml = new DreamYaml(System.getProperty("user.dir") + "/src/test/null-values-test.yml");
        yaml.load();
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

    @Test
    void testTopSpace() throws IOException, DuplicateKeyException, DYReaderException, IllegalListException, DYWriterException, NotLoadedException, IllegalKeyException {
        DreamYaml yaml = new DreamYaml(System.getProperty("user.dir") + "/src/test/top-space-test.yml");
        yaml.load();
        yaml.reset();
        yaml.put("i-got-3-spaces").setCountTopSpaces(3);
        yaml.put("i-got-1-space").setCountTopSpaces(1);
        yaml.saveAndLoad();
        assertTrue(yaml.get("i-got-3-spaces").getCountTopSpaces() == 3);
        assertTrue(yaml.get("i-got-1-space").getCountTopSpaces() == 1);
    }
}