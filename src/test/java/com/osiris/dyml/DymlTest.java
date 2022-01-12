package com.osiris.dyml;

import com.osiris.dyml.exceptions.DYReaderException;
import com.osiris.dyml.exceptions.IllegalListException;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DymlTest {

    @Test
    void testCodeStyle() throws IOException, DYReaderException, IllegalListException {
        Dyml dyml = Dyml.from("name Peter");
        dyml.printSections(System.out);

        assertEquals("Peter", dyml.get("name").asString());
    }
}