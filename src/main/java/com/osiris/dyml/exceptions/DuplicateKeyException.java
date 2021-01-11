package com.osiris.dyml.exceptions;

public class DuplicateKeyException extends Exception {
    private String fileName;
    private String key;

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
