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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Responsible for reading the provided file/stream and parsing it into modules.
 */
class DymlReader {
    private List<Integer> listCountSpaces = new ArrayList<>(50);
    private List<DymlSection> listSections = new ArrayList<>(50);

    // Last lines information: (use fields instead of an actual line object bc of performance)
    private boolean lastCommentFound;
    private List<String> lastComments = new ArrayList<>();

    private DymlSection section;
    // Current lines information:
    private int countSpaces;
    private boolean commentFound;
    private boolean keyFound;
    private boolean charFound;

    public List<DymlSection> parse(File file, InputStream inputStream, String inString) throws DYReaderException, IOException, IllegalListException {
        BufferedReader reader = null;
        if (file != null) {
            if (!file.exists()) throw new DYReaderException("File '" + file + "' doesn't exist!");
            reader = new BufferedReader(new FileReader(file));
        }
        if (inputStream!=null){
            reader = new BufferedReader(new InputStreamReader(inputStream));
        }
        if (inString !=null){
            reader = new BufferedReader(new StringReader(inString));
        }
        if (reader==null){
            throw new DYReaderException("File/InputStream/String are all null. Nothing to read/load dyml from!");
        }

        // Parse the first line manually, so that the lastSection is NOT null, and we don't have to check it every time
        char[] line = new char[10000]; // Max line length of 10k chars
        int lineIndex = 0;
        int c;
        try{
            while (true){ // Use this special loop to avoid an actual while loop, to increase read speed
                for (int i = 0; i < Integer.MAX_VALUE; i++) {
                    c = reader.read(); // Read char by char until the end
                    switch (c){ // Instead of if bc of performance
                        // The cases below are sorted from probably most abundant char in the file to least
                        case 32: // ' '
                            if (!charFound) countSpaces++;
                            break;
                        case 10: // '\n' (aka next line char, aka end of the current line)
                            // Run always on \n
                            // Save important current line info
                            lastCommentFound = commentFound;
                            // Reset current line info
                            countSpaces = 0;
                            commentFound = false;
                            keyFound = false;
                            charFound = false;
                            lineIndex = 0;
                            break;
                        case -1: // 'EOF' (end of file "char")
                            throw new EOFException(); // Do this bc then of performance
                        default: // We are at the first char after spaces
                            charFound = true;
                            line[lineIndex] = (char) c;
                            if (countSpaces%2==0){
                                keyFound = true;
                                if (lastCommentFound){
                                    section = new DymlSection(new SmartString(null), new SmartString(null), lastComments);
                                    lastComments = new ArrayList<>();
                                }
                                else{
                                    section = new DymlSection(new SmartString(null), new SmartString(null), new ArrayList<>());
                                }
                                // Determine key:
                                int indexStart = lineIndex;
                                lineIndex++; // From previous read()
                                for (int k = 0; k < Integer.MAX_VALUE; k++) { // No while loop bc of performance
                                    c = reader.read();
                                    line[lineIndex] = ((char) c);
                                    if (c==32){
                                        String key = new String(Arrays.copyOfRange(line, indexStart, lineIndex));
                                        section.key = new SmartString(key);
                                        // Value can only be determined in this case, otherwise its null
                                        indexStart = lineIndex;
                                        lineIndex++; // From previous read()
                                        for (int j = 0; j < Integer.MAX_VALUE; j++) { // No while loop bc of performance
                                            c = reader.read();
                                            line[lineIndex] = ((char) c);
                                            if (c==10){ // Reads until the end of the line
                                                String value = new String(Arrays.copyOfRange(line, indexStart, lineIndex));
                                                section.value = new SmartString(emptyToNull(value));
                                                break;
                                            } else if(c==-1){
                                                String value = new String(Arrays.copyOfRange(line, indexStart, lineIndex));
                                                section.value = new SmartString(emptyToNull(value));
                                                break; // Don't throw exception yet. Let the stuff below finish first.
                                            }
                                            lineIndex++;
                                        }
                                        break;
                                    }
                                    else if (c==10){ // Reads until the end of the line
                                        String key = new String(Arrays.copyOfRange(line, indexStart, lineIndex));
                                        section.key = new SmartString(key);
                                        break;
                                    } else if(c==-1){
                                        String key = new String(Arrays.copyOfRange(line, indexStart, lineIndex));
                                        section.key = new SmartString(key);
                                        break; // Don't throw exception yet. Let the stuff below finish first.
                                    }
                                    lineIndex++;
                                }

                                // Determine this sections parent:
                                // Go reversely through the sections list and pick the first section where the difference in spaces is 2.
                                // It can be that this is a G0 module. In that case we don't need to search for a parent.
                                if (countSpaces > 0) {
                                    for (int k = (listSections.size() - 1); k >= 0; k--) {
                                        if ((listCountSpaces.get(k)-countSpaces) == 2){
                                            DymlSection parent = listSections.get(k);
                                            section.parent = parent;
                                            parent.children.add(section);
                                        }
                                    }
                                }
                                listSections.add(section);
                                listCountSpaces.add(countSpaces);
                            } else {
                                commentFound = true;
                                int indexStart = lineIndex;
                                lineIndex++; // From previous read()
                                for (int k = 0; k < Integer.MAX_VALUE; k++) { // No while loop bc of performance
                                    c = reader.read();
                                    line[lineIndex] = ((char) c);
                                    if (c==10){ // Reads until the end of the line
                                        String comment = new String(Arrays.copyOfRange(line, indexStart, lineIndex));
                                        lastComments.add(emptyToNull(comment));
                                        break;
                                    } else if(c==-1){
                                        String comment = new String(Arrays.copyOfRange(line, indexStart, lineIndex));
                                        lastComments.add(emptyToNull(comment));
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
                            charFound = false;
                            lineIndex = 0;
                    }
                }
            }
        } catch (EOFException ignored){
            // Reached end
        }
        catch (Exception e) {
            throw e;
        }
        return listSections;
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
