package com.osiris.dyml;

/**
 * The in-memory representation of a single value. <br>
 * Note that this class acts as some sort of 'container' that holds
 * the actual String value and thus can never be null, but the value can (see {@link #get()} for details). <br>
 * This class also provides methods for working with the value as different data-types. <br>
 * This value may have a comment (side comment).
 */
@SuppressWarnings("ALL")
public class DYValueContainer {
    private String value;
    private String comment;
    private String defaultComment;

    public DYValueContainer(String value) {
        this(value, null);
    }

    public DYValueContainer(char[] value) {
        this(String.valueOf(value), null);
    }

    public DYValueContainer(boolean value) {
        this(String.valueOf(value), null);
    }

    public DYValueContainer(Boolean value) {
        this(value.toString(), null);
    }

    public DYValueContainer(byte value) {
        this(String.valueOf(value), null);
    }

    public DYValueContainer(Byte value) {
        this(value.toString(), null);
    }

    public DYValueContainer(short value) {
        this(String.valueOf(value), null);
    }

    public DYValueContainer(Short value) {
        this(value.toString(), null);
    }

    public DYValueContainer(int value) {
        this(String.valueOf(value), null);
    }

    public DYValueContainer(Integer value) {
        this(value.toString(), null);
    }

    public DYValueContainer(long value) {
        this(String.valueOf(value), null);
    }

    public DYValueContainer(Long value) {
        this(value.toString(), null);
    }

    public DYValueContainer(float value) {
        this(String.valueOf(value), null);
    }

    public DYValueContainer(Float value) {
        this(value.toString(), null);
    }

    public DYValueContainer(double value) {
        this(String.valueOf(value), null);
    }

    public DYValueContainer(Double value) {
        this(value.toString(), null);
    }

    /**
     * The in-memory representation of a single value. <br>
     * This value may have a comment (side comment). <br>
     * Note that multi lined side comments do not exist. <br>
     * For that sake line-separators get removed from the comment string.
     *
     * @param value   Can be null.
     * @param comment Can be null.
     */
    public DYValueContainer(String value, String comment) {
        this(value, comment, null);
    }

    /**
     * The in-memory representation of a single value. <br>
     * This value may have a comment (side comment). <br>
     * Note that multi lined side comments do not exist. <br>
     * For that sake line-separators get removed from the comment string.
     *
     * @param value      Can be null.
     * @param comment    Can be null.
     * @param defComment Can be null.
     */
    public DYValueContainer(String value, String comment, String defComment) {
        this.value = value;
        setComment(comment);
        setDefComment(defComment);
    }

    public String getValueInformationAsString() {
        return "VALUE: " + value + " COMMENT: " + comment + " DEF-COMMENT: " + defaultComment;
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
    public DYValueContainer setComment(String comment) {
        if (comment != null)
            comment = comment.replace(System.lineSeparator(), "");
        this.comment = comment;
        return this;
    }

    /**
     * Line separators get removed.
     * See {@link #getComment()} for details.
     */
    public DYValueContainer setDefComment(String defComment) {
        if (defComment != null)
            defComment = defComment.replace(System.lineSeparator(), "");
        this.defaultComment = defComment;
        return this;
    }


    // GETTERS:


    /**
     * Same as {@link #asString()}. <br>
     * Returns the value like its in the yaml file. If its empty there or null, this returns null. <br>
     * Note that this value got post-processed (if enabled). <br>
     * Also note that this is the lowest level you can get to the original yaml value. <br>
     * The lowest level is at {@link DYLine}, but thats only accessible for the {@link DYReader} and the {@link DYWriter}. <br>
     */
    public String get() {
        return value;
    }

    /**
     * Same as {@link #get()}. <br>
     * Returns the value like its in the yaml file. If its empty there or null, this returns null. <br>
     * Note that this value got post-processed (if enabled). <br>
     * Also note that this is the lowest level you can get to the original yaml value. <br>
     * The lowest level is at {@link DYLine}, but thats only accessible for the {@link DYReader} and the {@link DYWriter}. <br>
     */
    public String asString() {
        return value;
    }

    public char[] asCharArray() {
        return value.toCharArray();
    }

    public boolean asBoolean() {
        return Boolean.parseBoolean(value);
    }

    public byte asByte() {
        return Byte.parseByte(value);
    }

    public short asShort() {
        return Short.parseShort(value);
    }

    public int asInt() {
        return Integer.parseInt(value);
    }

    public long asLong() {
        return Long.parseLong(value);
    }

    public float asFloat() {
        return Float.parseFloat(value);
    }

    public Double asDouble() {
        return Double.parseDouble(value);
    }


    // SETTERS:


    public DYValueContainer set(String value) {
        this.value = value;
        return this;
    }

    public DYValueContainer set(char[] value) {
        this.value = String.valueOf(value);
        return this;
    }

    public DYValueContainer set(boolean value) {
        this.value = String.valueOf(value);
        return this;
    }

    public DYValueContainer set(Boolean value) {
        if (value == null) {
            this.value = null;
            return this;
        }
        this.value = value.toString();
        return this;
    }

    public DYValueContainer set(byte value) {
        this.value = String.valueOf(value);
        return this;
    }

    public DYValueContainer set(Byte value) {
        if (value == null) {
            this.value = null;
            return this;
        }
        this.value = value.toString();
        return this;
    }

    public DYValueContainer set(short value) {
        this.value = String.valueOf(value);
        return this;
    }

    public DYValueContainer set(Short value) {
        if (value == null) {
            this.value = null;
            return this;
        }
        this.value = value.toString();
        return this;
    }

    public DYValueContainer set(int value) {
        this.value = String.valueOf(value);

        return this;
    }

    public DYValueContainer set(Integer value) {
        if (value == null) {
            this.value = null;
            return this;
        }
        this.value = value.toString();
        return this;
    }

    public DYValueContainer set(long value) {
        this.value = String.valueOf(value);
        return this;
    }

    public DYValueContainer set(Long value) {
        if (value == null) {
            this.value = null;
            return this;
        }
        this.value = value.toString();
        return this;
    }

    public DYValueContainer set(float value) {
        this.value = String.valueOf(value);
        return this;
    }

    public DYValueContainer set(Float value) {
        if (value == null) {
            this.value = null;
            return this;
        }
        this.value = value.toString();
        return this;
    }

    public DYValueContainer set(double value) {
        this.value = String.valueOf(value);
        return this;
    }

    public DYValueContainer set(Double value) {
        if (value == null) {
            this.value = null;
            return this;
        }
        this.value = value.toString();
        return this;
    }


    // TYPE CHECKS:


    public boolean isBoolean() {
        return value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false");
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
