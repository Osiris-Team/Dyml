package com.osiris.dyml;

import java.util.Objects;

// TODO WORK IN PROGRESS

/**
 * The in-memory representation of a single value. <br>
 * This value may have a comment.
 */
public class DYValue{
    private String valueAsString;
    private String comment;

    public DYValue(String valueAsString) {
        this(valueAsString, null);
    }

    /**
     * The in-memory representation of a single value. <br>
     * This value may have a comment. <br>
     * @param valueAsString Cannot be null!
     * @param comment Can be null.
     */
    public DYValue(String valueAsString, String comment) {
        this.valueAsString = valueAsString;
        this.comment = comment;
        Objects.requireNonNull(valueAsString);
    }

    // COMMENT STUFF:


    /**
     * Returns true if this value has a comment. <br>
     * See {@link #getComment()} for details.
     */
    public boolean hasComment(){
        return comment!=null;
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


    // VALUE STUFF:


    /**
     * Short for {@link #asString()}.
     */
    public String STR(){
        return valueAsString;
    }
    /**
     * Short for {@link #getValueAsString()}.
     */
    public String asString(){
        return valueAsString;
    }
    public String getValueAsString() {
        return valueAsString;
    }

    public void setValueAsString(String valueAsString) {
        this.valueAsString = valueAsString;
    }


    /**
     * Short for {@link #asCharArray()}.
     */
    public char[] CHA(){
        return asCharArray();
    }
    public char[] asCharArray(){
        return valueAsString.toCharArray();
    }

    /**
     * Short for {@link #asBoolean()}.
     */
    public boolean BOL() {
        return asBoolean();
    }
    public boolean asBoolean(){
        return Boolean.parseBoolean(valueAsString);
    }

    /**
     * Short for {@link #asByte()}.
     */
    public byte BYT() {
        return asByte();
    }
    public byte asByte(){
        return Byte.parseByte(valueAsString);
    }

    /**
     * Short for {@link #asShort()}.
     */
    public short SHR(){
        return asShort();
    }
    public short asShort(){
        return Short.parseShort(valueAsString);
    }

    /**
     * Short for {@link #asInt()}.
     */
    public int INT(){
        return asInt();
    }
    public int asInt(){
        return Integer.parseInt(valueAsString);
    }

    /**
     * Short for {@link #asLong()}.
     */
    public long LNG(){
        return asLong();
    }
    public long asLong(){
        return Long.parseLong(valueAsString);
    }

    /**
     * Short for {@link #asFloat()}.
     */
    public float FLT(){
        return asFloat();
    }
    public float asFloat(){
        return Float.parseFloat(valueAsString);
    }

    /**
     * Short for {@link #asDouble()}.
     */
    public Double DBL(){
        return asDouble();
    }
    public Double asDouble(){
        return Double.parseDouble(valueAsString);
    }

    // TYPE CHECKS:

    public boolean isBoolean(){
        return valueAsString.equalsIgnoreCase("true") || valueAsString.equalsIgnoreCase("false");
    }

    public boolean isByte(){
        try{
            asByte();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isShort(){
        try{
            asShort();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isInt(){
        try{
            asInt();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isLong(){
        try{
            asLong();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isFloat(){
        try{
            asFloat();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isDouble(){
        try{
            asDouble();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

}
