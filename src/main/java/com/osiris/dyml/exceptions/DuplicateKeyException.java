/*
 *  Copyright Osiris Team
 *  All rights reserved.
 *
 *  This software is licensed work.
 *  Please consult the file "LICENSE" for details.
 */

package com.osiris.dyml.exceptions;

public class DuplicateKeyException extends Exception {
    private final String message;

    public DuplicateKeyException(String message) {
        this(message, null, null);
    }

    public DuplicateKeyException(String fileName, String key) {
        this(null, fileName, key);
    }

    public DuplicateKeyException(String message, String fileName, String key) {
        super();
        if (message == null)
            this.message = "Duplicate key '" + key + "' found in '" + fileName + "' file.";
        else
            this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
