package com.osiris.dyml;

import java.util.Objects;

/**
 * The in-memory representation of a single key. <br>
 * This key may have a comment (top comment).
 */
public class DYKey {
    private String keyAsString;
    private String comment;

    public DYKey(String value) {
        this(value, null);
    }

    public DYKey(char[] value) {
        this(String.valueOf(value), null);
    }

    public DYKey(boolean value) {
        this(String.valueOf(value), null);
    }

    public DYKey(Boolean value) {
        this(value.toString(), null);
    }

    public DYKey(byte value) {
        this(String.valueOf(value), null);
    }

    public DYKey(Byte value) {
        this(value.toString(), null);
    }

    public DYKey(short value) {
        this(String.valueOf(value), null);
    }

    public DYKey(Short value) {
        this(value.toString(), null);
    }

    public DYKey(int value) {
        this(String.valueOf(value), null);
    }

    public DYKey(Integer value) {
        this(value.toString(), null);
    }

    public DYKey(long value) {
        this(String.valueOf(value), null);
    }

    public DYKey(Long value) {
        this(value.toString(), null);
    }

    public DYKey(float value) {
        this(String.valueOf(value), null);
    }

    public DYKey(Float value) {
        this(value.toString(), null);
    }

    public DYKey(double value) {
        this(String.valueOf(value), null);
    }

    public DYKey(Double value) {
        this(value.toString(), null);
    }

    /**
     * The in-memory representation of a single key. <br>
     * This key may have a comment (top comment). <br>
     *
     * @param value   Cannot be null!
     * @param comment Can be null.
     */
    public DYKey(String value, String comment) {
        this.keyAsString = value;
        this.comment = comment;
        Objects.requireNonNull(value);
    }


    // COMMENT STUFF:


    /**
     * Returns true if this value has a comment. <br>
     * See {@link #getComment()} for details.
     */
    public boolean hasComment() {
        return comment != null;
    }

    /**
     * Returns this values comment. <br>
     * These can also be named 'side comments'. <br>
     * Note that multi-lined side comments do not exist.
     */
    public String getComment() {
        return comment;
    }

    /**
     * See {@link #getComment()} for details.
     */
    public void setComment(String comment) {
        this.comment = comment;
    }


    // GETTERS:


    public String asString() {
        return keyAsString;
    }

    public char[] asCharArray() {
        return keyAsString.toCharArray();
    }

    public boolean asBoolean() {
        return Boolean.parseBoolean(keyAsString);
    }

    public byte asByte() {
        return Byte.parseByte(keyAsString);
    }

    public short asShort() {
        return Short.parseShort(keyAsString);
    }

    public int asInt() {
        return Integer.parseInt(keyAsString);
    }

    public long asLong() {
        return Long.parseLong(keyAsString);
    }

    public float asFloat() {
        return Float.parseFloat(keyAsString);
    }

    public Double asDouble() {
        return Double.parseDouble(keyAsString);
    }


    // SETTERS:


    public DYKey set(String value) {
        this.keyAsString = value;
        return this;
    }

    public DYKey set(char[] value) {
        this.keyAsString = String.valueOf(value);
        return this;
    }

    public DYKey set(boolean value) {
        this.keyAsString = String.valueOf(value);
        return this;
    }

    public DYKey set(Boolean value) {
        if (value == null) {
            this.keyAsString = null;
            return this;
        }
        this.keyAsString = value.toString();
        return this;
    }

    public DYKey set(byte value) {
        this.keyAsString = String.valueOf(value);
        return this;
    }

    public DYKey set(Byte value) {
        if (value == null) {
            this.keyAsString = null;
            return this;
        }
        this.keyAsString = value.toString();
        return this;
    }

    public DYKey set(short value) {
        this.keyAsString = String.valueOf(value);
        return this;
    }

    public DYKey set(Short value) {
        if (value == null) {
            this.keyAsString = null;
            return this;
        }
        this.keyAsString = value.toString();
        return this;
    }

    public DYKey set(int value) {
        this.keyAsString = String.valueOf(value);
        return this;
    }

    public DYKey set(Integer value) {
        if (value == null) {
            this.keyAsString = null;
            return this;
        }
        this.keyAsString = value.toString();
        return this;
    }

    public DYKey set(long value) {
        this.keyAsString = String.valueOf(value);
        return this;
    }

    public DYKey set(Long value) {
        if (value == null) {
            this.keyAsString = null;
            return this;
        }
        this.keyAsString = value.toString();
        return this;
    }

    public DYKey set(float value) {
        this.keyAsString = String.valueOf(value);
        return this;
    }

    public DYKey set(Float value) {
        if (value == null) {
            this.keyAsString = null;
            return this;
        }
        this.keyAsString = value.toString();
        return this;
    }

    public DYKey set(double value) {
        this.keyAsString = String.valueOf(value);
        return this;
    }

    public DYKey set(Double value) {
        if (value == null) {
            this.keyAsString = null;
            return this;
        }
        this.keyAsString = value.toString();
        return this;
    }


    // TYPE CHECKS:


    public boolean isBoolean() {
        return keyAsString.equalsIgnoreCase("true") || keyAsString.equalsIgnoreCase("false");
    }

    public boolean isByte() {
        try {
            asByte();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isShort() {
        try {
            asShort();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isInt() {
        try {
            asInt();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isLong() {
        try {
            asLong();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isFloat() {
        try {
            asFloat();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isDouble() {
        try {
            asDouble();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

}
