package com.osiris.dyml;

/**
 * The in-memory representation of a single value. <br>
 * This value may have a comment (side comment).
 */
public class DYValue {
    private String valueAsString;
    private DYModule valueAsModule;
    private String comment;

    public DYValue(String valueAsString) {
        this(valueAsString, null);
    }

    public DYValue(DYModule valueAsModule) {
        this(valueAsModule, null);
    }

    public DYValue(char[] value) {
        this(String.valueOf(value), null);
    }

    public DYValue(boolean value) {
        this(String.valueOf(value), null);
    }

    public DYValue(Boolean value) {
        this(value.toString(), null);
    }

    public DYValue(byte value) {
        this(String.valueOf(value), null);
    }

    public DYValue(Byte value) {
        this(value.toString(), null);
    }

    public DYValue(short value) {
        this(String.valueOf(value), null);
    }

    public DYValue(Short value) {
        this(value.toString(), null);
    }

    public DYValue(int value) {
        this(String.valueOf(value), null);
    }

    public DYValue(Integer value) {
        this(value.toString(), null);
    }

    public DYValue(long value) {
        this(String.valueOf(value), null);
    }

    public DYValue(Long value) {
        this(value.toString(), null);
    }

    public DYValue(float value) {
        this(String.valueOf(value), null);
    }

    public DYValue(Float value) {
        this(value.toString(), null);
    }

    public DYValue(double value) {
        this(String.valueOf(value), null);
    }

    public DYValue(Double value) {
        this(value.toString(), null);
    }

    /**
     * The in-memory representation of a single value. <br>
     * This value may have a comment (side comment). <br>
     * Note that multi lined side comments do not exist. <br>
     * For that sake line-separators get removed from the comment string.
     *
     * @param valueAsString Can be null.
     * @param comment       Can be null.
     */
    public DYValue(String valueAsString, String comment) {
        this.valueAsString = valueAsString;
        setComment(comment);
    }

    /**
     * A value can also be another {@link DYModule}. <br>
     * This can go on forever like a Matroschka.
     *
     * @param valueAsModule Can be null.
     * @param comment       Can be null.
     */
    public DYValue(DYModule valueAsModule, String comment) {
        this.valueAsModule = valueAsModule;
        //this.valueAsString = valueAsModule.toString();
        setComment(comment);
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
     * Note that multi-lined side comments do not exist and thus contains no line separators.
     */
    public String getComment() {
        return comment;
    }

    /**
     * Line separators get removed.
     * See {@link #getComment()} for details.
     */
    public void setComment(String comment) {
        if (comment != null)
            comment = comment.replace(System.lineSeparator(), "");
        this.comment = comment;
    }


    // GETTERS:

    public DYModule asDYModule() {
        return valueAsModule;
    }

    public String asString() {
        return valueAsString;
    }

    public char[] asCharArray() {
        return valueAsString.toCharArray();
    }

    public boolean asBoolean() {
        return Boolean.parseBoolean(valueAsString);
    }

    public byte asByte() {
        return Byte.parseByte(valueAsString);
    }

    public short asShort() {
        return Short.parseShort(valueAsString);
    }

    public int asInt() {
        return Integer.parseInt(valueAsString);
    }

    public long asLong() {
        return Long.parseLong(valueAsString);
    }

    public float asFloat() {
        return Float.parseFloat(valueAsString);
    }

    public Double asDouble() {
        return Double.parseDouble(valueAsString);
    }


    // SETTERS:


    public DYValue set(String value) {
        this.valueAsString = value;
        this.valueAsModule = null;
        return this;
    }

    public DYValue set(char[] value) {
        this.valueAsString = String.valueOf(value);
        this.valueAsModule = null;
        return this;
    }

    public DYValue set(boolean value) {
        this.valueAsString = String.valueOf(value);
        this.valueAsModule = null;
        return this;
    }

    public DYValue set(Boolean value) {
        if (value == null) {
            this.valueAsString = null;
            this.valueAsModule = null;
            return this;
        }
        this.valueAsString = value.toString();
        this.valueAsModule = null;
        return this;
    }

    public DYValue set(byte value) {
        this.valueAsString = String.valueOf(value);
        this.valueAsModule = null;
        return this;
    }

    public DYValue set(Byte value) {
        if (value == null) {
            this.valueAsString = null;
            this.valueAsModule = null;
            return this;
        }
        this.valueAsString = value.toString();
        this.valueAsModule = null;
        return this;
    }

    public DYValue set(short value) {
        this.valueAsString = String.valueOf(value);
        this.valueAsModule = null;
        return this;
    }

    public DYValue set(Short value) {
        if (value == null) {
            this.valueAsString = null;
            this.valueAsModule = null;
            return this;
        }
        this.valueAsString = value.toString();
        this.valueAsModule = null;
        return this;
    }

    public DYValue set(int value) {
        this.valueAsString = String.valueOf(value);
        this.valueAsModule = null;
        return this;
    }

    public DYValue set(Integer value) {
        if (value == null) {
            this.valueAsString = null;
            this.valueAsModule = null;
            return this;
        }
        this.valueAsString = value.toString();
        this.valueAsModule = null;
        return this;
    }

    public DYValue set(long value) {
        this.valueAsString = String.valueOf(value);
        this.valueAsModule = null;
        return this;
    }

    public DYValue set(Long value) {
        if (value == null) {
            this.valueAsString = null;
            this.valueAsModule = null;
            return this;
        }
        this.valueAsString = value.toString();
        this.valueAsModule = null;
        return this;
    }

    public DYValue set(float value) {
        this.valueAsString = String.valueOf(value);
        this.valueAsModule = null;
        return this;
    }

    public DYValue set(Float value) {
        if (value == null) {
            this.valueAsString = null;
            this.valueAsModule = null;
            return this;
        }
        this.valueAsString = value.toString();
        this.valueAsModule = null;
        return this;
    }

    public DYValue set(double value) {
        this.valueAsString = String.valueOf(value);
        this.valueAsModule = null;
        return this;
    }

    public DYValue set(Double value) {
        if (value == null) {
            this.valueAsString = null;
            this.valueAsModule = null;
            return this;
        }
        this.valueAsString = value.toString();
        this.valueAsModule = null;
        return this;
    }


    // TYPE CHECKS:

    public boolean isDYModule() {
        return valueAsModule != null;
    }

    public boolean isBoolean() {
        return valueAsString.equalsIgnoreCase("true") || valueAsString.equalsIgnoreCase("false");
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
