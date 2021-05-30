/*
 *  Copyright Osiris Team
 *  All rights reserved.
 *
 *  This software is licensed work.
 *  Please consult the file "LICENSE" for details.
 */

package com.osiris.dyml;


import com.osiris.dyml.exceptions.DuplicateKeyException;
import com.osiris.dyml.utils.UtilsDYModule;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * The in-memory representation of a yaml section.
 * Contains information about its keys, values and comments.
 */
public class DYModule {
    private final UtilsDYModule utils = new UtilsDYModule();
    private List<String> keys;
    private List<DYValue> values;
    private List<DYValue> defaultValues;
    private List<String> comments;
    private boolean isReturnDefaultWhenValueIsNullEnabled = false;
    private boolean isWriteDefaultWhenValuesListIsEmptyEnabled = true;

    private List<DYModule> parentModules = new ArrayList<>();
    private List<DYModule> childModules = new ArrayList<>();

    /**
     * See {@link #DYModule(List, List, List, List)} for details.
     */
    public DYModule() {
        this((String[]) null);
    }

    /**
     * See {@link #DYModule(List, List, List, List)} for details.
     */
    public DYModule(String... keys) {
        List<String> list = new ArrayList<>();
        if (keys != null) list.addAll(Arrays.asList(keys));
        init(list, null, null, null);
    }

    /**
     * Creates a new module.
     * Null values are allowed for creation, but should be replaced with actual values later.
     *
     * @param keys          a list containing its keys. Pass over null to create a new list.
     *                      Note that you must add at least one key, otherwise u can't
     *                      save/parse this module.
     * @param defaultValues a list containing its default values. Pass over null to create a new list.
     * @param values        a list containing its values. Pass over null to create a new list.
     * @param comments      a list containing its comments. Pass over null to create a new list.
     */
    public DYModule(List<String> keys, List<DYValue> defaultValues, List<DYValue> values, List<String> comments) {
        init(keys, defaultValues, values, comments);
    }

    private void init(List<String> keys, List<DYValue> defaultValues, List<DYValue> values, List<String> comments) {
        this.keys = keys;
        this.values = values;
        this.defaultValues = defaultValues;
        this.comments = comments;
        if (keys == null) this.keys = new ArrayList<>();
        if (defaultValues == null) this.defaultValues = new ArrayList<>();
        if (values == null) this.values = new ArrayList<>();
        if (comments == null) this.comments = new ArrayList<>();
    }

    /**
     * Prints out this modules most important details.
     */
    public DYModule print() {
        System.out.println(getModuleInformationAsString());
        return this;
    }

    /**
     * Formats this module into a {@link String}.
     */
    public String getModuleInformationAsString() {
        StringBuilder s = new StringBuilder("KEYS: " + this.getKeys().toString() +
                " VALUES: " + utils.valuesListToStringList(this.getValues()).toString() +
                " DEF-VALUES: " + utils.valuesListToStringList(this.getDefaultValues()).toString() +
                " COMMENTS: " + this.getComments().toString());

        // add side comments
        s.append(" SIDE-COMMENTS: ");
        for (DYValue value :
                getValues()) {
            if (value != null && value.hasComment())
                s.append(" #").append(value.getComment());
        }
        return s.toString();
    }

    /**
     * Adds a new key to the list. <br>
     * Duplicate keys and null keys are not allowed.
     */
    public DYModule addKeys(String... keys) {
        for (String key :
                keys) {
            Objects.requireNonNull(key);
            this.keys.add(key);
        }
        return this;
    }

    /**
     * See {@link #addValues(List)} for details.
     */
    public DYModule addValues(String... v) throws DuplicateKeyException {
        addValues(utils.stringArrayToValuesList(v));
        return this;
    }

    /**
     * See {@link #addValues(List)} for details.
     */
    public DYModule addValues(DYValue... v) throws DuplicateKeyException {
        addValues(Arrays.asList(v));
        return this;
    }

