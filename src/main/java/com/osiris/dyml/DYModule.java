/*
 *  Copyright Osiris Team
 *  All rights reserved.
 *
 *  This software is licensed work.
 *  Please consult the file "LICENSE" for details.
 */

package com.osiris.dyml;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * The in-memory representation of a yaml section.
 * Contains information about its keys, values and comments.
 */
public class DYModule {
    private List<String> keys;
    private List<String> values;
    private List<String> defaultValues;
    private List<String> comments;
    private DYLine line;
    private boolean fallbackOnDefault = true;

    /**
     * See {@link #DYModule(List, List, List, List)} for details.
     */
    public DYModule(){
        this((String) null);
    }

    /**
     * See {@link #DYModule(List, List, List, List)} for details.
     */
    public DYModule(String... keys){
        List<String> list = new ArrayList<>();
        if (keys!=null) list.addAll(Arrays.asList(keys));
       init(list,null, null, null);
    }

    /**
     * Creates a new module.
     * Null values are allowed for creation, but should be replaced with actual values later.
     * @param keys a list containing its keys.
     * @param defaultValues a list containing its default values.
     * @param values a list containing its values.
     * @param comments a list containing its comments.
     */
    public DYModule(List<String> keys, List<String> defaultValues, List<String> values, List<String> comments) {
        init(keys, defaultValues, values, comments);
    }

    private void init(List<String> keys, List<String> defaultValues, List<String> values, List<String> comments){
        this.keys = new ArrayList<>();
        this.values = new ArrayList<>();
        this.defaultValues = new ArrayList<>();
        this.comments = new ArrayList<>();

        // To make sure that no null keys get added to the keys list
        if (keys!=null) {
            for (String s :
                    keys) {
                if (s != null)
                    this.keys.add(s);
            }
        }
        if (defaultValues!=null) this.defaultValues.addAll(defaultValues);
        if (values!=null) this.values.addAll(values);
        if (comments!=null) this.comments.addAll(comments);
    }

    /**
     * Prints out this modules most important details.
     */
    public void print(){
        System.out.println(
                "KEYS: " + this.getKeys().toString() +
                        " VALUES: " + this.getValues().toString() +
                        " DEF-VALUES: " + this.getDefaultValues().toString() +
                        " COMMENTS: " + this.getComments().toString());
    }

    /**
     * See {@link #setFallbackOnDefault(boolean)} for details.
     */
    public boolean isFallbackOnDefault() {
        return fallbackOnDefault;
    }

    /**
     * Null values return their default values as fallback.
     * This is enabled by default.
     * See {@link #getValueByIndex(int)} for details.
     */
    public void setFallbackOnDefault(boolean fallbackOnDefault) {
        this.fallbackOnDefault = fallbackOnDefault;
    }

    /**
     * See {@link #setKeys(List)} for details.
     */
    public DYModule setKey(String key) {
        setKeys(key);
        return this;
    }

    /**
     * See {@link #setKeys(List)} for details.
     */
    public DYModule setKeys(String... keys) {
        if (keys!=null) return setKeys(Arrays.asList(keys));
        return this;
    }

    /**
     * Clears the list and adds the given keys.
     * Duplicate keys are not allowed,
     * because its the only way of distinguishing modules.
     */
    public DYModule setKeys(List<String> keys) {
        if (keys!=null){
            this.keys.clear();
            this.keys.addAll(keys);
        }
        return this;
    }

    /**
     * See {@link #addKeys(String...)} for details.
     */
    public DYModule addKey(String key){
        addKeys(key);
        return this;
    }

    /**
     * Adds a new key to the list.
     * Duplicate keys are not allowed,
     * because its the only way of distinguishing modules.
     */
    public DYModule addKeys(String... keys){
        if (keys!=null) this.keys.addAll(Arrays.asList(keys));
        return this;
    }

    /**
     * See {@link #setValues(List)} for details.
     */
    public DYModule setValue(String v){
        setValues(v);
        return this;
    }

    /**
     * See {@link #setValues(List)} for details.
     */
    public DYModule setValues(String... v){
        if (v!=null) setValues(Arrays.asList(v));
        return this;
    }

    /**
     * Clears the values list and adds the values from the provided list.
     */
    public DYModule setValues(List<String> v){
        if (v!=null){
            this.values.clear();
            this.values.addAll(v);
        }
        return this;
    }

    /**
     * See {@link #addValues(String...)} for details.
     */
    public DYModule addValue(String v){
        addValues(v);
        return this;
    }

    /**
     * See {@link #addValues(String...)} for details.
     */
    public DYModule addValues(String... v){
        if (v!=null) addValues(Arrays.asList(v));
        return this;
    }

    /**
     * Adds new values to the list.
     * Note that null values wont be added!
     */
    public DYModule addValues(List<String> v){
        if (v!=null){
            this.values.addAll(v);
        }
        return this;
    }

    /**
     * See {@link #setDefValues(String...)} for details.
     */
    public DYModule setDefValue(String v){
        setDefValues(v);
        return this;
    }

