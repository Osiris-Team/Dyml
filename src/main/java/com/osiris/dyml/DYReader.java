/*
 *  Copyright Osiris Team
 *  All rights reserved.
 *
 *  This software is licensed work.
 *  Please consult the file "LICENSE" for details.
 */

package com.osiris.dyml;


import com.osiris.dyml.exceptions.DYReaderException;
import com.osiris.dyml.exceptions.DuplicateKeyException;
import com.osiris.dyml.exceptions.IllegalListException;
import com.osiris.dyml.utils.UtilsDYModule;
import com.osiris.dyml.utils.UtilsTimeStopper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Responsible for reading the provided file and parsing it into modules.
 */
class DYReader {
    /**
     * A list that only contains already read lines, that contain a key. <br>
     * Is used when working with regular modules <br>
     */
    private final List<DYLine> keyLinesList = new ArrayList<>();
    private BufferedReader reader;
    private DYLine beforeLine;
    /**
     * Gets set at the end of {@link #parseFirstLine(DreamYaml, DYLine)} and {@link #parseLine(DreamYaml, DYLine)}.
     */
    private DYModule beforeModule;

    public void parse(DreamYaml yaml) throws DYReaderException, IOException, IllegalListException, DuplicateKeyException {
        UtilsTimeStopper timer = new UtilsTimeStopper();
        timer.start();
        if (yaml.isDebugEnabled()) {
            System.out.println();
            System.out.println("Started loading yaml file: " + yaml.getFile().getName() + " at " + new Date());
        }

        File file = yaml.getFile();
        if (file == null) throw new DYReaderException("File is null! Make sure to load it at least once!");
        if (!file.exists()) throw new DYReaderException("File '" + file.getName() + "' doesn't exist!");

        reader = new BufferedReader(new FileReader(file));
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
        if (yaml.isPostProcessingEnabled()) {

            if (yaml.isTrimLoadedValuesEnabled())
                for (DYModule m :
                        yaml.getAllLoaded()) {
                    utils.trimValues(m.getValues());
                }

            if (yaml.isRemoveQuotesFromLoadedValuesEnabled())
                for (DYModule m :
                        yaml.getAllLoaded()) {
                    utils.removeQuotesFromValues(m.getValues());
                }

            if (yaml.isRemoveLoadedNullValuesEnabled())
                for (DYModule m :
                        yaml.getAllLoaded()) {
                    utils.removeNullValues(m.getValues());
                }

            if (yaml.isTrimCommentsEnabled())
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
                inEditM.setParentModules(loadedM.getParentModules());
                inEditM.setChildModules(loadedM.getChildModules());
            }

        timer.stop();
        if (yaml.isDebugEnabled()) {
            System.out.println();
            System.out.println("Finished parsing of " + yaml.getFile().getName() + " at " + new Date());
            System.out.println("Operation took " + timer.getFormattedMillis() + "ms or " + timer.getFormattedSeconds() + "s");
            System.out.println("Loaded modules details:");
            for (DYModule loadedModule :
                    yaml.getAllLoaded()) {

                System.out.println();
                System.out.println("---> " + loadedModule.getModuleInformationAsString());
                for (DYModule parentModule :
                        loadedModule.getParentModules()) {
                    if (parentModule != null)
                        System.out.println("PARENT -> " + parentModule.getModuleInformationAsString());
                    else
                        System.out.println("PARENT -> NULL");
                }

                for (DYModule childModule :
                        loadedModule.getChildModules()) {
                    if (childModule != null)
                        System.out.println("CHILD -> " + childModule.getModuleInformationAsString());
                    else
                        System.out.println("CHILD -> NULL");
                }
            }
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
                } catch (Exception e) {
                } // Don't use an if statement to avoid, bc this is more efficient.
                if (charCodeBefore == 0 || charCodeBefore == 32) {
                    line.setCommentFound(true);
                    line.setRawComment(line.getFullLine().substring(charCodePos + 1));
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

    public void parseFirstLine(DreamYaml yaml, DYLine currentLine) throws DuplicateKeyException, IllegalListException {
        if (!currentLine.getFullLine().isEmpty()) {
            if (yaml.isDebugEnabled())
                System.out.println("Reading first line '" + currentLine.getLineNumber() + "' with content: '" + currentLine.getFullLine() + "'");
            // Go thorough each character of the string, until a special one is found
            int charCode = 0;
            for (int i = 0; i < currentLine.getFullLine().length(); i++) {
                charCode = currentLine.getFullLine().codePointAt(i);
                checkChar(currentLine, charCode, i);
                if (currentLine.isCommentFound()) // Not at currentLine.isColonFound() or currentLine.isHyphenFound() because of side-comments
                    break;
            }

            // In comparison to parseLine we got a lot less stuff to check.
            DYModule module = new DYModule();
            if (currentLine.isCommentFound()) {
                if (currentLine.isKeyFound()) { // Its a side comment, so we add the comment to the value
                    module.setKeys(currentLine.getRawKey())
                            .setValues(new DYValue(currentLine.getRawValue(), currentLine.getRawComment()));
                    yaml.getAllLoaded().add(module);
                } else if (currentLine.isHyphenFound()) { // Its a side comment, so we add of a value in a list
                    throw new IllegalListException(yaml.getFile().getName(), currentLine);
                } else { // Regular comment, so add it to the module
                    module.addComments(currentLine.getRawComment());
                    // DOES NOT get added to the loaded list, until a key was found
                }
            } else if (currentLine.isKeyFound()) {
                module.setKeys(currentLine.getRawKey())
                        .setValues(currentLine.getRawValue());
                yaml.getAllLoaded().add(module);
            } else if (currentLine.isHyphenFound()) {
                throw new IllegalListException(yaml.getFile().getName(), currentLine);
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
    public void parseLine(DreamYaml yaml, DYLine currentLine) throws IllegalListException, DuplicateKeyException {

        if (!currentLine.getFullLine().isEmpty()) {
            if (yaml.isDebugEnabled())
                System.out.println("Reading line '" + currentLine.getLineNumber() + "' with content: '" + currentLine.getFullLine() + "'");

            // Add the module to the yaml loaded modules list, but only under certain circumstances (logic below)
            List<DYModule> allLoaded = yaml.getAllLoaded();
            DYModule module = new DYModule();
            // Go thorough each character of the string, until a special one is found
            int charCode = 0;
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

                    if (beforeLine.isCommentFound() && !beforeLine.isKeyFound()) // If the current line is a key, but the last line was a comment, add the key to the last comments module.
                        module = beforeModule;

                    // Go reversely through the lines list, search for the parent and copy its keys.
                    // It can be that this is a G0 module. In that case we don't need to search for a parent.
                    if (currentLine.getCountSpaces() > 0) {
                        for (int i = keyLinesList.size() - 1; i >= 0; i--) {
                            DYLine oldLine = keyLinesList.get(i);
                            if ((currentLine.getCountSpaces() - oldLine.getCountSpaces()) == 2) {
                                DYModule oldModule = allLoaded.get(i);
                                module.getKeys().addAll(oldModule.getKeys());
                                module.addParentModules(oldModule);
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
                    boolean addedValue = false;
                    for (int i = keyLinesList.size() - 1; i >= 0; i--) {
                        DYLine oldLine = keyLinesList.get(i);
                        if ((currentLine.getCountSpaces() - oldLine.getCountSpaces()) == 2) {
                            DYModule oldModule = allLoaded.get(i);
                            if (beforeLine.isCommentFound() && !beforeLine.isKeyFound()) { // In this special case, we put the comments from the last line/module together
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
                            addedValue = true;
                            break;
                        }
                    }

                    if (!addedValue)
                        throw new IllegalListException(yaml.getFile().getName(), currentLine);
                } else { // No side-comment, but regular comment
                    module.addComments(currentLine.getRawComment());
                    // If the current line and the last line are comments, add the current comment to the last comments object/module.
                    // In both cases, don't add the module to the list.
                    // The module gets added to the list, once a key was found in the next lines.
                    if (beforeLine.isCommentFound() && !beforeLine.isKeyFound()) // To make sure its not a side-comment
                        module = beforeModule;
                    module.addComments(currentLine.getRawComment());
                }
            } else if (currentLine.isKeyFound()) { // CURRENT LINE DOES NOT CONTAIN A COMMENT!
                // This line does NOT contain a HYPHEN, but HAS a KEY. Example:
                // m1:
                //   # BeforeLine comment
                //   m2: value <---

                if (beforeLine.isCommentFound() && !beforeLine.isKeyFound()) // If the current line is a key, but the last line was a comment, add the key to the last comments module.
                    module = beforeModule;

                // Go reversely through the lines list, search for the parent and copy its keys.
                // It can be that this is a G0 module. In that case we don't need to search for a parent.
                if (currentLine.getCountSpaces() > 0) {
                    for (int i = keyLinesList.size() - 1; i >= 0; i--) {
                        DYLine oldLine = keyLinesList.get(i);
                        if ((currentLine.getCountSpaces() - oldLine.getCountSpaces()) == 2) {
                            DYModule oldModule = allLoaded.get(i);
                            module.getKeys().addAll(oldModule.getKeys());
                            module.addParentModules(oldModule);
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
                //   - m1-inside:
                //     - value1
                //     - value2 <---
                boolean addedValue = false;
                for (int i = keyLinesList.size() - 1; i >= 0; i--) {
                    DYLine oldLine = keyLinesList.get(i);
                    if ((currentLine.getCountSpaces() - oldLine.getCountSpaces()) == 2) {
                        DYModule oldModule = allLoaded.get(i);
                        if (beforeLine.isCommentFound() && !beforeLine.isKeyFound()) { // In this special case, we put the comments from the last line/module together
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
                        addedValue = true;
                        break;
                    }
                }

                if (!addedValue)
                    throw new IllegalListException(yaml.getFile().getName(), currentLine);
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
