package com.osiris.dyml;

import com.osiris.dyml.exceptions.IllegalListException;
import com.osiris.dyml.exceptions.YamlReaderException;
import org.junit.jupiter.api.Test;

import java.io.IOException;

class DymlTest {

    @Test
    void testCodeStyle() throws IOException, YamlReaderException, IllegalListException {
        Dyml dyml = new Dyml("" +
                "key val\n" +
                "  c1 val\n" +
                "  c2 val\n" +
                "    g1 val\n");
        dyml.debugPrint(System.out);
    }
}