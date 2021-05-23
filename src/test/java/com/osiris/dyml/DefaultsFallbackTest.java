/*
 *  Copyright Osiris Team
 *  All rights reserved.
 *
 *  This software is licensed work.
 *  Please consult the file "LICENSE" for details.
 */

package com.osiris.dyml;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class DefaultsFallbackTest {

    @Test
    void testFallback() throws Exception{
        DreamYaml yml = new DreamYaml(System.getProperty("user.dir")+"/src/test/test-fallback.yml");
        yml.reset(); // Make sure that the file is empty
        DYModule m1 = yml.add("v1").setValue((DYValue) null).setDefValue("def-value");
        m1.setFallbackOnDefault(false);
        assertTrue(m1.getValue() == null); // Returns null, since fallback is disabled

        m1.setFallbackOnDefault(true);
        assertTrue(m1.asString().equals("def-value")); // Returns the default value, since fallback is enabled again
        yml.save(true);
    }

}