/*
 *  Copyright Osiris Team
 *  All rights reserved.
 *
 *  This software is licensed work.
 *  Please consult the file "LICENSE" for details.
 */

package com.osiris.dyml;

import com.osiris.dyml.exceptions.YamlWriterException;
import com.osiris.dyml.utils.UtilsTimeStopper;

import java.io.*;
import java.util.Date;
import java.util.List;

/**
 * Responsible for parsing and writing the provided modules.
 */
class YamlWriter {
    private Yaml yaml;

    public void parse(Yaml yaml, boolean overwrite, boolean reset) throws YamlWriterException, IOException {
        this.yaml = yaml;
        DYDebugLogger logger = yaml.debugLogger;
        boolean isDebug = yaml.debugLogger.isEnabled();

        UtilsTimeStopper timer = new UtilsTimeStopper();
        timer.start();

        BufferedWriter writer = null;
        StringWriter stringWriter = null;

        if (yaml.outputStream != null) {
            writer = new BufferedWriter(new OutputStreamWriter(yaml.outputStream), 32768); // TODO compare speed with def buffer
            logger.log(this, "Started writing yaml to file '" + yaml.file + "' with overwrite: " + overwrite + " and reset: " + reset);
        } else if (yaml.file != null) {
            if (!yaml.file.exists()) throw new YamlWriterException("File '" + yaml.file.getName() + "' doesn't exist!");
            writer = new BufferedWriter(new FileWriter(yaml.file), 32768); // TODO compare speed with def buffer
            logger.log(this, "Started writing yaml to OutputStream '" + yaml.outputStream + "' with overwrite: " + overwrite + " and reset: " + reset);
        } else if (yaml.outString != null) {
            stringWriter = new StringWriter();
            writer = new BufferedWriter(stringWriter, 32768); // TODO compare speed with def buffer
            logger.log(this, "Started writing yaml to String '" + yaml.outString + "' with overwrite: " + overwrite + " and reset: " + reset);
        }
        if (writer == null) {
            System.out.println("File and OutputStream are both null. Nothing to write yaml to!");
            return;
        }

        writer.write(""); // Clear old content
        if (reset) return;

        List<YamlSection> modulesToSave;
        if (overwrite) {
            modulesToSave = yaml.getAllInEdit();

        } else
            modulesToSave = yaml.createUnifiedList(yaml.getAllInEdit(), yaml.getAllLoaded());

        if (modulesToSave.isEmpty() && isDebug)
            logger.log(this, "The modules list is empty. Written an empty file.");


        YamlSection lastModule = new YamlSection(yaml); // Create an empty module as start point
        for (YamlSection m :
                modulesToSave) {
            parseModule(writer, m, lastModule);
            lastModule = m;
        }

        timer.stop();
        if (isDebug) {
            logger.log(this, "Finished writing of " + yaml.getFile().getName() + " at " + new Date());
            logger.log(this, "Written unified modules details:");
            for (YamlSection loadedModule :
                    modulesToSave) {

                logger.log(this, "");
                logger.log(this, "---> " + loadedModule.getModuleInformationAsString());
                if (loadedModule.getParentModule() != null)
                    logger.log(this, "PARENT -> " + loadedModule.getParentModule().getModuleInformationAsString());
                else
                    logger.log(this, "PARENT -> NULL");

                for (YamlSection childModule :
                        loadedModule.getChildModules()) {
                    if (childModule != null)
                        logger.log(this, "CHILD -> " + childModule.getModuleInformationAsString());
                    else
                        logger.log(this, "CHILD -> NULL");
                }
            }
        }
        if (stringWriter != null) {
            yaml.outString = stringWriter.toString();
        }

        if (isDebug) {
            logger.log(this, "Finished writing, took " + timer.getFormattedMillis() + "ms or " + timer.getFormattedSeconds() + "s");
            logger.log(this, "");
        }
    }

