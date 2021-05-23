/*
 *  Copyright Osiris Team
 *  All rights reserved.
 *
 *  This software is licensed work.
 *  Please consult the file "LICENSE" for details.
 */

package com.osiris.dyml.exceptions;

public class DuplicateKeyException extends Exception {
    private final String fileName;
    private final String key;

    public DuplicateKeyException(String fileName, String key) {
        super();
        this.fileName = fileName;
        this.key = key;
    }

    @Override
    public String getMessage() {
        return "Duplicate key '" + key + "' found in '" + fileName + "' file.";
    }
}
