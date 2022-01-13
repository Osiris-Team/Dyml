/*
 *  Copyright Osiris Team
 *  All rights reserved.
 *
 *  This software is licensed work.
 *  Please consult the file "LICENSE" for details.
 */

package com.osiris.dyml;

import com.osiris.dyml.exceptions.DYReaderException;
import com.osiris.dyml.exceptions.IllegalListException;

import java.io.*;
import java.util.List;

public class Dyml {
    public List<DymlSection> loadedSections;

    public Dyml(List<DymlSection> loadedSections) {
        this.loadedSections = loadedSections;
    }

    /**
     * Reads the dyml content from the provided file and returns a new {@link Dyml} object representing it.
     */
    public static Dyml from(File file) throws IOException, DYReaderException, IllegalListException {
        return new Dyml(new DymlReader().parse(file, null, null));
    }

    /**
     * Reads the dyml content from the provided InputStream and returns a new {@link Dyml} object representing it.
     */
    public static Dyml from(InputStream inputStream) throws IOException, DYReaderException, IllegalListException {
        return new Dyml(new DymlReader().parse(null, inputStream, null));
    }

    /**
     * Reads the dyml content from the provided String and returns a new {@link Dyml} object representing it.
     */
    public static Dyml from(String string) throws IOException, DYReaderException, IllegalListException {
        return new Dyml(new DymlReader().parse(null, null, string));
    }

    public static OutputStream to(OutputStream out) {
        return null;
    }

    public static File to(File file) {
        return null;
    }

    public static String to() {
        return null;
    }

    /**
     * Returns the {@link DymlSection} with the provided key, or null if not found.
     */
    public DymlSection get(String... keys) {
        DymlSection foundSection = null;
        List<DymlSection> listToSearch = loadedSections;
        for (int i = 0; i < keys.length; i++) {
            String key = keys[i];
            for (DymlSection section :
                    listToSearch) {
                if (section.key.asString().equals(key)) {
                    if (i == keys.length - 1)
                        foundSection = section;
                    else
                        listToSearch = section.children;

                }
            }
        }
        return foundSection;
    }

    public void printSections(PrintStream out) {
        if (loadedSections.size() == 0) System.err.println("List is empty!");
        out.println("Index | Key | Value | Comments");
        for (int i = 0; i < loadedSections.size(); i++) {
            DymlSection section = loadedSections.get(i);
            out.println(i + " '" + section.key.asString() + "' '" + section.value.asString() + "' '" + section.comments.toString() + "'");
        }
    }
}
