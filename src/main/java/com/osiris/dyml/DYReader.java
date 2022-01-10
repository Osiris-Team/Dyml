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
import java.util.Date;
import java.util.List;

/**
 * Responsible for reading the provided file/stream and parsing it into modules.
 */
class DYReader {
    private DYDebugLogger debug;
    /**
     * A list that only contains already read lines, that contain a key. <br>
     * Is used when working with regular modules <br>
     */
    private final List<DYLine> keyLinesList = new ArrayList<>();
    private DYLine beforeLine;
    private int countEmptyBeforeLines = 0;
    /**
     * Gets set at the end of {@link #parseFirstLine(DreamYaml, DYLine)} and {@link #parseLine(DreamYaml, DYLine)}.
     */
    private DYModule beforeModule;

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
        if (yaml.inputString!=null){
            reader = new BufferedReader(new StringReader(yaml.inputString));
            debug.log(this, "Started reading yaml from String '"+yaml.inputString+"'");
        }
        if (reader==null){
            System.out.println("File and InputStream are both null. Nothing to read/load yaml from!");
            return;
        }
        yaml.getAllLoaded().clear();


        int lineNumber = 1; // Start at 1 because it makes more sense. This number is only used to display the line number in exceptions and has no effect on important stuff.

        // Parse the first line manually, so that the beforeLine is NOT null, and we don't have to check it every time
        String firstLine = reader.readLine();
        if (firstLine == null)
            firstLine = "";
        DYLine firstDyLine = new DYLine(firstLine, lineNumber);
        parseFirstLine(yaml, firstDyLine); // beforeModule gets set here at the end
        if (firstDyLine.isKeyFound())
            keyLinesList.add(firstDyLine);
        beforeLine = firstDyLine;
        lineNumber++;

        String line;
        while ((line = reader.readLine()) != null) {
            String finalLine = line; // Its important, that a new, unique Object is created for each line and number
            int finalLineNumber = lineNumber; // Its important, that a new, unique Object is created for each line and number
            DYLine dyLine = new DYLine(finalLine, finalLineNumber);
            parseLine(yaml, dyLine); // beforeModule gets set here at the end
            if (dyLine.isKeyFound())
                // the size of the loadedModules list, and this list, stay the same.
                keyLinesList.add(dyLine);
            beforeLine = dyLine;
            lineNumber++;
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
        debug.log(this, "Loaded modules details:");
        for (DYModule loadedModule :
                yaml.getAllLoaded()) {

            debug.log(this, "");
            debug.log(this, "---> " + loadedModule.getModuleInformationAsString());
            if (loadedModule.getParentModule() != null)
                debug.log(this, "PARENT -> " + loadedModule.getParentModule().getModuleInformationAsString());
            else
                debug.log(this, "PARENT -> NULL");

            for (DYModule childModule :
                    loadedModule.getChildModules()) {
                if (childModule != null)
                    debug.log(this, "CHILD -> " + childModule.getModuleInformationAsString());
                else
                    debug.log(this, "CHILD -> NULL");
            }
        }
        debug.log(this, "");
        debug.log(this, "Finished reading of " + (yaml.getInputStream() == null ? yaml.getFile().getName() : "InputStream") + " at " + new Date());
        debug.log(this, "Operation took " + timer.getFormattedMillis() + "ms or " + timer.getFormattedSeconds() + "s");
    }

