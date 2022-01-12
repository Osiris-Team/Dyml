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
import com.osiris.dyml.utils.UtilsDYModule;
import com.osiris.dyml.utils.UtilsTimeStopper;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Responsible for reading the provided file/stream and parsing it into modules.
 */
class DymlReader {
    private DYDebugLogger debug;
    private List<Integer> listCountSpaces = new ArrayList<>();
    private List<DymlSection> listSections;

    private DymlSection lastSection;
    // Last lines information: (use fields instead of an actual line object bc of performance)
    private boolean lastCommentFound;

    private DymlSection section;
    // Current lines information:
    private int countSpaces;
    private boolean commentFound;
    private boolean keyFound;
    private boolean charFound;

    public void parse(Dyml dyml) throws DYReaderException, IOException, IllegalListException {
        this.debug = dyml.debugLogger;

        UtilsTimeStopper timer = new UtilsTimeStopper();
        timer.start();

        BufferedReader reader = null;
        if (dyml.file != null) {
            if (!dyml.file.exists()) throw new DYReaderException("File '" + dyml.file + "' doesn't exist!");
            reader = new BufferedReader(new FileReader(dyml.file));
            debug.log(this, "Started reading dyml from file '"+dyml.file+"'");
        }
        if (dyml.inputStream!=null){
            reader = new BufferedReader(new InputStreamReader(dyml.inputStream));
            debug.log(this, "Started reading dyml from InputStream '"+dyml.inputStream+"'");
        }
        if (dyml.inString !=null){
            reader = new BufferedReader(new StringReader(dyml.inString));
            debug.log(this, "Started reading dyml from String '"+dyml.inString +"'");
        }
        if (reader==null){
            System.out.println("File and InputStream are both null. Nothing to read/load dyml from!");
            return;
        }
        listSections = dyml.getAllLoaded();
        listSections.clear();


        // Parse the first line manually, so that the lastSection is NOT null, and we don't have to check it every time
        StringBuilder firstLine = new StringBuilder(100);
        int firstChar = reader.read();
        if (firstChar==32) { // ' '
            lastCommentFound = true;
            lastSection = new DymlSection(
                    new SmartString(emptyToNull(firstLine.toString())),
                    new SmartString(emptyToNull(reader.readLine())), // Read the rest of the line
                    null
            );
        } else{
            for (int i = 0; i < Integer.MAX_VALUE; i++) {
                firstChar = reader.read();
                firstLine.append((char) firstChar);
                if (firstChar==32 || firstChar==10 || firstChar==-1){
                    lastSection = new DymlSection(
                            new SmartString(emptyToNull(firstLine.toString())),
                            new SmartString(emptyToNull(reader.readLine())), // Read the rest of the line
                            null
                     );
                    break;
                }
            }
        }

        // This is the actual loop
        StringBuilder line = new StringBuilder(100); // Max line length of 10k chars
        int c;
        try{
            while (true){ // Use this special loop to avoid an actual while loop, to increase read speed
                for (int i = 0; i < Integer.MAX_VALUE; i++) {
                    c = reader.read(); // Read char by char until the end
                    line.append((char) c);
                    switch (c){ // Instead of if bc of performance
                        // The cases below are sorted from probably most abundant char in the file to least
                        case 32: // ' '
                            if (!charFound) countSpaces++;
                            else { // Found a key, read until next space
                                for (int j = 0; j < Integer.MAX_VALUE; j++) { // No while loop bc of performance
                                    c = reader.read();
                                    line.append((char) c);
                                    if (c==32 || c==10 || c==-1){
                                        if (countSpaces%2==0){
                                            keyFound = true;
                                            // parent
                                            //    A comment
                                            //   child value <---
                                            if (lastCommentFound)
                                                section = lastSection;
                                            section.key = new SmartString(emptyToNull(line.toString()));
                                            section.value = new SmartString(emptyToNull(reader.readLine())); // Read the rest of the line
                                            c = 10; // \n
                                            // Determine this sections parent:
                                            // Go reversely through the sections list and pick the first section where the difference in spaces is 2.
                                            // It can be that this is a G0 module. In that case we don't need to search for a parent.
                                            if (countSpaces > 0) {
                                                for (int k = listSections.size() - 1; k >= 0; k--) {
                                                    if ((listCountSpaces.get(k)-countSpaces) == 2){
                                                        DymlSection parent = listSections.get(k);
                                                        section.parent = parent;
                                                        parent.children.add(section);
                                                    }
                                                }
                                            }
                                            listSections.add(section);
                                            listCountSpaces.add(countSpaces);
                                        } else{
                                            commentFound = true;
                                            String comment = emptyToNull(reader.readLine()); // Read the rest of the line
                                            c = 10; // \n
                                            section.comment = comment;
                                        }
                                        break;
                                    }
                                }
                            }
                            break;
                        case 10: // '\n' (aka next line char, aka end of the current line)
                            charFound = true;
                            // Save important current line info
                            lastSection = section;
                            lastCommentFound = commentFound;
                            // Reset current line info
                            line = new StringBuilder(100);
                            countSpaces = 0;
                            commentFound = false;
                            keyFound = false;
                            charFound = false;
                            break;
                        case -1: // 'EOF' (end of file "char")
                            throw new EOFException(); // Do this bc then of performance
                        default:
                            // Any other charCode than above will count as word
                            charFound = true;
                    }
                }
            }
        } catch (EOFException ignored){
            // Reached end
        }
        catch (Exception e) {
            throw e;
        }

        // Do post processing if enabled
        UtilsDYModule utils = new UtilsDYModule();
        if (dyml.isPostProcessingEnabled) {

            if (dyml.isTrimLoadedValuesEnabled)
                for (DYModule m :
                        dyml.getAllLoaded()) {
                    utils.trimValues(m.getValues());
                }

            if (dyml.isRemoveQuotesFromLoadedValuesEnabled)
                for (DYModule m :
                        dyml.getAllLoaded()) {
                    utils.removeQuotesFromValues(m.getValues());
                }

            if (dyml.isRemoveLoadedNullValuesEnabled)
                for (DYModule m :
                        dyml.getAllLoaded()) {
                    utils.removeNullValues(m.getValues());
                }

            if (dyml.isTrimCommentsEnabled)
                for (DYModule m :
                        dyml.getAllLoaded()) {
                    utils.trimComments(m.getComments());
                    utils.trimValuesComments(m.getValues());
                }

        }

        // Update the inEditModules values and their parent/child modules.
        // This is done, because these modules may have only default values set.
        if (!dyml.getAllLoaded().isEmpty())
            for (DYModule inEditM :
                    dyml.getAllInEdit()) {
                DYModule loadedM = utils.getExisting(inEditM, dyml.getAllLoaded());
                inEditM.setValues(loadedM.getValues());
                inEditM.setParentModule(loadedM.getParentModule());
                inEditM.setChildModules(loadedM.getChildModules());
            }

        timer.stop();
        debug.log(this, "Finished reading, took " + timer.getFormattedMillis() + "ms or " + timer.getFormattedSeconds() + "s");
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
