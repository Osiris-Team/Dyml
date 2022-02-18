/*
 *  Copyright Osiris Team
 *  All rights reserved.
 *
 *  This software is licensed work.
 *  Please consult the file "LICENSE" for details.
 */

package com.osiris.dyml;

import com.osiris.dyml.exceptions.IllegalListException;
import com.osiris.dyml.exceptions.YamlReaderException;
import com.osiris.dyml.exceptions.YamlWriterException;
import com.osiris.dyml.utils.UtilsTimeStopper;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Responsible for parsing and writing the provided modules.
 */
class DymlWriter {

    public static void main(String[] args) throws YamlReaderException, IOException, IllegalListException, YamlWriterException {
        Dyml dyml = new Dyml("hello there\n" +
                "ma boi 69\n" +
                "  chil val\n" +
                "    c3 val\n" +
                "    c4 val\n" +
                "  skkr val\n");
        System.out.println("Found: " + dyml.get("ma", "chil", "c4").asString());
        dyml.debugPrint(System.out);
        System.out.println(dyml.saveToText());
        System.out.println();
    }

    public String parse(List<Dyml> sections, File file, OutputStream outputStream, String outString, boolean reset) throws YamlWriterException, IOException {
        Objects.requireNonNull(sections);
        if (sections.isEmpty()) throw new YamlWriterException("Sections cannot be empty!");
        PrintWriter writer = null; // Buffered is faster than the regular Reader by around 0,100 ms
        StringWriter stringWriter = null;
        if (file != null) {
            if (!file.exists()) throw new YamlWriterException("File '" + file + "' doesn't exist!");
            writer = new PrintWriter(new FileWriter(file));
        }
        if (outputStream != null) {
            writer = new PrintWriter(new OutputStreamWriter(outputStream));
        }
        if (outString != null) {
            stringWriter = new StringWriter();
            writer = new PrintWriter(stringWriter);
        }
        if (writer == null) {
            throw new YamlWriterException("File/OutputStream/String are all null. Nothing to write/save dyml to!");
        }

        UtilsTimeStopper timer = new UtilsTimeStopper();
        timer.start();

        writer.write(""); // Clear old content
        if (reset) return null;

        List<Dyml> copy = new ArrayList<>(sections);
        writeSections(writer, copy);

        if (stringWriter != null) return stringWriter.toString();
        writer.flush();
        writer.close();
        return null;
    }

    /**
     * Writes all sections (parents and children) via the provided writer, recursively.
     */
    public void writeSections(PrintWriter writer, List<Dyml> sections) {
        for (Dyml section : sections) {
            StringBuilder builder = new StringBuilder();
            for (int j = 0; j < section.countSpaces(); j++) {
                builder.append(" ");
            }
            String spaces = builder.toString();
            String spacesComments = spaces + " ";

            // Comments
            if (section.comments != null && !section.comments.isEmpty()) {
                for (String comment :
                        section.comments) {
                    if (comment.contains("\n")) {
                        String[] arr = comment.split("\n");
                        for (String c :
                                arr) {
                            writer.println(spacesComments + c.trim());
                        }
                    } else {
                        writer.println(spacesComments + comment.trim());
                    }
                }
            }

            // Key and values
            if (section.value.asString() != null)
                writer.println(spaces + section.key.trim() + " " + section.value.asString().trim());
            else
                writer.println(spaces + section.key.trim() + " ");

            if (!section.children.isEmpty()) {
                writeSections(writer, section.children);
            }
        }
    }
}
