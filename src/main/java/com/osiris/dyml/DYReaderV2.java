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
import java.util.List;

/**
 * Responsible for reading the provided file/stream and parsing it into modules.
 */
class DYReaderV2 {
    private DYDebugLogger debug;
    private int countEmptyBeforeLines = 0;
    private List<DYModule> loadedModules;

    private DYModule lastModule;
    // Last lines information: (use fields instead of an actual line object bc of performance)
    private String lastFullLine;
    private int lastLineNumber;
    private char[] lastFullLineAsChar;
    private int lastCountSpaces;
    private boolean lastCommentFound;
    private boolean lastKeyFound;
    private int lastKeyFoundPos;
    private boolean lastHyphenFound;
    private int lastHyphenFoundPos;
    private boolean lastCharFound;
    private String lastRawKey;
    private String lastRawValue;
    private String lastRawComment;

    private DYModule module;
    // Current lines information:
    private String fullLine;
    private int lineNumber;
    private char[] fullLineAsChar;
    private int countSpaces;
    private boolean commentFound;
    private boolean keyFound;
    private int keyFoundPos;
    private boolean hyphenFound;
    private int hyphenFoundPos;
    private boolean charFound;
    private String rawKey;
    private String rawValue;
    private String rawComment;

    public void parse(DreamYaml yaml) throws DYReaderException, IOException, IllegalListException {
        this.debug = yaml.debugLogger;

        UtilsTimeStopper timer = new UtilsTimeStopper();
        timer.start();

        BufferedReader reader = null;
        if (yaml.file != null) {
            if (!yaml.file.exists()) throw new DYReaderException("File '" + yaml.file + "' doesn't exist!");
            reader = new BufferedReader(new FileReader(yaml.file));
            debug.log(this, "Started reading yaml from file '"+yaml.file+"'");
        }
        if (yaml.inputStream!=null){
            reader = new BufferedReader(new InputStreamReader(yaml.inputStream));
            debug.log(this, "Started reading yaml from InputStream '"+yaml.inputStream+"'");
        }
        if (yaml.inString !=null){
            reader = new BufferedReader(new StringReader(yaml.inString));
            debug.log(this, "Started reading yaml from String '"+yaml.inString +"'");
        }
        if (reader==null){
            System.out.println("File and InputStream are both null. Nothing to read/load yaml from!");
            return;
        }
        loadedModules = yaml.getAllLoaded();
        loadedModules.clear();


        // Parse the first line manually, so that the beforeLine is NOT null, and we don't have to check it every time
        String firstLine = reader.readLine();
        if (firstLine == null)
            firstLine = "";
        DYLine firstDyLine = new DYLine(firstLine, lineNumber);
        parseFirstLine(yaml, firstDyLine); // beforeModule gets set here at the end
        if (firstDyLine.isKeyFound())
            keyLinesList.add(firstDyLine);
        beforeLine = firstDyLine;

        StringBuilder line = new StringBuilder(100); // Max line length of 10k chars
        int lineIndex = 0;
        int cBefore = 10; // \n
        int c;
        try{
            while (true){ // Use this special loop to avoid an actual while loop, to increase read speed
                for (int i = 0; i < Integer.MAX_VALUE; i++) {
                    c = reader.read(); // Read char by char until the end
                    line.append((char) c);
                    switch (c){ // Instead of if bc of performance
                        // The cases below are sorted from probably most abundant char in the file to least
                        case 32: // ' ' Empty space before a colon, indicates objects tree position. Increment until a char is found.
                            if (!charFound) countSpaces++;
                            else if (cBefore==58) { // ': ' Colon with a space, enables us to define a key and a value.
                                keyFound = true;
                                module.addKeys(emptyToNull(line.toString()));
                                module.addValues(emptyToNull(reader.readLine())); // Read the rest of the line
                                c = 10; // \n

                                // This line does NOT contain a HYPHEN, but HAS a KEY. Example:
                                // m1:
                                //   # BeforeLine comment
                                //   m2: value <---
                                if (lastCommentFound && !lastKeyFound && !lastHyphenFound) // If the current line is a key, but the last line was a comment, add the key to the last comments module.
                                    module = lastModule;
                                else {
                                    module.setCountTopSpaces(countEmptyBeforeLines);
                                    countEmptyBeforeLines = 0;
                                }

                                // Go reversely through the lines list, search for the parent and copy its keys.
                                // It can be that this is a G0 module. In that case we don't need to search for a parent.
                                if (currentLine.getCountSpaces() > 0) {
                                    for (int i = keyLinesList.size() - 1; i >= 0; i--) {
                                        DYLine oldLine = keyLinesList.get(i);
                                        if ((currentLine.getCountSpaces() - oldLine.getCountSpaces()) == 2) {
                                            DYModule oldModule = allLoaded.get(i);
                                            module.getKeys().addAll(oldModule.getKeys());
                                            module.setParentModule(oldModule);
                                            oldModule.addChildModules(module);
                                            break;
                                        }
                                    }
                                }

                                module.addKeys(currentLine.getRawKey());
                                module.setValues(currentLine.getRawValue());
                                allLoaded.add(module);
                            }
                            break;


                        case 35: { // '#' Hashtag indicates start of a comment but only if the char before didn't exist or it was a space
                            charFound = true;
                            if (cBefore == 10 || cBefore == 32) {
                                String comment = emptyToNull(reader.readLine()); // Read the rest of the line
                                c = 10; // \n
                                if (keyFound || hyphenFound)  // Means that this is a side-comment
                                    module.getLastValue().setComment(comment);
                                else
                                    module.addComments(comment);
                            }
                            break;
                        }


                        case 45: { // '-' Hyphen indicates a list object but only if the char before didn't exist or it was a space
                            charFound = true;
                            if ((cBefore == 32 || cBefore == 10)
                                    && !keyFound) { // To avoid hyphens inside values
                                String value = emptyToNull(reader.readLine()); // Read the rest of the line
                                c = 10; // \n
                                module.addValues(value);
                            }
                            break;
                        }

                        case 10: // '\n' (aka next line char, aka end of the current line)
                            charFound = true;
                            if (cBefore==10 || cBefore==32) countEmptyBeforeLines++;
                            line = new StringBuilder(100);
                            break;


                        case -1: // 'EOF' (end of file "char")
                            throw new EOFException(); // Do this bc then of performance


                        default:
                            // Any other charCode than above will count as word
                            charFound = true;
                    }
                    lineIndex++;
                    cBefore = c;
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
        if (yaml.isPostProcessingEnabled) {

            if (yaml.isTrimLoadedValuesEnabled)
                for (DYModule m :
                        yaml.getAllLoaded()) {
                    utils.trimValues(m.getValues());
                }

            if (yaml.isRemoveQuotesFromLoadedValuesEnabled)
                for (DYModule m :
                        yaml.getAllLoaded()) {
                    utils.removeQuotesFromValues(m.getValues());
                }

            if (yaml.isRemoveLoadedNullValuesEnabled)
                for (DYModule m :
                        yaml.getAllLoaded()) {
                    utils.removeNullValues(m.getValues());
                }

            if (yaml.isTrimCommentsEnabled)
                for (DYModule m :
                        yaml.getAllLoaded()) {
                    utils.trimComments(m.getComments());
                    utils.trimValuesComments(m.getValues());
                }

        }

        // Update the inEditModules values and their parent/child modules.
        // This is done, because these modules may have only default values set.
        if (!yaml.getAllLoaded().isEmpty())
            for (DYModule inEditM :
                    yaml.getAllInEdit()) {
                DYModule loadedM = utils.getExisting(inEditM, yaml.getAllLoaded());
                inEditM.setValues(loadedM.getValues());
                inEditM.setParentModule(loadedM.getParentModule());
                inEditM.setChildModules(loadedM.getChildModules());
            }

        timer.stop();
        debug.log(this, "Finished reading, took " + timer.getFormattedMillis() + "ms or " + timer.getFormattedSeconds() + "s");
    }


    public void parseFirstLine(DreamYaml yaml, DYLine currentLine) throws IllegalListException {
        if (!currentLine.getFullLine().isEmpty()) {
            debug.log(this, "Reading line '" + currentLine.getLineNumber() + "' with content: '" + currentLine.getFullLine() + "'");
            // Go thorough each character of the string, until a special one is found
            int charCode;
            for (int i = 0; i < currentLine.getFullLine().length(); i++) {
                charCode = currentLine.getFullLine().codePointAt(i);
                checkChar(currentLine, charCode, i);
                if (currentLine.isCommentFound()) // Not at currentLine.isColonFound() or currentLine.isHyphenFound() because of side-comments
                    break;
            }

            // In comparison to parseLine we got a lot less stuff to check.
            DYModule module = new DYModule(yaml);
            if (currentLine.isCommentFound()) {
                if (currentLine.isKeyFound()) { // Its a side comment, so we add the comment to the value
                    module.setKeys(currentLine.getRawKey())
                            .setValues(new DYValue(currentLine.getRawValue(), currentLine.getRawComment()));
                    yaml.getAllLoaded().add(module);
                } else if (currentLine.isHyphenFound()) { // Its a side comment, so we add of a value in a list
                    throw new IllegalListException((yaml.getInputStream() == null ? yaml.getFile().getName() : "<InputStream>"), currentLine);
                } else { // Regular comment, so add it to the module
                    module.addComments(currentLine.getRawComment());
                    // DOES NOT get added to the loaded list, until a key was found
                }
            } else if (currentLine.isKeyFound()) {
                module.setKeys(currentLine.getRawKey())
                        .setValues(currentLine.getRawValue());
                yaml.getAllLoaded().add(module);
            } else if (currentLine.isHyphenFound()) {
                throw new IllegalListException((yaml.getInputStream() == null ? yaml.getFile().getName() : "<InputStream>"), currentLine);
            }

            beforeModule = module;
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
