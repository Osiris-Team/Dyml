/*
 *  Copyright Osiris Team
 *  All rights reserved.
 *
 *  This software is licensed work.
 *  Please consult the file "LICENSE" for details.
 */

package com.osiris.dyml;

import com.osiris.dyml.utils.UtilsForModules;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

class DYWriter {

    public void parse(DreamYaml yaml) {
        File file = yaml.getFile();
        if (file==null) {
            System.err.println("File is null! Make sure to load it at least once!");
            return;
        }

        if (!file.exists()){
            System.err.println("File '"+file.getName()+"' doesn't exist!");
            return;
        }

        try {
            List<DYModule> completeModulesList =  new UtilsForModules().createUnifiedList(yaml.getAll(), yaml.getAllLoaded());
            if (completeModulesList.isEmpty()){
                System.err.println("Given modules list for file '"+file.getName()+"' is empty! Nothing to write!");
                return;
            }

            BufferedWriter writer = new BufferedWriter(new FileWriter(file), 32768); // TODO compare speed with def buffer
            writer.write(""); // Clear old content

            DYModule lastModule = new DYModule(); // Create an empty module as start point
            for (DYModule m :
                    completeModulesList) {
                parseModule(writer, m, lastModule);
                lastModule = m;
            }

            if(yaml.isDebug()) yaml.printAll();
        } catch (Exception e) {
            e.printStackTrace();
        }

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


                for (String comment :
                        m.getComments()) {
                    // Adds support for Strings containing \n to split up comments
                    BufferedReader bufReader = new BufferedReader(new StringReader(comment));
                    String commentLine=null;
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
                        if (m.getValues().size()==1){
                            writer.write("" + m.getValue());
                            writer.newLine();
                            writer.flush();
                        }
                        else {
                            writer.newLine();
                            for (String value :
                                    m.getValues()) {
                                writer.write(spaces + "  - " + value);
                                writer.newLine();
                                writer.flush();
                            }
                        }
                    }
                    else{
                        if (m.getDefaultValues()!=null && !m.getDefaultValues().isEmpty()){
                            if (m.getDefaultValues().size()==1){
                                writer.write("" + m.getDefaultValue());
                                writer.newLine();
                                writer.flush();
                            }
                            else {
                                writer.newLine();
                                for (String value :
                                        m.getDefaultValues()) {
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
