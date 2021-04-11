/*
 *  Copyright Osiris Team
 *  All rights reserved.
 *
 *  This software is licensed work.
 *  Please consult the file "LICENSE" for details.
 */

package com.osiris.dyml;


import com.osiris.dyml.exceptions.IllegalListException;
import com.osiris.dyml.utils.UtilsDYModule;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Responsible for reading the provided file and parsing it into modules.
 */
class DYReader {

    public void parse(DreamYaml yaml) throws Exception {
        File file = yaml.getFile();
        if (file==null) throw new Exception("File is null! Make sure to load it at least once!");
        if (!file.exists()) throw new Exception("File '"+file.getName()+"' doesn't exist!");

        BufferedReader reader = new BufferedReader(new FileReader(file));
        DYLine lastLine = null;
        List<DYLine> lineList = new ArrayList<>();
        while(true){
            String line = reader.readLine(); // Its important, that a new, unique Object is created for each line and number
            int lineNumber = 0; // Its important, that a new, unique Object is created for each line and number
            if (line!=null){
                DYLine currentLine = new DYLine(line, lineNumber);
                parseLine(yaml, currentLine, lastLine, lineList);
                if (currentLine.isColonFound())
                    lineList.add(currentLine);
                lastLine = currentLine;
                lineNumber++;
            }
            else
                break;
        }

        // If there where modules added, update their values
        UtilsDYModule utils = new UtilsDYModule();
        for (DYModule loadedModule :
                yaml.getAllLoaded()) {
            DYModule added = utils.getExisting(loadedModule, yaml.getAllAdded());
            if (added!=null) {
                added.setValues(loadedModule.getValues());
            }
        }

        // Do post processing if enabled
        if (yaml.isPostProcessingEnabled())
            for (DYModule m :
                    yaml.getAllLoaded()) {
                utils.optimizeValues(m.getValues());
            }
    }

