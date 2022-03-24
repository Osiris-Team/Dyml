package com.osiris.dyml;

import java.util.Arrays;
import java.util.List;

/**
 * Wraps around a String and provides additional type conversion methods.
 */
public class SmartString {
    private String string;

    public SmartString() {
        this(null);
    }

    public SmartString(String string) {
        this.string = string;
    }

    // GETTERS:

    /**
     * Note that this can be null.
     */
    public String asString() {
        return string;
    }

    /**
     * Note that this can be null.
     */
    public char[] asCharArray() {
        if (string == null) return null;
        return string.toCharArray();
    }

    /**
     * Note that this can be null.
     */
    public Boolean asBoolean() {
        if (string == null) return null;
        return Boolean.parseBoolean(string);
    }

    /**
     * Note that this can be null.
     */
    public Byte asByte() {
        if (string == null) return null;
        return Byte.parseByte(string);
    }

    /**
     * Note that this can be null.
     */
    public Short asShort() {
        if (string == null) return null;
        return Short.parseShort(string);
    }

    /**
     * Note that this can be null.
     */
    public Integer asInt() {
        if (string == null) return null;
        return Integer.parseInt(string);
    }

    /**
     * Note that this can be null.
     */
    public Long asLong() {
        if (string == null) return null;
        return Long.parseLong(string);
    }

    /**
     * Note that this can be null.
     */
    public Float asFloat() {
        if (string == null) return null;
        return Float.parseFloat(string);
    }

    /**
     * Note that this can be null.
     */
    public Double asDouble() {
        if (string == null) return null;
        return Double.parseDouble(string);
    }


    // CUSTOM SETTERS/GETTERS:

    /**
     * String will look like this: <br>
     * <pre>
     *     [val1, val2, val2]
     * </pre>
     */
    public SmartString set(String[] values) {
        string = Arrays.toString(values);
        return this;
    }

    /**
     * String must look something like this: <br>
     * <pre>
     *     [val1, val2, val2]
     *     [val1,val2,val3]
     *     val1, val2, val3
     *     val1,val2,val3
     * </pre>
     * Note that this can be null.
     */
    public String[] asArray() {
        if (string == null) return null;
        if (string.startsWith("[") && string.endsWith("]")) {
            String s = string.substring(1, string.length() - 1); // To remove first [ and last ]
            return s.split(",");
        } else {
            return asArraySplitByColons();
        }
    }

    /**
     * String will look like this: <br>
     * <pre>
     *     [val1, val2, val2]
     * </pre>
     */
    public SmartString set(List<String> values) {
        string = Arrays.toString(values.toArray(new String[0]));
        return this;
    }

    /**
     * String must look something like this: <br>
     * <pre>
     *     [val1, val2, val2]
     *     [val1,val2,val3]
     *     val1, val2, val3
     *     val1,val2,val3
     * </pre>
     * Note that this can be null.
     */
    public List<String> asList() {
        if (string == null) return null;
        return Arrays.asList(asArray());
    }


    /**
     * Note that this can be null.
     */
    public String[] asArraySplitBySpaces() {
        if (string == null) return null;
        return string.split(" ");
    }

    /**
     * Note that this can be null.
     */
    public List<String> asListSplitBySpaces() {
        if (string == null) return null;
        return Arrays.asList(string.split(" "));
    }

    /**
     * Note that this can be null.
     */
    public String[] asArraySplitByColons() {
        if (string == null) return null;
        return string.split(",");
    }

    /**
     * Note that this can be null.
     */
    public List<String> asListSplitByColons() {
        if (string == null) return null;
        return Arrays.asList(string.split(","));
    }


    // SETTERS:


    public SmartString set(String value) {
        this.string = value;
        return this;
    }

    public SmartString set(char[] value) {
        this.string = String.valueOf(value);
        return this;
    }

    public SmartString set(boolean value) {
        this.string = String.valueOf(value);
        return this;
    }

    public SmartString set(Boolean value) {
        if (value == null) {
            this.string = null;
            return this;
        }
        this.string = value.toString();
        return this;
    }

    public SmartString set(byte value) {
        this.string = String.valueOf(value);
        return this;
    }

    public SmartString set(Byte value) {
        if (value == null) {
            this.string = null;
            return this;
        }
        this.string = value.toString();
        return this;
    }

    public SmartString set(short value) {
        this.string = String.valueOf(value);
        return this;
    }

    public SmartString set(Short value) {
        if (value == null) {
            this.string = null;
            return this;
        }
        this.string = value.toString();
        return this;
    }

    public SmartString set(int value) {
        this.string = String.valueOf(value);

        return this;
    }

    public SmartString set(Integer value) {
        if (value == null) {
            this.string = null;
            return this;
        }
        this.string = value.toString();
        return this;
    }

    public SmartString set(long value) {
        this.string = String.valueOf(value);
        return this;
    }

    public SmartString set(Long value) {
        if (value == null) {
            this.string = null;
            return this;
        }
        this.string = value.toString();
        return this;
    }

    public SmartString set(float value) {
        this.string = String.valueOf(value);
        return this;
    }

    public SmartString set(Float value) {
        if (value == null) {
            this.string = null;
            return this;
        }
        this.string = value.toString();
        return this;
    }

    public SmartString set(double value) {
        this.string = String.valueOf(value);
        return this;
    }

    public SmartString set(Double value) {
        if (value == null) {
            this.string = null;
            return this;
        }
        this.string = value.toString();
        return this;
    }


    // TYPE CHECKS:


    public boolean isBoolean() {
        return string.equalsIgnoreCase("true") || string.equalsIgnoreCase("false");
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