    /**
     * See {@link #setDefValues(List)} for details.
     */
    public DYModule setDefValues(String... v){
        if (v!=null)
            setDefValues(Arrays.asList(v));
        return this;
    }

    /**
     * The default value is used when the normal value is null/empty or the key didn't exist yet.
     * See {@link #setFallbackOnDefault(boolean)} for more details.
     * Note that null values wont be added!
     */
    public DYModule setDefValues(List<String> v){
        if (v!=null) {
            this.defaultValues.clear();
            this.defaultValues.addAll(v);
        }
        return this;
    }

    /**
     * {@link #addDefValues(List)}
     */
    public DYModule addDefValue(String v){
        addDefValues(v);
        return this;
    }

    /**
     * {@link #addDefValues(List)}
     */
    public DYModule addDefValues(String... v){
        if (v!=null)
            addDefValues(Arrays.asList(v));
        return this;
    }

    /**
     * Adds new default values to the list.
     * Note that null values wont be added!
     */
    public DYModule addDefValues(List<String> v){
        if (v!=null) {
            this.defaultValues.addAll(v);
        }
        return this;
    }

    public DYModule setComment(String c){
        setComments(c);
        return this;
    }

    public DYModule setComments(String... c){
        if (c!=null) setComments(Arrays.asList(c));
        return this;
    }

    public DYModule setComments(List<String> c){
        if (c!=null) {
            this.comments.clear();
            this.comments.addAll(c);
        }
        return this;
    }

    public DYModule addComment(String c){
        addComments(c);
        return this;
    }

    public DYModule addComments(String... c){
        if (c!=null)
            this.comments.addAll(Arrays.asList(c));
        return this;
    }

    /**
     * Returns the first key located at index 0.
     */
    public String getKey(){
        return getKeyByIndex(0);
    }

    /**
     * Returns the key by given index or
     * null if there was no index i in the list.
     */
    public String getKeyByIndex(int i){
        try{
            return keys.get(i);
        } catch (Exception ignored) {}
        return null;
    }

    /**
     * Returns all keys.
     * Their order is essential for a correct yaml file.
     */
    public List<String> getKeys() {
        return keys;
    }

    /**
     * Returns the 'real' value from the yaml file
     * at the time when load() was called.
     */
    public String getValue(){
        return getValueByIndex(0);
    }

    /**
     * Returns the value by given index or
     * its default value, if the value is null/empty and {@link #isFallbackOnDefault()} is set to true.
     * Note that there can't be null values! All null values are regarded as empty!
     * This is because a null String will return 'null' and not actually null.
     * To avoid that an empty string is returned instead.
     */
    public String getValueByIndex(int i){
        String v = null;
        try{
            v = values.get(i);
        } catch (Exception ignored) {}

        if (v==null && fallbackOnDefault) return getDefaultValueByIndex(i);
        return v;
    }

    public List<String> getValues() {
        return values;
    }

    public String getDefaultValue(){
        return getDefaultValueByIndex(0);
    }

    public String getDefaultValueByIndex(int i){
        String v = null;
        try{
            v = defaultValues.get(i);
        } catch (Exception ignored) {}
        return v;
    }

    public List<String> getDefaultValues() {
        return defaultValues;
    }

    /**
     * Returns the first comment at index 0.
     */
    public String getComment(){
        return getCommentByIndex(0);
    }

    /**
     * Returns a specific comment by its index or null if nothing found at that index.
     */
    public String getCommentByIndex(int i){
        try{
            return comments.get(i);
        } catch (Exception ignored) {}
        return null;
    }

    public List<String> getComments() {
        return comments;
    }

    public DYLine getLine() {
        return line;
    }

    public void setLine(DYLine line) {
        this.line = line;
    }

    public String asString(){
        return asString(0);
    }

    public String asString(int i){
        return getValueByIndex(i);
    }

    public List<String> asStringList(){
        return this.values;
    }

    public char[] asCharArray(){
        return asCharArray(0);
    }

    public char[] asCharArray(int i){
        return asString(i).toCharArray();
    }

    public boolean asBoolean(){
        return asBoolean(0);
    }

    public boolean asBoolean(int i){
        return Boolean.parseBoolean(asString(i));
    }

    public byte asByte(){
        return asByte(0);
    }

    public byte asByte(int i){
        return Byte.parseByte(asString(i));
    }

    public short asShort(){
        return asShort(0);
    }

    public short asShort(int i){
        return Short.parseShort(asString(i));
    }

    public int asInt(){
        return asInt(0);
    }

    public int asInt(int i){
        return Integer.parseInt(asString(i));
    }

    public long asLong(){
        return asLong(0);
    }

    public long asLong(int i){
        return Long.parseLong(asString(i));
    }

    public float asFloat(){
        return asFloat(0);
    }

    public float asFloat(int i){
        return Float.parseFloat(asString(i));
    }

    public Double asDouble(){
        return asDouble(0);
    }

    public Double asDouble(int i){
        return Double.parseDouble(asString(i));
    }
}