    public void parseLine(DreamYaml yaml, DYLine currentLine, DYLine lastLine, List<DYLine> lineList) throws IllegalListException {

        if (!currentLine.getLineContent().isEmpty()){
            if(yaml.isDebug()) System.out.println("Reading line '"+currentLine.getLineNumber()+"' with content: '"+currentLine.getLineContent()+"'");
            // Go thorough each character of the string, until a special one is found
            int charCode = 0;
            int charCodeBefore = 0;
            for (int i = 0; i < currentLine.getLineContent().length(); i++) {
                charCode = currentLine.getLineContent().codePointAt(i);
                checkChar(currentLine, charCode, i, charCodeBefore);
                charCodeBefore = charCode;
                // TODO
                //System.out.println(currentLine.getLineAsChar()[i]+" : "+charCode);
                if (currentLine.isHashTagFound() || currentLine.isColonFound() || currentLine.isHyphenFound())
                    break;
            }

            // Add the module to the yaml modules list, but only under certain circumstances (logic below)
            DYModule module = new DYModule();
            if (lastLine!=null){
                if (currentLine.isHashTagFound()){
                    module.setLine(currentLine); // Line gets set for everything except lists/hyphens
                    if (lastLine.isHashTagFound())
                        module = yaml.getLastLoadedModule(); // If the current line and the last line are comments, add the current comment to the last comments object/module
                    else{
                        yaml.getAllLoaded().add(module);
                    }
                    String c = currentLine.getValue();
                    if (c!=null)
                        module.addComment(c);
                }
                else if(currentLine.isColonFound()){
                    module.setLine(currentLine);
                    if(lastLine.isHashTagFound() && currentLine.getCountSpaces()>0){
                        /*
                        Adds support for: (Child-Keys in general and with same parents)
                        p1:
                          c1:
                            a1:
                          c2:
                            a1:
                         */
                        module = yaml.getLastLoadedModule(); // If the current line is a key, but the last line was a comment, add the key to the last comments object
                        // Go reversely through the lines list and search for the parent
                        for (int i = lineList.size()-1; i >= 0; i--) { // -2 is not needed, because the current-line only gets added to the list after the method finished
                            DYLine line = lineList.get(i);
                            if (line.isColonFound() && line.getCountSpaces()<currentLine.getCountSpaces()){
                                module.getKeys().addAll(yaml.getAllLoaded().get(i).getKeys()); // module.getKeys().addAll(yaml.getAllLoaded().get(i).getKeys());
                                break;
                            }
                        }
                    }
                    else if(!lastLine.isHashTagFound() && currentLine.getCountSpaces()>0){
                        for (int i = lineList.size()-1; i >= 0; i--) {
                            DYLine line = lineList.get(i);
                            if (line.isColonFound() && (line.getCountSpaces()<currentLine.getCountSpaces())){
                                module.getKeys().addAll(yaml.getAllLoaded().get(i).getKeys()); //module.getKeys().addAll(yaml.getAllLoaded().get(i).getKeys());
                                break;
                            }
                        }
                        yaml.getAllLoaded().add(module);
                    }
                    else if (lastLine.isHashTagFound()){
                        module = yaml.getLastLoadedModule();
                    }
                    else{
                        yaml.getAllLoaded().add(module);
                    }
                    module.addKey(currentLine.getKey());
                    String v = currentLine.getValue();
                    if (v!=null)
                        module.setValue(v);
                }
                else if(currentLine.isHyphenFound()){
                    if(lastLine.isColonFound() || lastLine.isHyphenFound())
                        module = yaml.getLastLoadedModule();
                    else
                        throw new IllegalListException(yaml.getFile().getName(), currentLine);
                    String v = currentLine.getValue();
                    if (v!=null)
                        module.addValue(v);
                }
            }
            else{
                if (currentLine.isHashTagFound()){
                    module.setLine(currentLine);
                    String c = currentLine.getValue();
                    if (c!=null)
                        module.addComment(c);
                }
                else if(currentLine.isColonFound()){
                    module.setLine(currentLine);
                    module.setKeys(currentLine.getKey());
                    String v = currentLine.getValue();
                    if (v!=null)
                        module.setValue(v);
                }
                else if(currentLine.isHyphenFound()){
                    String v = currentLine.getValue();
                    if (v!=null)
                        module.addValue(v);
                }

                yaml.getAllLoaded().add(module);
            }
        }
    }

    private void checkChar(DYLine line, int charCode, int charIndex, int charCodeBefore) {
            switch (charCode){
                case 32:
                    // Empty space before a colon, indicates objects tree position. Increment until a colon is found.
                    if (!line.isWordFound())
                        line.setCountSpaces(line.getCountSpaces() + 1);
                    break;
                case 35:
                    // # Hashtag indicates start of a comment
                    line.setHashTagFound(true);
                    line.setValue(getOptimizedString(line
                            .getLineContent()
                            .substring(charIndex+1)));
                    break;
                case 58:
                    // : Colon enables us to define a key
                    line.setColonFound(true);
                    line.setKey(line
                            .getLineContent()
                            .substring(line.getCountSpaces(), charIndex));
                    line.setValue(getOptimizedString(line
                            .getLineContent()
                            .substring(charIndex+1)));
                    break;
                case 45:
                    // - Hyphen indicates a list object but only if the char before didn't exist or it was a space
                    if (charCodeBefore==0 || charCodeBefore==32){
                        line.setHyphenFound(true);
                        line.setValue(getOptimizedString(line
                                .getLineContent()
                                .substring(charIndex+1)));
                    }
                    break;
                default:
                    // Any other charCode than above will count as word
                    line.setWordFound(true);
            }
    }


    /**
     * Trims the string and sets it null if empty.
     * String before: '  hi boi  '
     * String after: 'hi boi'
     * Result: removed 4 spaces.
     * @param s
     */
    private String getOptimizedString(String s) {
        if (s != null){
            s = s.trim();
            if (s.isEmpty()) s = null;
        }
        return s;
    }

}
