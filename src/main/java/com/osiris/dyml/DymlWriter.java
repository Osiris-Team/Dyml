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
        Dyml dyml = Dyml.from("hello there\n" +
                "ma boi 69\n" +
                "  chil val\n" +
                "    c3 val\n" +
                "    c4 val\n" +
                "  skkr val\n" );
        System.out.println("Found: "+dyml.get("ma", "chil", "c4").asString());
        dyml.printSections(System.out);
        System.out.println(dyml.toText());
        System.out.println();
    }

    public String parse(Dyml dyml, File file, OutputStream outputStream, String outString, boolean reset) throws YamlWriterException, IOException {
        Objects.requireNonNull(dyml);
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

        List<Dyml> sections = new ArrayList<>(dyml.children);
        writeSections(writer, sections);

        if (stringWriter != null) return (outString = stringWriter.toString());
        writer.flush();

        writer.close();

        return null;
    }

    public void writeSections(PrintWriter writer, List<Dyml> sections) {
        for (int i = 0; i < sections.size(); i++) {
            Dyml section = sections.get(i);
            StringBuilder builder = new StringBuilder();
            for (int j = 0; j < section.countSpaces(); j++) {
                builder.append(" ");
            }
            String spaces = builder.toString();
            String spacesComments = spaces+" ";

            // Comments
            if (section.comments!=null && !section.comments.isEmpty()){
                for (String comment :
                        section.comments) {
                    if (comment.contains("\n")){
                        String[] arr = comment.split("\n");
                        for (String c :
                                arr) {
                            writer.println(spacesComments + c.trim());
                        }
                    } else{
                        writer.println(spacesComments + comment.trim());
                    }
                }
            }

            // Key and values
            writer.println(spaces + section.key.trim()+" "+section.value.asString().trim());

            if (!section.children.isEmpty()){
                writeSections(writer, section.children);
            }
        }
    }

    private void writeSection(PrintWriter writer, Dyml section) {
        int coutSpaces = 0;
        if (section.children.isEmpty()){
            coutSpaces = 0;
            Dyml parent = section.parent;
            while (parent != null){
                coutSpaces += 2;
                parent = parent.parent;
            }



        } else
            for (Dyml sec :
                    section.children) {
                writeSection(writer, sec);
            }
    }
}
