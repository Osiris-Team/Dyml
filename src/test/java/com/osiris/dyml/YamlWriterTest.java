/*
 *  Copyright Osiris Team
 *  All rights reserved.
 *
 *  This software is licensed work.
 *  Please consult the file "LICENSE" for details.
 */

package com.osiris.dyml;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class YamlWriterTest {

    @Test
    void testWritingWithoutLineBreaks() {
        String actual = "hello\nthere\n";
        String expected = "hello there ";
        actual = actual.replace("\n", " ");
        Assertions.assertEquals(expected, actual);
    }
}