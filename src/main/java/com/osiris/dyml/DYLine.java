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
    private String rawStringKey;
    private String rawStringValue;

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

    public void setColonFound(boolean colonFound) {
        this.colonFound = colonFound;
    }

    public String getRawStringKey() {
        return rawStringKey;
    }

    public void setRawStringKey(String rawStringKey) {
        this.rawStringKey = rawStringKey;
    }

    public String getRawStringValue() {
        return this.rawStringValue;
    }

    public void setRawStringValue(String rawStringValue) {
        this.rawStringValue = rawStringValue;
    }

    public boolean isWordFound() {
        return wordFound;
    }

    public void setWordFound(boolean wordFound) {
        this.wordFound = wordFound;
    }
}
