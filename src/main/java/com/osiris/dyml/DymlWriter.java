/*
 *  Copyright Osiris Team
 *  All rights reserved.
 *
 *  This software is licensed work.
 *  Please consult the file "LICENSE" for details.
 */

package com.osiris.dyml;

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


    public String parse(List<Dyml> sections, File file, OutputStream outputStream, String outString, boolean reset) throws YamlWriterException, IOException {
        Objects.requireNonNull(sections);
        if (sections.isEmpty()) throw new YamlWriterException("Sections cannot be empty!");
        PrintWriter writer = null; // Buffered is faster than the regular Reader by around 0,100 ms
        StringWriter stringWriter = null;
        if (file != null) {
            if (!file.exists()) throw new YamlWriterException("File '" + file + "' doesn't exist!");
            writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), Dyml.charset)));
        }
        if (outputStream != null) {
            writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(outputStream, Dyml.charset)));
        }
        if (outString != null) {
            stringWriter = new StringWriter();
            writer = new PrintWriter(stringWriter);
        }
        if (writer == null) {
            throw new YamlWriterException("File/OutputStream/String are all null. Nothing to write/save dyml to!");
        }

        try {
            UtilsTimeStopper timer = new UtilsTimeStopper();
            timer.start();

            writer.write(""); // Clear old content
            if (reset) return null;

            List<Dyml> copy = new ArrayList<>(sections);
            writeSections(writer, copy);

            if (stringWriter != null) return stringWriter.toString();
            writer.flush();
            writer.close();
        } finally {
            // Close only writers we created, which means the writer for the provided
            // outputstream stays open.
            if (file != null || outString != null) writer.close();
        }
        return null;
    }

    /**
     * Writes all sections (parents and children) via the provided writer, recursively.
     */
    public void writeSections(PrintWriter writer, List<Dyml> sections) {
        for (Dyml section : sections) {
            StringBuilder builder = new StringBuilder();
            for (int j = 0; j < section.countParents() - 1; j++) { // -1 bc of the root section
                builder.append("  ");
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
