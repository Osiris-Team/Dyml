package com.osiris.dyml;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DymlTest {

    @Test
    void testCodeStyle() {
        Dyml dyml = Dyml.from("" +
                "# This is a comment.\n" +
                "name Peter\n" +
                "age 32\n" +
                "child\n" +
                "\n" +
                "  name James\n" +
                "  age 11\n" +
                "\n" +
                "name John\n" +
                "age 24\n" +
                "children\n" +
                "\n" +
                "  name Samantha\n" +
                "  age 3\n" +
                "\n" +
                "  name Steve \n" +
                "  age 1\n" +
                "\n" +
                "unrelated\n" +
                "  \n" +
                "  name Victory\n" +
                "  age 34");
        assertEquals("Peter", dyml.get(0).get("name").asString());
    }
}