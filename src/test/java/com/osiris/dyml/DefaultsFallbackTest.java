/*
 *  Copyright Osiris Team
 *  All rights reserved.
 *
 *  This software is licensed work.
 *  Please consult the file "LICENSE" for details.
 */

package com.osiris.dyml;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DefaultsFallbackTest {

    @Test
    void testFallback() throws Exception{
        DreamYaml yml = new DreamYaml(System.getProperty("user.dir")+"/src/test/test-fallback.yml");
        yml.load();
        DYModule v1 = yml.add("v1").setDefValue("def-value");
        v1.setFallbackOnDefault(false);
        v1.setValue("");
        yml.save(); // First we disable fallback and write an empty String for v1
        assertTrue(v1.asString().equals("")); // Check if that worked

        // Then enabled fallback again and check its value now
        v1.setFallbackOnDefault(true);
        yml.save();
        assertTrue(v1.asString().equals("def-value"));
    }

}