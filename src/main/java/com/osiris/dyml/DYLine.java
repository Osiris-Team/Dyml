/*
 *  Copyright Osiris Team
 *  All rights reserved.
 *
 *  This software is licensed work.
 *  Please consult the file "LICENSE" for details.
 */

package com.osiris.dyml;

/**
 * Represents a single line of a yaml file.
 * It gets filled with information by {@link DYReader#checkChar(DYLine, int, int)} and {@link DYReader#parseLine(DreamYaml, DYLine)}.
 */
public class DYLine {
    private String fullLine;
    private int lineNumber;
    private char[] fullLineAsChar;
    private int countSpaces;
    private boolean commentFound;
    private boolean keyFound;
    private boolean hyphenFound;
    private boolean charFound;
    private String rawKey;
    private String rawValue;
    private String rawComment;

    public DYLine(String fullLine, int lineNumber) {
        this.fullLine = fullLine;
        this.lineNumber = lineNumber;
        this.fullLineAsChar = fullLine.toCharArray();
    }

    public String getFullLine() {
        return fullLine;
    }

    public void setFullLine(String fullLine) {
        this.fullLine = fullLine;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }

    public char[] getFullLineAsChar() {
        return fullLineAsChar;
    }

    public void setFullLineAsChar(char[] fullLineAsChar) {
        this.fullLineAsChar = fullLineAsChar;
    }

    public int getCountSpaces() {
        return countSpaces;
    }

    public void setCountSpaces(int countSpaces) {
        this.countSpaces = countSpaces;
    }

    public boolean isCommentFound() {
        return commentFound;
    }

    public void setCommentFound(boolean commentFound) {
        this.commentFound = commentFound;
    }

    public boolean isHyphenFound() {
        return hyphenFound;
    }

    public void setHyphenFound(boolean hyphenFound) {
        this.hyphenFound = hyphenFound;
    }

    public boolean isKeyFound() {
        return keyFound;
    }

    public void setKeyFound(boolean keyFound) {
        this.keyFound = keyFound;
    }

    public String getRawKey() {
        return rawKey;
    }

    public void setRawKey(String rawKey) {
        this.rawKey = rawKey;
    }

    public String getRawValue() {
        return this.rawValue;
    }

    public void setRawValue(String rawValue) {
        this.rawValue = rawValue;
    }

    public boolean isCharFound() {
        return charFound;
    }

    public void setCharFound(boolean charFound) {
        this.charFound = charFound;
    }

    public String getRawComment() {
        return rawComment;
    }

    public void setRawComment(String rawComment) {
        this.rawComment = rawComment;
    }
}