    /**
     * Writes an in-memory {@link YamlSection} object to file.
     *
     * @param writer       the writer to use.
     * @param module       the current module to write.
     * @param beforeModule the last already written module.
     */
    private void parseModule(BufferedWriter writer,
                             YamlSection module,
                             YamlSection beforeModule) throws IOException {
        int keysSize = module.getKeys().size();
        int beforeKeysSize = beforeModule.getKeys().size();
        String currentKey; // The current key of the current module
        String currentBeforeKey; // The current key of the before module
        for (int i = 0; i < keysSize; i++) { // Go through each key of the module

            // Get current modules key and beforeModules key.
            // It may happen that the beforeModule has less keys, or no keys at all,
            // so deal with that:
            currentKey = module.getKeyByIndex(i);
            if (i < beforeKeysSize && !beforeModule.getKeys().isEmpty())
                currentBeforeKey = beforeModule.getKeyByIndex(i);
            else
                currentBeforeKey = "";

            // Only write this key, if its not equal to the currentBeforeKey
            // or... the other part is hard to explain.
            if (!currentKey.equals(currentBeforeKey) || (i != 0 && !module.getKeyByIndex(i - 1).equals(beforeModule.getKeyByIndex(i - 1)))) {

                String spaces = "";
                for (int j = 0; j < i; j++) { // The current keys index/position in the list defines how much spaces are needed.
                    spaces = spaces + "  ";
                }

                for (int j = 0; j < module.getCountTopSpaces(); j++) {
                    writer.newLine();
                    writer.flush();
                }

                if (module.getComments() != null && i == (keysSize - 1)) // Only write comments to the last key in the list
                    if (!module.getComments().isEmpty()) {
                        for (String comment :
                                module.getComments()) {
                            // Adds support for Strings containing \n to split up comments
                            BufferedReader bufReader = new BufferedReader(new StringReader(comment));
                            String commentLine;
                            boolean isMultiline = false;
                            while ((commentLine = bufReader.readLine()) != null) {
                                isMultiline = true;
                                writer.write(spaces + "# " + commentLine);
                                writer.newLine();
                                writer.flush();
                            }

                            if (!isMultiline) {
                                writer.write(spaces + "# " + comment);
                                writer.newLine();
                                writer.flush();
                            }
                        }
                    } else if (yaml.isWriteDefaultCommentsWhenEmptyEnabled) {
                        for (String comment :
                                module.getComments()) {
                            // Adds support for Strings containing \n to split up comments
                            BufferedReader bufReader = new BufferedReader(new StringReader(comment));
                            String commentLine;
                            boolean isMultiline = false;
                            while ((commentLine = bufReader.readLine()) != null) {
                                isMultiline = true;
                                writer.write(spaces + "# " + commentLine);
                                writer.newLine();
                                writer.flush();
                            }

                            if (!isMultiline) {
                                writer.write(spaces + "# " + comment);
                                writer.newLine();
                                writer.flush();
                            }
                        }
                    }

                writer.write(spaces + currentKey + ": ");

                if (module.getValues() != null && i == (keysSize - 1)) { // Only write values to the last key in the list
                    if (!module.getValues().isEmpty() && !isOnlyNullsList(module.getValues())) { // Write values if they exist, else write defaults, else write nothing
                        if (module.getValues().size() == 1) { // Even if we only got one DYModule, it written as a list
                            YamlValue value = module.getValue();
                            if (value != null) { // Only write if its not null
                                if (value.asString() != null) writeValueWithoutLineBreaks(writer, value.asString());
                                if (value.hasComment())
                                    writer.write(" # " + value.getComment()); // Append side comment to value
                            }

                            writer.newLine();
                            writer.flush();
                        } else { // This means we got multiple values, aka a list
                            writer.newLine();
                            for (int j = 0; j < module.getValues().size(); j++) {
                                YamlValue value = module.getValueByIndex(j);
                                if (value != null) {
                                    writer.write(spaces + "  - ");
                                    if (value.asString() != null)
                                        writeValueWithoutLineBreaks(writer, value.asString()); // Append the value
                                    if (value.hasComment())
                                        writer.write(" # " + value.getComment()); // Append side comment to value
                                }

                                writer.newLine();
                                writer.flush();
                            }
                        }
                    } else if (yaml.isWriteDefaultValuesWhenEmptyEnabled) {
                        if (module.getDefValues() != null && !module.getDefValues().isEmpty()) {
                            if (module.getDefValues().size() == 1) {
                                YamlValue defValue = module.getDefValue();
                                if (defValue != null) {
                                    if (defValue.asString() != null)
                                        writeValueWithoutLineBreaks(writer, defValue.asString());
                                    if (defValue.hasComment())
                                        writer.write(" # " + defValue.getComment()); // Append side comment to value
                                }

                                writer.newLine();
                                writer.flush();
                            } else {
                                writer.newLine();
                                for (int j = 0; j < module.getDefValues().size(); j++) {
                                    YamlValue value = module.getDefValueByIndex(j);
                                    if (value != null) {
                                        writer.write(spaces + "  - ");
                                        if (value.asString() != null)
                                            writeValueWithoutLineBreaks(writer, value.asString()); // Append the value
                                        if (value.hasComment())
                                            writer.write(" # " + value.getComment()); // Append side comment to value
                                    }

                                    writer.newLine();
                                    writer.flush();
                                }
                            }
                        } else {
                            writer.newLine();
                            writer.flush();
                        }
                    }

                } else {
                    writer.newLine();
                    writer.flush();
                }
            }
        }
    }

    private void writeValueWithoutLineBreaks(BufferedWriter writer, String value) throws IOException {
        value = value.replace("\n", " ");
        writer.write(value);
    }

    private boolean isOnlyNullsList(List<YamlValue> values) {
        boolean hasValue = false;
        for (YamlValue val :
                values) {
            if (val.get() != null) {
                hasValue = true;
                break;
            }
        }
        return !hasValue;
    }
}
