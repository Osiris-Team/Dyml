package com.osiris.dyml;

import com.osiris.dyml.exceptions.DYReaderException;
import com.osiris.dyml.exceptions.IllegalListException;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DymlTest {

    @Test
    void testCodeStyle() throws IOException, DYReaderException, IllegalListException {
        Dyml dyml = Dyml.from(" Project Name\n" +
                "architect amihaiemil\n" +
                " This is a test comment with no line breaks!\n" +
                "devops rultor 0pdd\n" +
                " This is a multi-lined comment!\n" +
                " See it goes on!\n" +
                "developers amihaiemil salikjan SherifWally");
        dyml.printSections(System.out);
        assertEquals("Peter", dyml.get("name").asString());
    }
}