    /**
     * Adds new values to the list. <br>
     * Checks for duplicate keys, if the value is a {@link DYModule}.
     */
    public DYModule addValues(List<DYValue> v) {
        this.values.addAll(v);
        return this;
    }

    /**
     * See {@link #setDefValues(List)} for details.
     */
    public DYModule setDefValues(String... v) {
        setDefValues(utils.stringArrayToValuesList(v));
        return this;
    }

    /**
     * See {@link #setDefValues(List)} for details.
     */
    public DYModule setDefValues(DYValue... v) {
        setDefValues(Arrays.asList(v));
        return this;
    }

    /**
     * The default values are written to the yaml file, when there were no regular values set/added. <br>
     * Further details: <br>
     * {@link #isWriteDefaultWhenValuesListIsEmptyEnabled()} <br>
     * {@link #isReturnDefaultWhenValueIsNullEnabled()} <br>
     */
    public DYModule setDefValues(List<DYValue> v) {
        this.defaultValues.clear();
        this.defaultValues.addAll(v);
        return this;
    }

    /**
     * Converts the provided string array, into a {@link DYValue}s list. <br>
     * See {@link #addDefValues(List)} for details.
     */
    public DYModule addDefValues(String... v) {
        if (v != null)
            addDefValues(utils.stringArrayToValuesList(v));
        return this;
    }

    /**
     * {@link #addDefValues(List)}
     */
    public DYModule addDefValues(DYValue... v) {
        if (v != null)
            addDefValues(Arrays.asList(v));
        return this;
    }

    /**
     * Adds new default {@link DYValue}s to the list. <br>
     * Note that the list cannot contain null {@link DYValue}s. <br>
     * {@link DYValue#asString()} may return null though.
     */
    public DYModule addDefValues(List<DYValue> v) {
        if (v != null) {
            this.defaultValues.addAll(v);
        }
        return this;
    }


    public DYModule addComments(String... c) {
        if (c != null)
            this.comments.addAll(Arrays.asList(c));
        return this;
    }

    public UtilsDYModule getUtils() {
        return utils;
    }

    /**
     * Returns the first key located at index 0.
     */
    public String getKey() {
        return getKeyByIndex(0);
    }


    /**
     * Returns the key by given index or
     * null if there was no index i in the list.
     */
    public String getKeyByIndex(int i) {
        try {
            return keys.get(i);
        } catch (Exception ignored) {
        }
        return null;
    }

    /**
     * Returns all keys. Their order is essential for a correct yaml file. <br>
     */
    public List<String> getKeys() {
        return keys;
    }

    /**
     * See {@link #setKeys(List)} for details.
     */
    public DYModule setKeys(String... keys) {
        if (keys != null) return setKeys(Arrays.asList(keys));
        return this;
    }

    /**
     * Clears the list and adds the given keys.
     * Duplicate keys are not allowed,
     * because its the only way of distinguishing modules.
     */
    public DYModule setKeys(List<String> keys) {
        if (keys != null) {
            this.keys.clear();
            this.keys.addAll(keys);
        }
        return this;
    }

    /**
     * Returns the 'real' value from the yaml file
     * at the time when load() was called.
     */
    public DYValue getValue() {
        return getValueByIndex(0);
    }


    /**
     * Returns the value by given index or
     * its default value, if the value is null/empty and {@link #isReturnDefaultWhenValueIsNullEnabled()} is set to true.
     */
    public DYValue getValueByIndex(int i) {
        DYValue v = null;
        try {
            v = values.get(i);
        } catch (Exception ignored) {
        }

        if (v == null && isReturnDefaultWhenValueIsNullEnabled)
            return getDefaultValueByIndex(i);
        return v;
    }

    public List<DYValue> getValues() {
        return values;
    }

    /**
     * See {@link #setValues(List)} for details.
     */
    public DYModule setValues(String... v) {
        setValues(utils.stringArrayToValuesList(v));
        return this;
    }

