package com.osiris.dyml;

/**
 * The in-memory representation of a single value. <br>
 * Note that this class acts as some sort of 'container' that holds
 * the actual String value and thus can never be null, but the value can (see {@link #get()} for details). <br>
 * This class also provides methods for working with the value as different data-types. <br>
 * This value may have a comment (side comment).
 */
@SuppressWarnings("ALL")
public class DYValue {
    private String value;
    private String comment;
    private String defaultComment;

    public DYValue(String value) {
        this(value, null);
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
     * @param value   Can be null.
     * @param comment Can be null.
     */
    public DYValue(String value, String comment) {
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
    public DYValue(String value, String comment, String defComment) {
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
    public DYValue setComment(String comment) {
        if (comment != null)
            comment = comment.replace(System.lineSeparator(), "");
        this.comment = comment;
        return this;
    }

    /**
     * Line separators get removed.
     * See {@link #getComment()} for details.
     */
    public DYValue setDefComment(String defComment) {
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

    /**
     * Note that this can be null.
     */
    public char[] asCharArray() {
        if (value==null) return null;
        return value.toCharArray();
    }

    /**
     * Note that this can be null.
     */
    public Boolean asBoolean() {
        if (value==null) return null;
        return Boolean.parseBoolean(value);
    }

    /**
     * Note that this can be null.
     */
    public Byte asByte() {
        if (value==null) return null;
        return Byte.parseByte(value);
    }

    /**
     * Note that this can be null.
     */
    public Short asShort() {
        if (value==null) return null;
        return Short.parseShort(value);
    }

    /**
     * Note that this can be null.
     */
    public Integer asInt() {
        if (value==null) return null;
        return Integer.parseInt(value);
    }

    /**
     * Note that this can be null.
     */
    public Long asLong() {
        if (value==null) return null;
        return Long.parseLong(value);
    }

    /**
     * Note that this can be null.
     */
    public Float asFloat() {
        if (value==null) return null;
        return Float.parseFloat(value);
    }

    /**
     * Note that this can be null.
     */
    public Double asDouble() {
        if (value==null) return null;
        return Double.parseDouble(value);
    }


    // SETTERS:


    public DYValue set(String value) {
        this.value = value;
        return this;
    }

    public DYValue set(char[] value) {
        this.value = String.valueOf(value);
        return this;
    }

    public DYValue set(boolean value) {
        this.value = String.valueOf(value);
        return this;
    }

    public DYValue set(Boolean value) {
        if (value == null) {
            this.value = null;
            return this;
        }
        this.value = value.toString();
        return this;
    }

    public DYValue set(byte value) {
        this.value = String.valueOf(value);
        return this;
    }

    public DYValue set(Byte value) {
        if (value == null) {
            this.value = null;
            return this;
        }
        this.value = value.toString();
        return this;
    }

    public DYValue set(short value) {
        this.value = String.valueOf(value);
        return this;
    }

    public DYValue set(Short value) {
        if (value == null) {
            this.value = null;
            return this;
        }
        this.value = value.toString();
        return this;
    }

    public DYValue set(int value) {
        this.value = String.valueOf(value);

        return this;
    }

    public DYValue set(Integer value) {
        if (value == null) {
            this.value = null;
            return this;
        }
        this.value = value.toString();
        return this;
    }

    public DYValue set(long value) {
        this.value = String.valueOf(value);
        return this;
    }

    public DYValue set(Long value) {
        if (value == null) {
            this.value = null;
            return this;
        }
        this.value = value.toString();
        return this;
    }

    public DYValue set(float value) {
        this.value = String.valueOf(value);
        return this;
    }

    public DYValue set(Float value) {
        if (value == null) {
            this.value = null;
            return this;
        }
        this.value = value.toString();
        return this;
    }

    public DYValue set(double value) {
        this.value = String.valueOf(value);
        return this;
    }

    public DYValue set(Double value) {
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
