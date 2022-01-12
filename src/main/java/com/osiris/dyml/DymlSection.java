package com.osiris.dyml;

import java.util.ArrayList;
import java.util.List;

public class DymlSection {
    public SmartString key;
    public SmartString value;
    public String comment;
    public DymlSection parent;
    public List<DymlSection> children;

    public DymlSection(SmartString key, SmartString value, String comment) {
        this.key = key;
        this.value = value;
        this.comment = comment;
        this.children = new ArrayList<>();
    }

    /**
     * Returns the value like its in the yaml file. If its empty there or null, this returns null. <br>
     * Note that this value got post-processed (if enabled). <br>
     * Also note that this is the lowest level you can get to the original yaml value. <br>
     * The lowest level is at {@link DYLine}, but thats only accessible for the {@link DYReader} and the {@link DYWriter}. <br>
     */
    public String asString() {
        return value.asString();
    }

    /**
     * Note that this can be null.
     */
    public char[] asCharArray() {
        if (value.asString()==null) return null;
        return value.asString().toCharArray();
    }

    /**
     * Note that this can be null.
     */
    public Boolean asBoolean() {
        if (value.asString()==null) return null;
        return Boolean.parseBoolean(value.asString());
    }

    /**
     * Note that this can be null.
     */
    public Byte asByte() {
        if (value.asString()==null) return null;
        return Byte.parseByte(value.asString());
    }

    /**
     * Note that this can be null.
     */
    public Short asShort() {
        if (value.asString()==null) return null;
        return Short.parseShort(value.asString());
    }

    /**
     * Note that this can be null.
     */
    public Integer asInt() {
        if (value.asString()==null) return null;
        return Integer.parseInt(value.asString());
    }

    /**
     * Note that this can be null.
     */
    public Long asLong() {
        if (value.asString()==null) return null;
        return Long.parseLong(value.asString());
    }

    /**
     * Note that this can be null.
     */
    public Float asFloat() {
        if (value.asString()==null) return null;
        return Float.parseFloat(value.asString());
    }

    /**
     * Note that this can be null.
     */
    public Double asDouble() {
        if (value.asString()==null) return null;
        return Double.parseDouble(value.asString());
    }
}
