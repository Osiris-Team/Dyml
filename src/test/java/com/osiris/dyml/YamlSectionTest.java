package com.osiris.dyml;

import com.osiris.dyml.exceptions.*;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class YamlSectionTest {

    @Test
    void nullAndFallbackTests() throws Exception {
        Yaml yaml = new Yaml(System.getProperty("user.dir") + "/src/test/null-values-test.yml");
        yaml.load();
        yaml.isReturnDefaultWhenValueIsNullEnabled = false; // Disable to check values
        yaml.reset(); // Make sure the file is empty
        YamlSection nullValueModule = yaml.add("i have no value")
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

        yaml.isReturnDefaultWhenValueIsNullEnabled = true; // Enable
        assertNotNull(nullValueModule.getValue().asString());
        assertNotNull(nullValueModule.getDefValue().asString());

        assertNull(yaml.get("skrrrrr")); // This module doesnt exist so it should be null
        yaml.save(true);
    }

    @Test
    void testTopSpace() throws IOException, DuplicateKeyException, YamlReaderException, IllegalListException, YamlWriterException, NotLoadedException, IllegalKeyException {
        Yaml yaml = new Yaml(System.getProperty("user.dir") + "/src/test/top-space-test.yml");
        yaml.load();
        yaml.reset();
        yaml.put("i-got-3-spaces").setCountTopSpaces(3);
        yaml.put("i-got-1-space").setCountTopSpaces(1);
        yaml.saveAndLoad();
        assertTrue(yaml.get("i-got-3-spaces").getCountTopSpaces() == 3);
        assertTrue(yaml.get("i-got-1-space").getCountTopSpaces() == 1);
    }
}