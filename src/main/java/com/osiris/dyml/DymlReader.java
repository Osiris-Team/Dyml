/*
 *  Copyright Osiris Team
 *  All rights reserved.
 *
 *  This software is licensed work.
 *  Please consult the file "LICENSE" for details.
 */

package com.osiris.dyml;


import com.osiris.dyml.exceptions.YamlReaderException;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

/**
 * Responsible for reading the provided file/stream and parsing it into modules.
 */
class DymlReader {

    /**
     * Parses the .dyml content of a file/stream/string into a special list, which only contains the root sections.
     * Those have references to their parent and child sections. <br>
     */
    public void parse(Dyml root, File file, InputStream inputStream, String inString) throws IOException, YamlReaderException {
        BufferedReader reader = null; // BufferedReader is faster than the regular Reader by around 0,100 ms
        try {
            if (file != null) {
                if (!file.exists()) throw new YamlReaderException("File '" + file + "' doesn't exist!");
                reader = new BufferedReader(new InputStreamReader(Files.newInputStream(file.toPath()), Dyml.charset));
            }
            if (inputStream != null) {
                reader = new BufferedReader(new InputStreamReader(inputStream, Dyml.charset));
            }
            if (inString != null) {
                reader = new BufferedReader(new StringReader(inString));
            }
            if (reader == null) {
                throw new YamlReaderException("File/InputStream/String are all null. Nothing to read/load dyml from!");
            }

            root.children.clear();
            List<Integer> spaces = new ArrayList<>(50);
            List<Dyml> sections = new ArrayList<>(50);
            // Last lines info: (use fields instead of an actual line object bc of performance)
            boolean lastCommentFound = false;
            List<String> lastComments = new ArrayList<>();

            // Current line info:
            Dyml section = null;
            int countSpaces = 0;
            boolean commentFound = false;
            String line;
            char c;
            char cSpace = ' ';
            while ((line = reader.readLine()) != null) {
                int length = line.length();
                int i;
                for (i = 0; i < length; i++) {
                    c = line.charAt(i);
                    if (c == cSpace) countSpaces++;
                    else {
                        i++;
                        break;
                    }
                }

                if (countSpaces % 2 == 0) { // Key goes until the next space
                    if (lastCommentFound) {
                        section = new Dyml(null, new SmartString(), lastComments);
                        lastComments = new ArrayList<>();
                    } else {
                        section = new Dyml(null, new SmartString(), new ArrayList<>());
                    }
                    // Determine key:
                    for (; i < length; i++) {
                        c = line.charAt(i);
                        if (c == cSpace) {
                            section.key = line.substring(countSpaces, i);
                            section.value.set(emptyToNull(line.substring(i + 1, length)));
                            break;
                        }
                    }

                    // Determine this sections parent:
                    // Go reversely through the sections list and pick the first section where the difference in spaces is 2.
                    // It can be that this is a G0 module. In that case we don't need to search for a parent.
                    if (countSpaces > 0) {
                        for (int k = (sections.size() - 1); k >= 0; k--) {
                            if ((countSpaces - spaces.get(k)) == 2) {
                                Dyml parent = sections.get(k);
                                section.parent = parent;
                                parent.children.add(section);
                                break;
                            }
                        }
                    } else {
                        section.parent = root;
                        root.children.add(section);
                    }
                    sections.add(section);
                    spaces.add(countSpaces);

                } else { // Comment goes until the end of the line
                    commentFound = true;
                    lastComments.add(emptyToNull(line.substring(i, length)));
                }

                // Save important current line info
                lastCommentFound = commentFound;
                // Reset current line info
                countSpaces = 0;
                commentFound = false;
            }
        } catch (YamlReaderException | IOException e) {
            if (file != null || inString != null) reader.close();
            throw e;
        } finally {
            if (file != null || inString != null) reader.close();
        }

    }

    /**
     * If the provided String is empty, return null.
     * This is useful, because {@link String#substring(int)} returns an empty string instead of null,
     * even though an empty string in YAML means that the value is null.
     */
    private String emptyToNull(String s) {
        if (s.trim().isEmpty()) return null;
        return s;
    }

}
