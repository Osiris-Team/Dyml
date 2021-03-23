/*
 *  Copyright Osiris Team
 *  All rights reserved.
 *
 *  This software is licensed work.
 *  Please consult the file "LICENSE" for details.
 */

package com.osiris.dyml;

import com.osiris.dyml.utils.UtilsDYModule;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Responsible for parsing and writing the provided modules to file.
 */
class DYWriter {

    public void parse(DreamYaml yaml, boolean overwrite, boolean reset) throws Exception{
        File file = yaml.getFile();
        if (file==null) throw new Exception("File is null! Make sure to load it at least once!");
        if (!file.exists()) throw new Exception("File '"+file.getName()+"' doesn't exist!");

        BufferedWriter writer = new BufferedWriter(new FileWriter(file), 32768); // TODO compare speed with def buffer
        writer.write(""); // Clear old content

        if (reset) return;

        List<DYModule> modulesToSave = new ArrayList<>();
        if (overwrite) {
            modulesToSave = yaml.getAllAdded();
            if (modulesToSave.isEmpty())
                throw new Exception("Failed to write modules to file: There are no modules in the 'added modules list' for file '"+file.getName()+"' ! Nothing to write!");
        }
        else {
            modulesToSave = new UtilsDYModule().createUnifiedList(yaml.getAllAdded(), yaml.getAllLoaded());
            if (modulesToSave.isEmpty())
                throw new Exception("Failed to write modules to file: There are no modules in the list for file '"+file.getName()+"' ! Nothing to write!");
        }


        DYModule lastModule = new DYModule(); // Create an empty module as start point
        for (DYModule m :
                modulesToSave) {
            parseModule(writer, m, lastModule);
            lastModule = m;
        }

        if(yaml.isDebug()) yaml.printAll();
    }

    private void parseModule(BufferedWriter writer,
                             DYModule m,
                             DYModule lastM) throws IOException {

        int size = m.getKeys().size();
        int lastSize = lastM.getKeys().size();
        String key;
        String lastKey;
        for (int i = 0; i < size; i++) {
            key = m.getKeyByIndex(i);
            if (i<lastSize && !lastM.getKeys().isEmpty()) lastKey = lastM.getKeyByIndex(i);
            else lastKey = "";

            if (!key.equals(lastKey) || ( i!=0 && !m.getKeyByIndex(i-1).equals(lastM.getKeyByIndex(i-1)))) {
                // Only write new key if this key isn't equal to the key before
                String spaces = "";
                for (int j = 0; j < i; j++) { // The current keys index/position in the list defines how much spaces are needed.
                    spaces = spaces+"  ";
                }

                if (m.getComments()!=null && i==(size-1)) // Only write comments to the last key in the list
                    for (String comment :
                            m.getComments()) {
                        // Adds support for Strings containing \n to split up comments
                        BufferedReader bufReader = new BufferedReader(new StringReader(comment));
                        String commentLine = null;
                        boolean isMultiline = false;
                        while( (commentLine=bufReader.readLine()) != null )
                        {
                            isMultiline = true;
                            writer.write(spaces + "# " + commentLine);
                            writer.newLine();
                            writer.flush();
                        }

                        if (!isMultiline){
                            writer.write(spaces + "# " + comment);
                            writer.newLine();
                            writer.flush();
                        }
                    }

                writer.write(spaces + key + ": ");

                if (m.getValues()!=null && i==(size-1)){ // Only write values to the last key in the list
                    if (!m.getValues().isEmpty()){ // Write values if they exist, else write defaults, else write nothing
                        if (m.getValues().size() == 1){
                            if (m.getValue() != null) // Only write if its not null
                                writer.write("" + m.getValue());
                            writer.newLine();
                            writer.flush();
                        }
                        else {
                            writer.newLine();
                            for (String value :
                                    m.getValues()) {
                                if (value!=null) // Only write if its not null
                                    writer.write(spaces + "  - " + value);
                                writer.newLine();
                                writer.flush();
                            }
                        }
                    }
                    else{
                        if (m.getDefaultValues()!=null && !m.getDefaultValues().isEmpty()){
                            if (m.getDefaultValues().size()==1){
                                if (m.getDefaultValue()!=null)
                                    writer.write("" + m.getDefaultValue());
                                writer.newLine();
                                writer.flush();
                            }
                            else {
                                writer.newLine();
                                for (String value :
                                        m.getDefaultValues()) {
                                    if (value!=null)
                                        writer.write(spaces + "  - " + value);
                                    writer.newLine();
                                    writer.flush();
                                }
                            }
                        }
                        else{
                            writer.newLine();
                            writer.flush();
                        }
                    }

                }
                else{
                    writer.newLine();
                    writer.flush();
                }
            }
        }
    }
}
