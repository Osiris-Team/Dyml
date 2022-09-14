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
import com.osiris.dyml.utils.UtilsTimeStopper;
import com.osiris.dyml.utils.UtilsYamlSection;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Responsible for reading the provided file/stream and parsing it into modules.
 */
class YamlReader {
    /**
     * A list that only contains already read lines, that contain a key. <br>
     * Is used when working with regular modules <br>
     */
    private final List<DYLine> keyLinesList = new ArrayList<>();
    private DYDebugLogger debug;
    private DYLine beforeLine;
    private int countEmptyBeforeLines = 0;
    /**
     * Gets set at the end of {@link #parseFirstLine(Yaml, DYLine)} and {@link #parseLine(Yaml, DYLine)}.
     */
    private YamlSection beforeModule;

    public void parse(Yaml yaml) throws YamlReaderException, IOException, IllegalListException {
        this.debug = yaml.debugLogger;

        UtilsTimeStopper timer = new UtilsTimeStopper();
        timer.start();

        BufferedReader reader = null;
        try {
            if (yaml.file != null) {
                if (!yaml.file.exists()) throw new YamlReaderException("File '" + yaml.file + "' doesn't exist!");
                reader = new BufferedReader(new FileReader(yaml.file));
                debug.log(this, "Started reading yaml from file '" + yaml.file + "'");
            }
            if (yaml.inputStream != null) {
                reader = new BufferedReader(new InputStreamReader(yaml.inputStream));
                debug.log(this, "Started reading yaml from InputStream '" + yaml.inputStream + "'");
            }
            if (yaml.inString != null) {
                reader = new BufferedReader(new StringReader(yaml.inString));
                debug.log(this, "Started reading yaml from String '" + yaml.inString + "'");
            }
            if (reader == null) {
                debug.log(this, "File and InputStream are both null. Nothing to read/load yaml from!");
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

            UtilsYamlSection utils = new UtilsYamlSection();
            // Set isInsideQuotes for values
            for (YamlSection m :
                    yaml.getAllLoaded()) {
                for (SmartString value : m.getValues()) {
                    value.isInsideQuotes = utils.isEncapsulatedInQuotes(value.asString());
                }
            }

            // Do post processing if enabled
            if (yaml.isPostProcessingEnabled) {

                if (yaml.isTrimLoadedValuesEnabled)
                    for (YamlSection m :
                            yaml.getAllLoaded()) {
                        utils.trimValues(m.getValues());
                    }

                if (yaml.isRemoveQuotesFromLoadedValuesEnabled)
                    for (YamlSection m :
                            yaml.getAllLoaded()) {
                        utils.removeQuotesFromValues(m.getValues());
                    }

                if (yaml.isRemoveLoadedNullValuesEnabled)
                    for (YamlSection m :
                            yaml.getAllLoaded()) {
                        utils.removeNullValues(m.getValues());
                    }

                if (yaml.isTrimCommentsEnabled)
                    for (YamlSection m :
                            yaml.getAllLoaded()) {
                        utils.trimComments(m.getComments());
                        utils.trimComments(m.getSideComments());
                    }

            }

            // Update the inEditModules values and their parent/child modules.
            // This is done, because these modules may have only default values set.
            if (!yaml.getAllLoaded().isEmpty())
                for (YamlSection inEditM :
                        yaml.getAllInEdit()) {
                    YamlSection loadedM = utils.getExisting(inEditM, yaml.getAllLoaded());
                    inEditM.setValues(loadedM.getValues());
                    inEditM.setParentSection(loadedM.getParentSection());
                    inEditM.setChildSections(loadedM.getChildSections());
                }

            timer.stop();

            debug.log(this, "Loaded modules details:");
            for (YamlSection loadedModule :
                    yaml.getAllLoaded()) {

                debug.log(this, "");
                debug.log(this, "---> " + loadedModule.toPrintString());
                if (loadedModule.getParentSection() != null)
                    debug.log(this, "PARENT -> " + loadedModule.getParentSection().toPrintString());
                else
                    debug.log(this, "PARENT -> NULL");

                for (YamlSection childModule :
                        loadedModule.getChildSections()) {
                    if (childModule != null)
                        debug.log(this, "CHILD -> " + childModule.toPrintString());
                    else
                        debug.log(this, "CHILD -> NULL");
                }
            }
            debug.log(this, "");
            debug.log(this, "Finished reading, took " + timer.getFormattedMillis() + "ms or " + timer.getFormattedSeconds() + "s");
        } catch (YamlReaderException | IOException | IllegalListException e) {
            if (yaml.file != null || yaml.inString != null) reader.close();
            throw e;
        } finally {
            if (yaml.file != null || yaml.inString != null) reader.close();
        }
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
                    // Since we support side comments keep this in mind:
                    // key: value# # Side-Comment
                    // First # belongs to the value and not the side comment
                    line.setCommentFound(true);
                    boolean anotherHashtagFound = false;
                    for (int i = charCodePos + 1; i < line.getFullLine().length(); i++) {
                        if (line.getFullLine().codePointAt(i) == 35) {
                            anotherHashtagFound = true;
                            line.setRawComment(line.getFullLine().substring(i + 1));
                            // Since we got side comments, also remove this from the value
                            if (line.getRawValue() != null)
                                if (line.isKeyFound())
                                    line.setRawValue(line.getFullLine().substring(line.getKeyFoundPos() + 1, i));
                                else if (line.isHyphenFound())
                                    line.setRawValue(line.getFullLine().substring(line.getHyphenFoundPos() + 1, i));
                        }
                    }
                    if (!anotherHashtagFound) {
                        line.setRawComment(line.getFullLine().substring(charCodePos + 1));
                        // Since we got side comments, also remove this from the value
                        if (line.getRawValue() != null)
                            if (line.isKeyFound())
                                line.setRawValue(line.getFullLine().substring(line.getKeyFoundPos() + 1, charCodePos));
                            else if (line.isHyphenFound())
                                line.setRawValue(line.getFullLine().substring(line.getHyphenFoundPos() + 1, charCodePos));
                    }
                }
                break;
            }
            case 58: // :
                // Colon enables us to define a key and a value.
                // Note that the next char must be a space for this to be a key.
                line.setCharFound(true);
                if(line.isKeyFound()) break; // Skip if the key was already found.
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
                if (!line.isHyphenFound()) {
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
                }
                break;
            }
            default:
                // Any other charCode than above will count as word
                line.setCharFound(true);
        }
    }

    public void parseFirstLine(Yaml yaml, DYLine currentLine) throws IllegalListException {
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
            YamlSection module = new YamlSection(yaml);
            if (currentLine.isCommentFound()) {
                if (currentLine.isKeyFound()) { // It's a side comment, so we add the comment to the value
                    module.setKeys(currentLine.getRawKey())
                            .setValues(new SmartString(currentLine.getRawValue()));
                    module.addSideComments(currentLine.getRawComment());
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
     * Parses the provided {@link DYLine} into a {@link YamlSection} depending on its content. <br>
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
    public void parseLine(Yaml yaml, DYLine currentLine) {

        if (currentLine.getFullLine().trim().isEmpty()) {
            countEmptyBeforeLines++;
            return;
        }
        debug.log(this, "Reading line '" + currentLine.getLineNumber() + "' with content: '" + currentLine.getFullLine() + "'");

        // Add the module to the yaml loaded modules list, but only under certain circumstances (logic below)
        List<YamlSection> allLoaded = yaml.getAllLoaded();
        YamlSection module = new YamlSection(yaml);
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
                    module.setCountTopLineBreaks(countEmptyBeforeLines);
                    countEmptyBeforeLines = 0;
                }

                // Go reversely through the lines list, search for the parent and copy its keys.
                // It can be that this is a G0 module. In that case we don't need to search for a parent.
                if (currentLine.getCountSpaces() > 0) {
                    for (int i = keyLinesList.size() - 1; i >= 0; i--) {
                        DYLine oldLine = keyLinesList.get(i);
                        if ((currentLine.getCountSpaces() - oldLine.getCountSpaces()) == 2) {
                            YamlSection oldModule = allLoaded.get(i);
                            module.getKeys().addAll(oldModule.getKeys());
                            module.setParentSection(oldModule);
                            oldModule.addChildSections(module);
                            break;
                        }
                    }
                }

                module.addKeys(currentLine.getRawKey());
                module.setValues(new SmartString(currentLine.getRawValue()));
                module.addSideComments(currentLine.getRawComment());
                allLoaded.add(module);
            } else if (currentLine.isHyphenFound()) { // Comment + Hyphen found without a key
                // Its a side comment from a value in a list. Also add support for value top comments inside a list. Example:
                // list:
                //   - value # value-comment  <---
                //   # value-comment of the value below, not a key-comment, bc inside of a list
                //   - second value # value-comment <---
                YamlSection oldModule = yaml.getLastLoadedModule(); // The last added module, which has to contain a key, otherwise its not added
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
                oldModule.addValues(new SmartString(currentLine.getRawValue()));
                oldModule.addSideComments(currentLine.getRawComment());
            } else { // No side-comment, but regular comment
                // If the current line and the last line are comments, add the current comment to the last comments object/module.
                // In both cases, don't add the module to the list.
                // The module gets added to the list, once a key was found in the next lines.
                if (beforeLine.isCommentFound() && !beforeLine.isKeyFound() && !beforeLine.isHyphenFound()) // To make sure its not a side-comment
                    module = beforeModule;
                else {
                    module.setCountTopLineBreaks(countEmptyBeforeLines);
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
                module.setCountTopLineBreaks(countEmptyBeforeLines);
                countEmptyBeforeLines = 0;
            }

            // Go reversely through the lines list, search for the parent and copy its keys.
            // It can be that this is a G0 module. In that case we don't need to search for a parent.
            if (currentLine.getCountSpaces() > 0) {
                for (int i = keyLinesList.size() - 1; i >= 0; i--) {
                    DYLine oldLine = keyLinesList.get(i);
                    if ((currentLine.getCountSpaces() - oldLine.getCountSpaces()) == 2) {
                        YamlSection oldModule = allLoaded.get(i);
                        module.getKeys().addAll(oldModule.getKeys());
                        module.setParentSection(oldModule);
                        oldModule.addChildSections(module);
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
            YamlSection oldModule = yaml.getLastLoadedModule();
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
        } else { // CURRENT LINE DOES NOT CONTAIN A COMMENT OR A KEY OR A HYPHEN! Multiple examples:
            // m1: value1
            // value2 <---

            // m1:
            //   m1-inside: value1
            //              value2 <---
            // value3 <---
            //      value4 <---
            // All those values are actually part of value1, so we need to append the content of this line to the last modules, last value.
            SmartString lastValue = yaml.getLastLoadedModule().getLastValue();
            if (lastValue.asString() == null)
                lastValue.set(currentLine.getFullLine()); // Note that we don't call currentLine.getRawValue() because that only gets set if there was a ':'
            else
                lastValue.set(lastValue.asString() + "\n" + currentLine.getFullLine());
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