    /**
     * Checks the provided charCode and adds information
     * like, if the line contains a hashtag, to the provided {@link DYLine} object.
     *
     * @param line        this object receives information about this charCode.
     * @param charCode    the charCode to interpret.
     * @param charCodePos the chars/charCodes index position in the full line.
     */
    public void checkChar(DYLine line, int charCode, int charCodePos) {
        switch (charCode) {
            case 32: //
                // Empty space before a colon, indicates objects tree position. Increment until a char is found.
                if (!line.isCharFound())
                    line.setCountSpaces(line.getCountSpaces() + 1);
                break;
            case 35: { // #
                // Hashtag indicates start of a comment but only if the char before didn't exist or it was a space
                line.setCharFound(true);
                int charCodeBefore = 0;
                try {
                    charCodeBefore = line.getFullLine().codePointAt(charCodePos - 1);
                    // This may fail if we are at the last/first char.
                } catch (Exception ignored) {
                } // Don't use an if statement to avoid, bc this is more efficient.
                if (charCodeBefore == 0 || charCodeBefore == 32) {
                    line.setCommentFound(true);
                    line.setRawComment(line.getFullLine().substring(charCodePos + 1));
                    // Since we got side comments, also remove this from the value
                    if (line.getRawValue() != null)
                        if (line.isKeyFound())
                            line.setRawValue(line.getFullLine().substring(line.getKeyFoundPos() + 1, charCodePos));
                        else if (line.isHyphenFound())
                            line.setRawValue(line.getFullLine().substring(line.getHyphenFoundPos() + 1, charCodePos));
                }
                break;
            }
            case 58: // :
                // Colon enables us to define a key and a value.
                // Note that the next char must be a space for this to be a key.
                line.setCharFound(true);
                int charCodeNext = 0;
                try {
                    charCodeNext = line.getFullLine().codePointAt(charCodePos + 1); // This may fail if we are at the last/first char.
                } catch (Exception ignored) {
                } // Don't use an if statement to avoid, bc this is more efficient.
                if (charCodeNext == 32 || charCodeNext == 0) {
                    line.setKeyFound(true);
                    line.setKeyFoundPos(charCodePos);
                    line.setRawKey(line.getFullLine().substring(line.getCountSpaces(), charCodePos));
                    line.setRawValue(emptyToNull(line.getFullLine().substring(charCodePos + 1)));
                }
                break;
            case 45: { // -
                // Hyphen indicates a list object but only if the char before didn't exist or it was a space
                line.setCharFound(true);
                int charCodeBefore = 0;
                try {
                    charCodeBefore = line.getFullLine().codePointAt(charCodePos - 1); // This may fail if we are at the last/first char.
                } catch (Exception ignored) {
                } // Don't use an if statement to avoid, bc this is more efficient.
                if ((charCodeBefore == 32 || charCodeBefore == 0)
                        && !line.isKeyFound()) { // To avoid hyphens inside values
                    line.setHyphenFound(true);
                    line.setHyphenFoundPos(charCodePos);
                    line.setRawValue(emptyToNull(line
                            .getFullLine()
                            .substring(charCodePos + 1)));
                }
                break;
            }
            default:
                // Any other charCode than above will count as word
                line.setCharFound(true);
        }
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
     * Parses the provided {@link DYLine} into a {@link DYModule} depending on its content. <br>
     * Note that the provided {@link DYLine}s content gets checked (char by char) and its information updated,
     * before parsing anything. <br>
     * DEV NOTE: <br>
     * When I talk about generations, I mean a modules horizontal position in the yaml file. <br>
     * We can find out a modules generation, by looking at its count of spaces. <br>
     * If a module has 0 spaces, its in G0 (generation 0), if it has 2 spaces its in G1, and so on... <br>
     * Example: <br>
     * <pre>
     * key1: value     # G0 | Parent of key2 | Count of spaces: 0
     *   key2: value   # G1 | Child of key1 | Count of spaces: 2
     * </pre>
     */
    public void parseLine(DreamYaml yaml, DYLine currentLine) {

        if (currentLine.getFullLine().trim().isEmpty()) {
            countEmptyBeforeLines++;
            return;
        }
        debug.log(this, "Reading line '" + currentLine.getLineNumber() + "' with content: '" + currentLine.getFullLine() + "'");

        // Add the module to the yaml loaded modules list, but only under certain circumstances (logic below)
        List<DYModule> allLoaded = yaml.getAllLoaded();
        DYModule module = new DYModule(yaml);
        // Go thorough each character of the string, until a special one is found
        int charCode;
        String fullLine = currentLine.getFullLine();
        for (int i = 0; i < fullLine.length(); i++) {
            charCode = fullLine.codePointAt(i);
            checkChar(currentLine, charCode, i);
            if (currentLine.isCommentFound()) // Not at currentLine.isColonFound() or currentLine.isHyphenFound() because of side-comments
                break;
        }

        // The code below does all the magic, of reading the keys, values and comments, and putting them together into modules.
        // "<---" in the examples, shows that this could be the current line
        if (currentLine.isCommentFound()) {
            if (currentLine.isKeyFound()) {
                // This line does NOT contain a HYPHEN, but HAS a KEY. Example:
                // m1:
                //   # Key-Comment
                //   m2: value # Side-Comment <---

                if (beforeLine.isCommentFound() && !beforeLine.isKeyFound() && !beforeLine.isHyphenFound()) // If the current line is a key, but the last line was a comment, add the key to the last comments module.
                    module = beforeModule;
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
                module.setValues(new DYValue(currentLine.getRawValue(), currentLine.getRawComment()));
                allLoaded.add(module);
            } else if (currentLine.isHyphenFound()) { // Comment + Hyphen found without a key
                // Its a side comment from a value in a list. Also add support for value top comments inside a list. Example:
                // list:
                //   - value # value-comment  <---
                //   # value-comment of the value below, not a key-comment, bc inside of a list
                //   - second value # value-comment <---
                DYModule oldModule = yaml.getLastLoadedModule(); // The last added module, which has to contain a key, otherwise its not added
                if (beforeLine.isCommentFound() && !beforeLine.isKeyFound() && !beforeLine.isHyphenFound()) { // In this special case, we put the comments from the last line/module together
                    String c = currentLine.getRawComment();
                    for (String comment :
                            beforeModule.getComments()) {
                        c = c + " # " + comment;
                    }
                    currentLine.setRawComment(c);
                }
                // Since the key has a null value we need to remove it. Example:
                // list: <--- This is the first null value
                //   - value <--- This is our current position

                // If we wouldn't remove it, the writer would write that value in form of a list. Example:
                // list:   <--- New null value
                //   -     <--- Old null value
                //   - value

                // If we would then read the read the above again, the same would happen.
                // Its basically a infinite loop of adding null values to a list.
                // That's why the code below is very important:
                if (!beforeLine.isHyphenFound() && oldModule.getValues().size() == 1 && oldModule.getValues().get(0).asString() == null)
                    oldModule.getValues().remove(0);

                // Since the allLoaded lists and keyLinesList sizes are the same we can do the below:
                oldModule.addValues(new DYValue(currentLine.getRawValue(), currentLine.getRawComment()));
            } else { // No side-comment, but regular comment
                // If the current line and the last line are comments, add the current comment to the last comments object/module.
                // In both cases, don't add the module to the list.
                // The module gets added to the list, once a key was found in the next lines.
                if (beforeLine.isCommentFound() && !beforeLine.isKeyFound() && !beforeLine.isHyphenFound()) // To make sure its not a side-comment
                    module = beforeModule;
                else {
                    module.setCountTopSpaces(countEmptyBeforeLines);
                    countEmptyBeforeLines = 0;
                }
                module.addComments(currentLine.getRawComment());
            }
        } else if (currentLine.isKeyFound()) { // CURRENT LINE DOES NOT CONTAIN A COMMENT!
            // This line does NOT contain a HYPHEN, but HAS a KEY. Example:
            // m1:
            //   # BeforeLine comment
            //   m2: value <---

            if (beforeLine.isCommentFound() && !beforeLine.isKeyFound() && !beforeLine.isHyphenFound()) // If the current line is a key, but the last line was a comment, add the key to the last comments module.
                module = beforeModule;
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
        } else if (currentLine.isHyphenFound()) { // CURRENT LINE DOES NOT CONTAIN A COMMENT OR A KEY! Multiple examples:
            // m1:
            //   - value1
            //   - value2 <---

            // m1:
            //   m1-inside:
            //     - value1
            //     - value2 <---
            DYModule oldModule = yaml.getLastLoadedModule();
            if (beforeLine.isCommentFound() && !beforeLine.isKeyFound() && !beforeLine.isHyphenFound()) { // In this special case, we put the comments from the last line/module together
                String c = currentLine.getRawComment();
                for (String comment :
                        beforeModule.getComments()) {
                    c = c + " # " + comment;
                }
                currentLine.setRawComment(c);
            }
            if (!beforeLine.isHyphenFound() && oldModule.getValues().size() == 1 && oldModule.getValues().get(0).asString() == null) {
                oldModule.getValues().remove(0);
            }
            oldModule.addValues(currentLine.getRawValue()); // Now all we do is add the current value to the parent module.
        }

        beforeModule = module;
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
