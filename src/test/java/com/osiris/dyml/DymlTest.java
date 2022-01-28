package com.osiris.dyml;

import com.osiris.dyml.exceptions.IllegalListException;
import com.osiris.dyml.exceptions.YamlReaderException;
import org.junit.jupiter.api.Test;

import java.io.IOException;

class DymlTest {

    @Test
    void testCodeStyle() throws IOException, YamlReaderException, IllegalListException {
        Dyml dyml = Dyml.from("" +
                "key val\n" +
                "  c1 val\n" +
                "  c2 val\n" +
                "    g1 val\n");
        dyml.printSections(System.out);
    }
}