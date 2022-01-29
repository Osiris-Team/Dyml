package com.osiris.dyml;

import java.util.ArrayList;
import java.util.List;

public class DymlSection {
    public String key;
    public SmartString value;
    public List<String> comments;
    public DymlSection parent;
    public List<DymlSection> children;

    public DymlSection(String key, SmartString value, List<String> comments) {
        this.key = key;
        this.value = value;
        this.comments = comments;
        this.children = new ArrayList<>();
    }

    /**
     * Returns the child {@link DymlSection} at the provided index.
     */
    public DymlSection getAt(int index) {
        return children.get(index);
    }

    /**
     * Returns the child with the provided key. If not existing creates one.
     */
    public DymlSection put(String... keys) {

        return null;
    }

    /**
     * Returns a list containing this sections' parent sections. Example:
     * <pre>
     *     g0 val
     *       g1 val
     *         g2 val <---
     * </pre>
     * Returned list: [g1, g0]
     */
    public List<DymlSection> getAllParents(){
        List<DymlSection> parents = new ArrayList<>();
        DymlSection parent = this.parent;
        while(parent!=null){
            parents.add(parent);
            parent = parent.parent;
        }
        return parents;
    }

    public int countParents() {
        int count = 0;
        DymlSection parent = this.parent;
        while(parent!=null){
            count++;
            parent = parent.parent;
        }
        return count;
    }

    public int countSpaces(){
        return countParents() * 2;
    }


    /**
     * Returns the value like its in the yaml file. If its empty there or null, this returns null. <br>
     * Note that this value got post-processed (if enabled). <br>
     * Also note that this is the lowest level you can get to the original yaml value. <br>
     * The lowest level is at {@link DYLine}, but thats only accessible for the {@link YamlReader} and the {@link YamlWriter}. <br>
     */
    public String asString() {
        return value.asString();
    }

    /**
     * Note that this can be null.
     */
    public char[] asCharArray() {
        if (value.asString() == null) return null;
        return value.asString().toCharArray();
    }

    /**
     * Note that this can be null.
     */
    public Boolean asBoolean() {
        if (value.asString() == null) return null;
        return Boolean.parseBoolean(value.asString());
    }

    /**
     * Note that this can be null.
     */
    public Byte asByte() {
        if (value.asString() == null) return null;
        return Byte.parseByte(value.asString());
    }

    /**
     * Note that this can be null.
     */
    public Short asShort() {
        if (value.asString() == null) return null;
        return Short.parseShort(value.asString());
    }

    /**
     * Note that this can be null.
     */
    public Integer asInt() {
        if (value.asString() == null) return null;
        return Integer.parseInt(value.asString());
    }

    /**
     * Note that this can be null.
     */
    public Long asLong() {
        if (value.asString() == null) return null;
        return Long.parseLong(value.asString());
    }

    /**
     * Note that this can be null.
     */
    public Float asFloat() {
        if (value.asString() == null) return null;
        return Float.parseFloat(value.asString());
    }

    /**
     * Note that this can be null.
     */
    public Double asDouble() {
        if (value.asString() == null) return null;
        return Double.parseDouble(value.asString());
    }
}
