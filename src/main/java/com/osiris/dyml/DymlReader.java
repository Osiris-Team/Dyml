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

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Responsible for reading the provided file/stream and parsing it into modules.
 */
class DymlReader {

    /**
     * Parses the .dyml content of a file/stream/string into a special list, which only contains the root sections.
     * Those have references to their parent and child sections. <br>
     */
    public List<Dyml> parse(File file, InputStream inputStream, String inString) throws YamlReaderException, IOException, IllegalListException {
        BufferedReader reader = null; // BufferedReader is faster than the regular Reader by around 0,100 ms
        if (file != null) {
            if (!file.exists()) throw new YamlReaderException("File '" + file + "' doesn't exist!");
            reader = new BufferedReader(new FileReader(file));
        }
        if (inputStream != null) {
            reader = new BufferedReader(new InputStreamReader(inputStream));
        }
        if (inString != null) {
            reader = new BufferedReader(new StringReader(inString));
        }
        if (reader == null) {
            throw new YamlReaderException("File/InputStream/String are all null. Nothing to read/load dyml from!");
        }

        List<Integer> spaces = new ArrayList<>(50);
        List<Dyml> sections = new ArrayList<>(50);
        // Last lines information: (use fields instead of an actual line object bc of performance)
        boolean lastCommentFound = false;
        List<String> lastComments = new ArrayList<>();

        Dyml section = null;
        // Current lines information:
        int countSpaces = 0;
        boolean commentFound = false;
        boolean keyFound = false;

        // Parse the first line manually, so that the lastSection is NOT null, and we don't have to check it every time
        char[] line = new char[10000]; // Max line length of 10k chars
        int lineIndex = 0;
        int c;
        try {
            while (true) { // Use this special loop to avoid an actual while loop, to increase read speed
                for (int i = 0; i < Integer.MAX_VALUE; i++) {
                    c = reader.read(); // Read char by char until the end
                    if (c == 32) { // ' '
                        countSpaces++;
                    } else if (c == 10) {
                        // Run always on \n
                        // Save important current line info
                        lastCommentFound = commentFound;
                        // Reset current line info
                        countSpaces = 0;
                        commentFound = false;
                        keyFound = false;
                        lineIndex = 0;
                        break;
                    } else if (c == -1) {
                        throw new EOFException(); // Do this bc then of performance
                    } else {
                        line[lineIndex] = (char) c;
                        if (countSpaces % 2 == 0) {
                            keyFound = true;
                            if (lastCommentFound) {
                                section = new Dyml(null, new SmartString(null), lastComments);
                                lastComments = new ArrayList<>();
                            } else {
                                section = new Dyml(null, new SmartString(null), new ArrayList<>());
                            }
                            // Determine key:
                            int indexStart = lineIndex;
                            lineIndex++; // From previous read()
                            for (int k = 0; k < Integer.MAX_VALUE; k++) { // No while loop bc of performance
                                c = reader.read();
                                line[lineIndex] = ((char) c);
                                if (c == 32) {
                                    section.key = new String(Arrays.copyOfRange(line, indexStart, lineIndex));
                                    // Value can only be determined in this case, otherwise its null
                                    indexStart = lineIndex;
                                    lineIndex++; // From previous read()
                                    while (true) { // If we use a for loop here it gets changed to a weird while loop anyways by the compiler
                                        c = reader.read();
                                        line[lineIndex] = ((char) c);
                                        if (c == 10) { // Reads until the end of the line
                                            section.value = new SmartString(emptyToNull(new String(Arrays.copyOfRange(line, indexStart, lineIndex))));
                                            break;
                                        } else if (c == -1) {
                                            section.value = new SmartString(emptyToNull(new String(Arrays.copyOfRange(line, indexStart, lineIndex))));
                                            break; // Don't throw exception yet. Let the stuff below finish first.
                                        }
                                        lineIndex++;
                                    }
                                    break;
                                } else if (c == 10) { // Reads until the end of the line
                                    section.key = new String(Arrays.copyOfRange(line, indexStart, lineIndex));
                                    break;
                                } else if (c == -1) {
                                    section.key = new String(Arrays.copyOfRange(line, indexStart, lineIndex));
                                    break; // Don't throw exception yet. Let the stuff below finish first.
                                }
                                lineIndex++;
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
                            }
                            sections.add(section);
                            spaces.add(countSpaces);

                        } else {
                            commentFound = true;
                            int indexStart = lineIndex;
                            lineIndex++; // From previous read()
                            for (int k = 0; k < Integer.MAX_VALUE; k++) { // No while loop bc of performance
                                c = reader.read();
                                line[lineIndex] = ((char) c);
                                if (c == 10) { // Reads until the end of the line
                                    lastComments.add(emptyToNull(new String(Arrays.copyOfRange(line, indexStart, lineIndex))));
                                    break;
                                } else if (c == -1) {
                                    lastComments.add(emptyToNull(new String(Arrays.copyOfRange(line, indexStart, lineIndex))));
                                    break; // Don't throw exception yet. Let the stuff below finish first.
                                }
                                lineIndex++;
                            }
                        }
                        // Run always on \n
                        // Save important current line info
                        //lastSection = section;
                        lastCommentFound = commentFound;
                        // Reset current line info
                        countSpaces = 0;
                        commentFound = false;
                        keyFound = false;
                        lineIndex = 0;
                        break;
                    }

                }
            }
        } catch (EOFException ignored) {
            // Reached end
        } catch (Exception e) {
            throw e;
        }

        List<Dyml> rootSections = new ArrayList<>();
        for (int i = 0; i < spaces.size(); i++) { // spaces and sections lists are same length
            if (spaces.get(i) == 0)
                rootSections.add(sections.get(i));
        }
        return rootSections;
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
