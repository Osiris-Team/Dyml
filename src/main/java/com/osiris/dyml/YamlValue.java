package com.osiris.dyml;

/**
 * The in-memory representation of a single value. <br>
 * Note that this class acts as some sort of 'container' that holds
 * the actual String value and thus can never be null, but the value can (see {@link #get()} for details). <br>
 * This class also provides methods for working with the value as different data-types. <br>
 * This value may have a comment (side comment).
 */
@SuppressWarnings("ALL")
public class YamlValue {
    private String value;
    private String comment;
    private String defaultComment;

    public YamlValue(String value) {
        this(value, null);
    }

    public YamlValue(char[] value) {
        this(String.valueOf(value), null);
    }

    public YamlValue(boolean value) {
        this(String.valueOf(value), null);
    }

    public YamlValue(Boolean value) {
        this(value.toString(), null);
    }

    public YamlValue(byte value) {
        this(String.valueOf(value), null);
    }

    public YamlValue(Byte value) {
        this(value.toString(), null);
    }

    public YamlValue(short value) {
        this(String.valueOf(value), null);
    }

    public YamlValue(Short value) {
        this(value.toString(), null);
    }

    public YamlValue(int value) {
        this(String.valueOf(value), null);
    }

    public YamlValue(Integer value) {
        this(value.toString(), null);
    }

    public YamlValue(long value) {
        this(String.valueOf(value), null);
    }

    public YamlValue(Long value) {
        this(value.toString(), null);
    }

    public YamlValue(float value) {
        this(String.valueOf(value), null);
    }

    public YamlValue(Float value) {
        this(value.toString(), null);
    }

    public YamlValue(double value) {
        this(String.valueOf(value), null);
    }

    public YamlValue(Double value) {
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
    public YamlValue(String value, String comment) {
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
    public YamlValue(String value, String comment, String defComment) {
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
    public YamlValue setComment(String comment) {
        if (comment != null)
            comment = comment.replace(System.lineSeparator(), "");
        this.comment = comment;
        return this;
    }

    /**
     * Line separators get removed.
     * See {@link #getComment()} for details.
     */
    public YamlValue setDefComment(String defComment) {
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
     * The lowest level is at {@link DYLine}, but thats only accessible for the {@link YamlReader} and the {@link YamlWriter}. <br>
     */
    public String get() {
        return value;
    }

    /**
     * Same as {@link #get()}. <br>
     * Returns the value like its in the yaml file. If its empty there or null, this returns null. <br>
     * Note that this value got post-processed (if enabled). <br>
     * Also note that this is the lowest level you can get to the original yaml value. <br>
     * The lowest level is at {@link DYLine}, but thats only accessible for the {@link YamlReader} and the {@link YamlWriter}. <br>
     */
    public String asString() {
        return value;
    }

    /**
     * Note that this can be null.
     */
    public char[] asCharArray() {
        if (value == null) return null;
        return value.toCharArray();
    }

    /**
     * Note that this can be null.
     */
    public Boolean asBoolean() {
        if (value == null) return null;
        return Boolean.parseBoolean(value);
    }

    /**
     * Note that this can be null.
     */
    public Byte asByte() {
        if (value == null) return null;
        return Byte.parseByte(value);
    }

    /**
     * Note that this can be null.
     */
    public Short asShort() {
        if (value == null) return null;
        return Short.parseShort(value);
    }

    /**
     * Note that this can be null.
     */
    public Integer asInt() {
        if (value == null) return null;
        return Integer.parseInt(value);
    }

    /**
     * Note that this can be null.
     */
    public Long asLong() {
        if (value == null) return null;
        return Long.parseLong(value);
    }

    /**
     * Note that this can be null.
     */
    public Float asFloat() {
        if (value == null) return null;
        return Float.parseFloat(value);
    }

    /**
     * Note that this can be null.
     */
    public Double asDouble() {
        if (value == null) return null;
        return Double.parseDouble(value);
    }


    // SETTERS:


    public YamlValue set(String value) {
        this.value = value;
        return this;
    }

    public YamlValue set(char[] value) {
        this.value = String.valueOf(value);
        return this;
    }

    public YamlValue set(boolean value) {
        this.value = String.valueOf(value);
        return this;
    }

    public YamlValue set(Boolean value) {
        if (value == null) {
            this.value = null;
            return this;
        }
        this.value = value.toString();
        return this;
    }

    public YamlValue set(byte value) {
        this.value = String.valueOf(value);
        return this;
    }

    public YamlValue set(Byte value) {
        if (value == null) {
            this.value = null;
            return this;
        }
        this.value = value.toString();
        return this;
    }

    public YamlValue set(short value) {
        this.value = String.valueOf(value);
        return this;
    }

    public YamlValue set(Short value) {
        if (value == null) {
            this.value = null;
            return this;
        }
        this.value = value.toString();
        return this;
    }

    public YamlValue set(int value) {
        this.value = String.valueOf(value);

        return this;
    }

    public YamlValue set(Integer value) {
        if (value == null) {
            this.value = null;
            return this;
        }
        this.value = value.toString();
        return this;
    }

    public YamlValue set(long value) {
        this.value = String.valueOf(value);
        return this;
    }

    public YamlValue set(Long value) {
        if (value == null) {
            this.value = null;
            return this;
        }
        this.value = value.toString();
        return this;
    }

    public YamlValue set(float value) {
        this.value = String.valueOf(value);
        return this;
    }

    public YamlValue set(Float value) {
        if (value == null) {
            this.value = null;
            return this;
        }
        this.value = value.toString();
        return this;
    }

    public YamlValue set(double value) {
        this.value = String.valueOf(value);
        return this;
    }

    public YamlValue set(Double value) {
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
