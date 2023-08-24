package com.osiris.dyml.utils;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Arrays;

/**
 * "Writes" to a {@link StringBuilder}.
 */
public class BufferedSBWriter extends BufferedWriter {
    public StringBuilder builder;

    public BufferedSBWriter() {
        this(new StringBuilder());
    }

    public BufferedSBWriter(@NotNull StringBuilder builder) {
        super(new OutputStreamWriter(new ByteArrayOutputStream()));
        this.builder = builder;
    }

    @Override
    public void write(@NotNull char[] cbuf) throws IOException {
        builder.append(cbuf);
    }

    @Override
    public void write(@NotNull String str) throws IOException {
        builder.append(str);
    }

    @Override
    public void write(int c) throws IOException {
        builder.append((char) c);
    }

    @Override
    public void write(@NotNull char[] cbuf, int off, int len) throws IOException {
        builder.append(Arrays.copyOfRange(cbuf, off, len));
    }

    @Override
    public void write(@NotNull String s, int off, int len) throws IOException {
        builder.append(Arrays.copyOfRange(s.toCharArray(), off, len));
    }
}
