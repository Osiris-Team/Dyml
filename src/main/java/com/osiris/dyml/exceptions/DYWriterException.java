package com.osiris.dyml.exceptions;

public class DYWriterException extends Exception {

    public DYWriterException() {
    }

    public DYWriterException(String message) {
        super(message);
    }

    public DYWriterException(String message, Throwable cause) {
        super(message, cause);
    }

    public DYWriterException(Throwable cause) {
        super(cause);
    }

    public DYWriterException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
