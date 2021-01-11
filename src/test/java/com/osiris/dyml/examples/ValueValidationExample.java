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
 * Very basic example of value validation.
 */
public class ValueValidationExample {

    @Test
    void test() throws Exception {
        DreamYaml yaml = new DreamYaml(System.getProperty("user.dir")+"/src/test/value-validation-example.yml");
        yaml.load();

        DYModule m = yaml.add("is-valid").setDefValue("false");

        yaml.save(); // It could be that the file is empty and the default value doesn't exist yet.


        if (!m.asBoolean())
            System.err.println("Invalid value '"+m.getValue()+"' at "+m.getKeys()+" Corrected to -> '"+m.setValue("true").getValue()+"'");

        yaml.save(); // Remember to save and update the file, after doing the correction.


        /*
        BEFORE CORRECTION:
         is-valid: false
        AFTER CORRECTION:
         is-valid: true
         */

    }
}
