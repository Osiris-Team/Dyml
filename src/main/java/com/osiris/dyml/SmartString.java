package com.osiris.dyml;

/**
 * Wraps around a String and provides additional type conversion methods.
 */
public class SmartString {
    private String string;

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
