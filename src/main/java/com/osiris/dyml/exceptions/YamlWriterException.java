package com.osiris.dyml.exceptions;

public class YamlWriterException extends Exception {

    public YamlWriterException() {
    }

    public YamlWriterException(String message) {
        super(message);
    }

    public YamlWriterException(String message, Throwable cause) {
        super(message, cause);
    }

    public YamlWriterException(Throwable cause) {
        super(cause);
    }

    public YamlWriterException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
