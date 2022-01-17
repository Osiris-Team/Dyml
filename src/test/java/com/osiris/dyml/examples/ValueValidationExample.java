/*
 *  Copyright Osiris Team
 *  All rights reserved.
 *
 *  This software is licensed work.
 *  Please consult the file "LICENSE" for details.
 */

package com.osiris.dyml.examples;

import com.osiris.dyml.Yaml;
import com.osiris.dyml.YamlSection;
import org.junit.jupiter.api.Test;

/**
 * Very basic example of value validation.
 */
public class ValueValidationExample {

    @Test
    void test() throws Exception {
        Yaml yaml = new Yaml(System.getProperty("user.dir") + "/src/test/value-validation-example.yml");
        yaml.load();
        yaml.reset(); // DO NOT CALL THIS IN PRODUCTION, IT WILL REMOVE ALL THE INFORMATION FROM YOUR YAML FILE!
        // I am doing this only for the sake of testing!

        YamlSection module = yaml.put("is-valid").setDefValues("false");

        yaml.saveAndLoad(); // It could be that the file is empty and the default value doesn't exist yet.

        if (!module.asBoolean())
            System.err.println("Invalid value '" + module.getValue().asBoolean() + "' at " + module.getKeys() + " Corrected to -> '" + module.setValues("true").getValue().asBoolean() + "'");

        yaml.save(true); // Remember to save and update the file, after doing the correction.


        /*
        BEFORE CORRECTION:
         is-valid: false
        AFTER CORRECTION:
         is-valid: true
         */

    }
}
