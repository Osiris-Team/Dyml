package com.osiris.dyml.exceptions;

import com.osiris.dyml.DYLine;

public class UnsupportedFeatureException extends Exception {

    public UnsupportedFeatureException(String message, String fileName, DYLine line) {
        super(message + " (cause in '" + fileName + "' at line " + line.getLineNumber() + " '" + line.getFullLine() + "')");
    }

}
