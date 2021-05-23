/*
 *  Copyright Osiris Team
 *  All rights reserved.
 *
 *  This software is licensed work.
 *  Please consult the file "LICENSE" for details.
 */

package com.osiris.dyml;

import com.osiris.dyml.exceptions.DYWriterException;
import com.osiris.dyml.utils.UtilsDYModule;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Responsible for parsing and writing the provided modules to file.
 */
class DYWriter {

    public void parse(DreamYaml yaml, boolean overwrite, boolean reset) throws DYWriterException, IOException {
        File file = yaml.getFile();
        if (file == null) throw new DYWriterException("File is null! Make sure to load it at least once!");
        if (!file.exists()) throw new DYWriterException("File '" + file.getName() + "' doesn't exist!");

        BufferedWriter writer = new BufferedWriter(new FileWriter(file), 32768); // TODO compare speed with def buffer
        writer.write(""); // Clear old content

        if (reset) return;

        List<DYModule> modulesToSave = new ArrayList<>();
        if (overwrite) {
            modulesToSave = yaml.getAllAdded();
            if (modulesToSave.isEmpty())
                throw new DYWriterException("Failed to write modules to file: There are no modules in the 'added modules list' for file '" + file.getName() + "' ! Nothing to write!");
        } else {
            modulesToSave = new UtilsDYModule().createUnifiedList(yaml.getAllAdded(), yaml.getAllLoaded());
            if (modulesToSave.isEmpty())
                throw new DYWriterException("Failed to write modules to file: There are no modules in the list for file '" + file.getName() + "' ! Nothing to write!");
        }


        DYModule lastModule = new DYModule(); // Create an empty module as start point
        for (DYModule m :
                modulesToSave) {
            parseModule(writer, m, lastModule);
            lastModule = m;
        }

        if (yaml.isDebugEnabled()) yaml.printAll();
    }

    private void parseModule(BufferedWriter writer,
                             DYModule m,
                             DYModule lastM) throws IOException {

        int size = m.getKeys().size();
        int lastSize = lastM.getKeys().size();
        String key;
        String beforeKey;
        for (int i = 0; i < size; i++) { // Go through each key
            key = m.getKeyByIndex(i);
            if (i < lastSize && !lastM.getKeys().isEmpty()) beforeKey = lastM.getKeyByIndex(i);
            else beforeKey = "";

            if (!key.equals(beforeKey) || (i != 0 && !m.getKeyByIndex(i - 1).equals(lastM.getKeyByIndex(i - 1)))) {
                // Only write new key if this key isn't equal to the key before
                String spaces = "";
                for (int j = 0; j < i; j++) { // The current keys index/position in the list defines how much spaces are needed.
                    spaces = spaces + "  ";
                }

                if (m.getComments() != null && i == (size - 1)) // Only write comments to the last key in the list
                    for (String comment :
                            m.getComments()) {
                        // Adds support for Strings containing \n to split up comments
                        BufferedReader bufReader = new BufferedReader(new StringReader(comment));
                        String commentLine = null;
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

                writer.write(spaces + key + ": ");

                if (m.getValues() != null && i == (size - 1)) { // Only write values to the last key in the list
                    if (!m.getValues().isEmpty()) { // Write values if they exist, else write defaults, else write nothing
                        if (m.getValues().size() == 1) {
                            DYValue value = m.getValue();
                            if (value != null) // Only write if its not null
                                if (value.isDYModule()) // Check if its a module
                                    parseModule(writer, value.asDYModule(), m);
                                else {
                                    writer.write(value.asString());
                                    if (value.hasComment()) // Append side comment to value
                                        writer.write(value.getComment());
                                }

                            writer.newLine();
                            writer.flush();
                        } else { // This means we got a list and not just a single value
                            writer.newLine();
                            for (DYValue value :
                                    m.getValues()) {
                                if (value != null) // Only write if its not null
                                    if (value.isDYModule()) // Check if its a module
                                        parseModule(writer, value.asDYModule(), m);
                                    else {
                                        writer.write(spaces + "  - " + value.asString());
                                        if (value.hasComment()) // Append side comment to value
                                            writer.write(value.getComment());
                                    }

                                writer.newLine();
                                writer.flush();
                            }
                        }
                    } else {
                        if (m.getDefaultValues() != null && !m.getDefaultValues().isEmpty()) {
                            if (m.getDefaultValues().size() == 1) {
                                DYValue defValue = m.getDefaultValue();
                                if (defValue != null)
                                    if (defValue.isDYModule()) // Check if its a module
                                        parseModule(writer, defValue.asDYModule(), m);
                                    else {
                                        writer.write(defValue.asString());
                                        if (defValue.hasComment()) // Append side comment to value
                                            writer.write(defValue.getComment());
                                    }

                                writer.newLine();
                                writer.flush();
                            } else {
                                writer.newLine();
                                for (DYValue value :
                                        m.getDefaultValues()) {
                                    if (value != null)
                                        if (value.isDYModule()) // Check if its a module
                                            parseModule(writer, value.asDYModule(), m);
                                        else {
                                            writer.write(spaces + "  - " + value.asString());
                                            if (value.hasComment()) // Append side comment to value
                                                writer.write(value.getComment());
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
}
