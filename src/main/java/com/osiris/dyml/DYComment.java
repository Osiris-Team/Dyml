/*
 *  Copyright Osiris Team
 *  All rights reserved.
 *
 *  This software is licensed work.
 *  Please consult the file "LICENSE" for details.
 */

package com.osiris.dyml;

// TODO WORK IN PROGRESS

/**
 * Holds 
 */
public class DYComment {
    private String keyCommentAsString;
    private String valueCommentAsString;

    /**
     * One of the params must be null, its not allowed to have
     * both of them null.
     * @param keyCommentAsString
     * @param valueCommentAsString
     */
    public DYComment(String keyCommentAsString, String valueCommentAsString) {
        this.keyCommentAsString = keyCommentAsString;
        this.valueCommentAsString = valueCommentAsString;

        if (keyCommentAsString ==null && valueCommentAsString ==null)
            throw new NullPointerException("Its not allowed that both of the params are null! " +
                    "Please define at least one param.");

        if (keyCommentAsString !=null && valueCommentAsString !=null)
            throw new NullPointerException("Its not allowed that both of the params are NOT null! At least one of " +
                    "the params must be null!");
    }

    // UTIL METHODS:

    /**
     * Returns the regular comment if not null, else returns the side comment.
     */
    public String getComment(){
        if (isKeyComment())
            return keyCommentAsString;
        else
            return valueCommentAsString;
    }

    // IS METHODS:

    public boolean isKeyComment(){
        return keyCommentAsString != null;
    }

    public boolean isValueComment(){
        return valueCommentAsString != null;
    }


    // GETTERS AND SETTERS:

    public String getKeyCommentAsString() {
        return keyCommentAsString;
    }

    public void setKeyCommentAsString(String keyCommentAsString) {
        this.keyCommentAsString = keyCommentAsString;
    }

    public String getValueCommentAsString() {
        return valueCommentAsString;
    }

    public void setValueCommentAsString(String valueCommentAsString) {
        this.valueCommentAsString = valueCommentAsString;
    }
}
