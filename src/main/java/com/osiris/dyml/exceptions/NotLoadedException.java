package com.osiris.dyml.exceptions;

public class NotLoadedException extends Exception {
    @Override
    public String getMessage() {
        return "Make sure to call the load() method once before setting/adding any values!";
    }
}
