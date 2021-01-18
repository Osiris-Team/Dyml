/*
 *  Copyright Osiris Team
 *  All rights reserved.
 *
 *  This software is licensed work.
 *  Please consult the file "LICENSE" for details.
 */

package com.osiris.dyml.exceptions;

public class IllegalSpaceException extends Exception {
    private String fileName;
    private int lineNumber;

    public IllegalSpaceException(String fileName, int lineNumber) {
        super();
        this.fileName = fileName;
        this.lineNumber = lineNumber;
    }

    @Override
    public String getMessage() {
        return "Illegal space found at line '" + lineNumber + "' in '" + fileName + "' file.";
    }
}