    /**
     * See {@link #setValues(List)} for details.
     */
    public DYModule setValues(DYValue... v) {
        setValues(Arrays.asList(v));
        return this;
    }

    /**
     * Clears the values list and adds the values from the provided list. <br>
     * Note that the list cannot contain null {@link DYValue}s. <br>
     * {@link DYValue#asString()} may return null though.
     */
    public DYModule setValues(List<DYValue> v) {
        this.values.clear();
        addValues(v);
        return this;
    }

    /**
     * Returns the first {@link DYValue} in the default values list.
     */
    public DYValue getDefaultValue() {
        return getDefaultValueByIndex(0);
    }

    /**
     * Returns the {@link DYValue} at index i in the default values list.
     */
    public DYValue getDefaultValueByIndex(int i) {
        DYValue v = null;
        try {
            v = defaultValues.get(i);
        } catch (Exception ignored) {
        }
        return v;
    }

    public List<DYValue> getDefaultValues() {
        return defaultValues;
    }

    /**
     * Returns the first comment at index 0.
     */
    public String getComment() {
        return getCommentByIndex(0);
    }

    /**
     * Returns a specific comment by its index or null if nothing found at that index.
     */
    public String getCommentByIndex(int i) {
        try {
            return comments.get(i);
        } catch (Exception ignored) {
        }
        return null;
    }

    public List<String> getComments() {
        return comments;
    }

    public DYModule setComments(String... c) {
        if (c != null) setComments(Arrays.asList(c));
        return this;
    }

    public DYModule setComments(List<String> c) {
        if (c != null) {
            this.comments.clear();
            this.comments.addAll(c);
        }
        return this;
    }

    /**
     * Shortcut for retrieving this {@link DYModule}s first {@link DYValue} as string.
     */
    public String asString() {
        return asString(0);
    }

    public String asString(int i) {
        return getValueByIndex(i).asString();
    }

    /**
     * Shortcut for retrieving this {@link DYModule}s first {@link DYValue}.
     */
    public DYValue asDYValue() {
        return asDYValue(0);
    }

    public DYValue asDYValue(int i) {
        return getValueByIndex(i);
    }

    /**
     * Note that this is a copy and not the original list.
     */
    public List<String> asStringList() {
        return utils.valuesListToStringList(this.values);
    }

    /**
     * Shortcut for retrieving this {@link DYModule}s first {@link DYValue} as char-array.
     */
    public char[] asCharArray() {
        return asCharArray(0);
    }

    public char[] asCharArray(int i) {
        return getValueByIndex(i).asCharArray();
    }

    /**
     * Shortcut for retrieving this {@link DYModule}s first {@link DYValue} as boolean.
     */
    public boolean asBoolean() {
        return asBoolean(0);
    }

    public boolean asBoolean(int i) {
        return getValueByIndex(i).asBoolean();
    }

    /**
     * Shortcut for retrieving this {@link DYModule}s first {@link DYValue} as byte.
     */
    public byte asByte() {
        return asByte(0);
    }

    public byte asByte(int i) {
        return getValueByIndex(i).asByte();
    }

    /**
     * Shortcut for retrieving this {@link DYModule}s first {@link DYValue} as short.
     */
    public short asShort() {
        return asShort(0);
    }

    public short asShort(int i) {
        return getValueByIndex(i).asShort();
    }

    /**
     * Shortcut for retrieving this {@link DYModule}s first {@link DYValue} as int.
     */
    public int asInt() {
        return asInt(0);
    }

    public int asInt(int i) {
        return getValueByIndex(i).asInt();
    }

    /**
     * Shortcut for retrieving this {@link DYModule}s first {@link DYValue} as long.
     */
    public long asLong() {
        return asLong(0);
    }

    public long asLong(int i) {
        return getValueByIndex(i).asLong();
    }

