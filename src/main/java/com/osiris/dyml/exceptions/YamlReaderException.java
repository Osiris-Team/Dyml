package com.osiris.dyml.exceptions;

public class YamlReaderException extends Exception {

    public YamlReaderException() {
    }

    public YamlReaderException(String message) {
        super(message);
    }

    public YamlReaderException(String message, Throwable cause) {
        super(message, cause);
    }

    public YamlReaderException(Throwable cause) {
        super(cause);
    }

    public YamlReaderException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
