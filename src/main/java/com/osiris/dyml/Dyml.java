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

import java.io.*;
import java.nio.file.Path;
import java.util.List;

public class Dyml {
    public List<DymlSection> sections;

    /**
     * <p style="color:red">Use the static method {@link #from(String)} instead to create {@link Dyml} objects.</p>
     */
    public Dyml(List<DymlSection> sections) {
        this.sections = sections;
    }

    /**
     * Reads the dyml content from the provided InputStream and returns a new {@link Dyml} object representing it.
     */
    public static Dyml from(InputStream inputStream) throws IOException, YamlReaderException, IllegalListException {
        return new Dyml(new DymlReader().parse(null, inputStream, null));
    }

    /**
     * Reads the dyml content from the provided String and returns a new {@link Dyml} object representing it.
     */
    public static Dyml from(String string) throws IOException, YamlReaderException, IllegalListException {
        return new Dyml(new DymlReader().parse(null, null, string));
    }

    /**
     * Reads the dyml content from the provided file and returns a new {@link Dyml} object representing it.
     */
    public static Dyml fromFile(File file) throws IOException, YamlReaderException, IllegalListException {
        return new Dyml(new DymlReader().parse(file, null, null));
    }
    /**
     * Reads the dyml content from the provided file and returns a new {@link Dyml} object representing it.
     */
    public static Dyml fromFile(String filePath) throws IOException, YamlReaderException, IllegalListException {
        return new Dyml(new DymlReader().parse(new File(filePath), null, null));
    }
    /**
     * Reads the dyml content from the provided file and returns a new {@link Dyml} object representing it.
     */
    public static Dyml fromFile(Path filePath) throws IOException, YamlReaderException, IllegalListException {
        return new Dyml(new DymlReader().parse(filePath.toFile(), null, null));
    }


    /**
     * Parses this {@link Dyml} object and writes it to the provided output.
     */
    public OutputStream to(OutputStream out) throws YamlWriterException, IOException {
        new DymlWriter().parse(this, null, out, null, false);
        return out;
    }

    /**
     * Parses this {@link Dyml} object and writes it to a {@link String}, which gets returned.
     */
    public String asString() throws YamlWriterException, IOException {
        return new DymlWriter().parse(this, null, null, "", false);
    }

    /**
     * Parses this {@link Dyml} object and writes it to the provided output.
     */
    public File toFile(File file) throws YamlWriterException, IOException {
        new DymlWriter().parse(this, file, null, null, false);
        return file;
    }
    /**
     * Parses this {@link Dyml} object and writes it to the provided output.
     */
    public File toFile(String filePath) throws YamlWriterException, IOException {
        File file = new File(filePath);
        new DymlWriter().parse(this, file, null, null, false);
        return file;
    }
    /**
     * Parses this {@link Dyml} object and writes it to the provided output.
     */
    public File toFile(Path filePath) throws YamlWriterException, IOException {
        File file = filePath.toFile();
        new DymlWriter().parse(this, file, null, null, false);
        return file;
    }


    /**
     * Returns the {@link DymlSection} with the provided key(s), or null if not found.
     */
    public DymlSection get(String... keys) {
        DymlSection foundSection = null;
        List<DymlSection> listToSearch = sections;
        for (int i = 0; i < keys.length; i++) {
            String key = keys[i];
            for (DymlSection section :
                    listToSearch) {
                if (section.key.equals(key)) {
                    if (i == keys.length - 1)
                        foundSection = section;
                    else
                        listToSearch = section.children;

                }
            }
        }
        return foundSection;
    }

    /**
     * Returns the {@link DymlSection} at the provided index.
     */
    public DymlSection getAt(int index) {
        return sections.get(index);
    }

    public void printSections(PrintStream out) {
        if (sections.size() == 0) System.err.println("List is empty!");
        out.println("Index | Key | Value | Comments");
        for (int i = 0; i < sections.size(); i++) {
            DymlSection section = sections.get(i);
            out.println("I:"+i + " KEY:'" + section.key + "' VAL:'" + section.value.asString() + "' COM:'" + section.comments.toString() + "'");
            if (!section.children.isEmpty()){
                out.print("  -> CHILD: ");
                for (DymlSection child :
                        section.children) {
                    out.print(child.key);
                }
                out.println();
            }
        }
    }
}