    /**
     * Shortcut for retrieving this {@link DYModule}s first {@link DYValue} as float.
     */
    public float asFloat() {
        return asFloat(0);
    }

    public float asFloat(int i) {
        return getValueByIndex(i).asFloat();
    }

    /**
     * Shortcut for retrieving this {@link DYModule}s first {@link DYValue} as double.
     */
    public Double asDouble() {
        return asDouble(0);
    }

    public Double asDouble(int i) {
        return getValueByIndex(i).asDouble();
    }

    /**
     * <p style="color:red;">Do not modify this list directly, unless you know what you are doing!</p>
     * A list containing this modules parent modules, aka the generation before. <br>
     * Note that this list does NOT contain generations beyond that. <br>
     * More about generations here: {@link DYReader#parseLine(DreamYaml, DYLine)}.
     */
    public List<DYModule> getParentModules() {
        return parentModules;
    }

    /**
     * <p style="color:red;">Do not modify this list directly, unless you know what you are doing!</p>
     * A list containing this modules parent modules, aka the generation before. <br>
     * Note that this list does NOT contain generations beyond that. <br>
     * More about generations here: {@link DYReader#parseLine(DreamYaml, DYLine)}.
     */
    public DYModule setParentModules(List<DYModule> parentModules) {
        this.parentModules = parentModules;
        return this;
    }

    public DYModule addParentModules(DYModule... pModules) {
        Objects.requireNonNull(pModules);
        parentModules.addAll(Arrays.asList(pModules));
        return this;
    }

    /**
     * <p style="color:red;">Do not modify this list directly, unless you know what you are doing!</p>
     * A list containing this modules child modules, aka the next generation. <br>
     * Note that this list does NOT contain generations beyond that. <br>
     * More about generations here: {@link DYReader#parseLine(DreamYaml, DYLine)}.
     */
    public List<DYModule> getChildModules() {
        return childModules;
    }

    /**
     * <p style="color:red;">Do not modify this list directly, unless you know what you are doing!</p>
     * A list containing this modules child modules, aka the next generation. <br>
     * Note that this list does NOT contain generations beyond that. <br>
     * More about generations here: {@link DYReader#parseLine(DreamYaml, DYLine)}.
     */
    public DYModule setChildModules(List<DYModule> childModules) {
        this.childModules = childModules;
        return this;
    }

    public DYModule addChildModules(DYModule... cModules) {
        Objects.requireNonNull(cModules);
        childModules.addAll(Arrays.asList(cModules));
        return this;
    }

    /**
     * Disabled by default. <br>
     * Null values return their default values as fallback.<br>
     * See {@link #getValueByIndex(int)} for details.
     */
    public boolean isReturnDefaultWhenValueIsNullEnabled() {
        return isReturnDefaultWhenValueIsNullEnabled;
    }

    /**
     * Disabled by default. <br>
     * Null values return their default values as fallback. <br>
     * See {@link #getValueByIndex(int)} for details.
     */
    public DYModule setReturnDefaultWhenValueIsNullEnabled(boolean returnDefaultWhenValueIsNullEnabled) {
        this.isReturnDefaultWhenValueIsNullEnabled = returnDefaultWhenValueIsNullEnabled;
        return this;
    }

    /**
     * Enabled by default. <br>
     * If there are no values to write, write the default values.
     */
    public boolean isWriteDefaultWhenValuesListIsEmptyEnabled() {
        return isWriteDefaultWhenValuesListIsEmptyEnabled;
    }

    /**
     * Enabled by default. <br>
     * If there are no values to write, write the default values.
     */
    public DYModule setWriteDefaultWhenValuesListIsEmptyEnabled(boolean writeDefaultWhenValuesListIsEmptyEnabled) {
        isWriteDefaultWhenValuesListIsEmptyEnabled = writeDefaultWhenValuesListIsEmptyEnabled;
        return this;
    }
}
