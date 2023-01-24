package com.osiris.dyml.utils;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BufferedSBWriterTest {

    @Test
    void write() throws IOException {
        StringBuilder builder = new StringBuilder();
        BufferedSBWriter writer = new BufferedSBWriter(builder);
        writer.write("hello\n");
        writer.write("there!\n");
        writer.write('a');
        writer.flush();
        assertEquals("hello\n" +
                "there!\n" +
                "a", writer.builder.toString());
    }
}