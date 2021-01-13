/*
 *  Copyright Osiris Team
 *  All rights reserved.
 *
 *  This software is licensed work.
 *  Please consult the file "LICENSE" for details.
 */

package com.osiris.dyml;

public class DYLine {
    private String lineContent;
    private int lineNumber;
    private char[] lineAsChar;
    private int countSpaces;
    private boolean hashTagFound;
    private boolean colonFound;
    private boolean hyphenFound;
    private boolean wordFound;
    private String key;
    private String value;

    public DYLine(String lineContent, int lineNumber) {
        this.lineContent = lineContent;
        this.lineNumber = lineNumber;
        this.lineAsChar = lineContent.toCharArray();
    }

    public String getLineContent() {
        return lineContent;
    }

    public void setLineContent(String lineContent) {
        this.lineContent = lineContent;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }

    public char[] getLineAsChar() {
        return lineAsChar;
    }

    public void setLineAsChar(char[] lineAsChar) {
        this.lineAsChar = lineAsChar;
    }

    public int getCountSpaces() {
        return countSpaces;
    }

    public void setCountSpaces(int countSpaces) {
        this.countSpaces = countSpaces;
    }

    public boolean isHashTagFound() {
        return hashTagFound;
    }

    public void setHashTagFound(boolean hashTagFound) {
        this.hashTagFound = hashTagFound;
    }

    public boolean isHyphenFound() {
        return hyphenFound;
    }

    public void setHyphenFound(boolean hyphenFound) {
        this.hyphenFound = hyphenFound;
    }

    public boolean isColonFound() {
        return colonFound;
    }

    public void setColonFound(boolean colonFound){
        this.colonFound = colonFound;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public boolean isWordFound() {
        return wordFound;
    }

    public void setWordFound(boolean wordFound) {
        this.wordFound = wordFound;
    }
}
