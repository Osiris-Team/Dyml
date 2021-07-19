package com.osiris.dyml.exceptions;

/**
 * Usually thrown when there is something wrong
 * with a {@link com.osiris.dyml.db.DYColumn}s length.
 */
public class BrokenColumnsException extends RuntimeException{
    public BrokenColumnsException() {
    }

    public BrokenColumnsException(String message) {
        super(message);
    }

    public BrokenColumnsException(String message, Throwable cause) {
        super(message, cause);
    }

    public BrokenColumnsException(Throwable cause) {
        super(cause);
    }

    public BrokenColumnsException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
