/*
 *  Copyright Osiris Team
 *  All rights reserved.
 *
 *  This software is licensed work.
 *  Please consult the file "LICENSE" for details.
 */

package com.osiris.dyml.exceptions;

import com.osiris.dyml.DYLine;

public class IllegalListException extends Exception {
    private String fileName;
    private DYLine line;

    public IllegalListException(String fileName, DYLine line) {
        super();
        this.fileName = fileName;
        this.line = line;
    }

    @Override
    public String getMessage() {
        return "Illegal list  '" + line.getLineContent() + "' found in '" + fileName + "' file at line "+line.getLineNumber()+".";
    }
}
