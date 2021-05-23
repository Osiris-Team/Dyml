package com.osiris.dyml.exceptions;

public class DYReaderException extends Exception {

    public DYReaderException() {
    }

    public DYReaderException(String message) {
        super(message);
    }

    public DYReaderException(String message, Throwable cause) {
        super(message, cause);
    }

    public DYReaderException(Throwable cause) {
        super(cause);
    }

    public DYReaderException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
