/*
 *  Copyright Osiris Team
 *  All rights reserved.
 *
 *  This software is licensed work.
 *  Please consult the file "LICENSE" for details.
 */

package com.osiris.dyml;


import com.osiris.dyml.exceptions.IllegalKeyException;
import com.osiris.dyml.exceptions.NotLoadedException;
import com.osiris.dyml.utils.UtilsYamlSection;

import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * The in-memory representation of a yaml section.
 * Contains information about its keys, values and comments.
 */
@SuppressWarnings("ALL")
public class YamlSection {
    private final UtilsYamlSection utils = new UtilsYamlSection();
    private Yaml yaml;
    private List<String> keys;
    private List<SmartString> values;
    private List<SmartString> defaultValues;
    private List<String> comments;
    private List<String> defaultComments;
    private List<String> sideComments;
    private List<String> defaultSideComments;
    private int countTopLineBreaks;

    private YamlSection parentSection = null;
    private List<YamlSection> childSections = new ArrayList<>();

    /**
     * See {@link #YamlSection(Yaml, List, List, List, List)} for details.
     */
    public YamlSection(Yaml yaml) {
        this(yaml, (String[]) null);
    }

    /**
     * See {@link #YamlSection(Yaml, List, List, List, List)} for details.
     */
    public YamlSection(Yaml yaml, String... keys) {
        List<String> list = new ArrayList<>();
        if (keys != null) list.addAll(Arrays.asList(keys));
        init(yaml, list, null, null, null);
    }

    /**
     * Creates a new module.
     * Null values are allowed for creation, but should be replaced with actual values later.
     *
     * @param yaml          this modules yaml file.
     * @param keys          a list containing its keys. Pass over null to create a new list.
     *                      Note that you must add at least one key, otherwise u can't
     *                      save/parse this module.
     * @param defaultValues a list containing its default values. Pass over null to create a new list.
     * @param values        a list containing its values. Pass over null to create a new list.
     * @param comments      a list containing its comments. Pass over null to create a new list.
     */
    public YamlSection(Yaml yaml, List<String> keys, List<SmartString> defaultValues, List<SmartString> values, List<String> comments) {
        init(yaml, keys, defaultValues, values, comments);
    }

    private void init(Yaml yaml, List<String> keys, List<SmartString> defaultValues, List<SmartString> values, List<String> comments) {
        this.yaml = yaml;
        this.keys = keys;
        this.values = values;
        this.defaultValues = defaultValues;
        this.comments = comments;
        if (keys == null) this.keys = new ArrayList<>();
        if (defaultValues == null) this.defaultValues = new ArrayList<>();
        if (values == null) this.values = new ArrayList<>();
        if (comments == null) this.comments = new ArrayList<>();
        this.defaultComments = new ArrayList<>(1);
        this.sideComments = new ArrayList<>(1);
        this.defaultSideComments = new ArrayList<>(1);
    }

    /**
     * Returns the yaml file this module is in.
     */
    public Yaml getYaml() {
        return yaml;
    }

    /**
     * Prints out this modules most important details.
     */
    public YamlSection print() {
        System.out.println(toPrintString());
        return this;
    }

    /**
     * Formats this module into a {@link String}.
     */
    public String toPrintString() {
        String s = "KEYS: " + this.getKeys().toString() +
                " VALUES: " + utils.valuesListToStringList(this.getValues()).toString() +
                " DEF-VALUES: " + utils.valuesListToStringList(this.getDefValues()).toString() +
                " COMMENTS: " + this.getComments().toString() +
                " SIDE-COMMENTS: " + this.getSideComments().toString();
        return s;
    }


    // REMOVE METHODS:


    /**
     * Clears the {@link #keys} list.
     */
    public YamlSection removeAllKeys() {
        keys.clear();
        return this;
    }

    /**
     * Clears the {@link #values} list.
     */
    public YamlSection removeAllValues() {
        values.clear();
        return this;
    }

    /**
     * Clears the {@link #defaultValues} list.
     */
    public YamlSection removeAllDefValues() {
        defaultValues.clear();
        return this;
    }

    /**
     * Clears the {@link #comments} list.
     */
    public YamlSection removeAllComments() {
        comments.clear();
        return this;
    }


    // ADD METHODS:


    /**
     * Adds a new key to the list. <br>
     * Duplicate keys and null keys are not allowed.
     */
    public YamlSection addKeys(String... keys) {
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
    public YamlSection addValues(String... v) {
        if (v != null)
            addValues(utils.stringArrayToValuesList(v));
        else
            addValues((List<SmartString>) null);
        return this;
    }

    /**
     * See {@link #addValues(List)} for details.
     */
    public YamlSection addValues(SmartString... v) {
        if (v != null)
            addValues(Arrays.asList(v));
        else
            addValues((List<SmartString>) null);
        return this;
    }

    /**
     * Adds new values to the list. <br>
     */
    public YamlSection addValues(List<SmartString> v) {
        if (v != null) {
            for (SmartString value :
                    v) {
                Objects.requireNonNull(value);
            }
            this.values.addAll(v);
        } else
            this.values.add(new SmartString((String) null));

        return this;
    }

    public YamlSection addComments(String... c) {
        if (c != null)
            this.comments.addAll(Arrays.asList(c));
        return this;
    }

    public YamlSection addSideComments(String... c) {
        if (c != null)
            this.sideComments.addAll(Arrays.asList(c));
        return this;
    }

    /**
     * Converts the provided string array, into a {@link SmartString}s list. <br>
     * See {@link #addDefValues(List)} for details.
     */
    public YamlSection addDefValues(String... v) {
        if (v != null)
            addDefValues(utils.stringArrayToValuesList(v));
        else
            addDefValues((List<SmartString>) null);
        return this;
    }

    /**
     * {@link #addDefValues(List)}
     */
    public YamlSection addDefValues(SmartString... v) {
        if (v != null)
            addDefValues(Arrays.asList(v));
        else
            addDefValues((List<SmartString>) null);
        return this;
    }

    /**
     * Adds new default {@link SmartString}s to the list. <br>
     * Note that the list cannot contain null {@link SmartString}s, thus <br>
     * if null is passed as parameter a new {@link SmartString} gets created with a null value. <br>
     * That means that {@link SmartString#asString()} will return null.
     */
    public YamlSection addDefValues(List<SmartString> v) {
        if (v != null) {
            for (SmartString value :
                    v) {
                Objects.requireNonNull(value);
            }
            defaultValues.addAll(v);
        } else
            defaultValues.add(new SmartString((String) null));
        return this;
    }

    public YamlSection addDefComments(String... c) {
        if (c != null)
            this.defaultComments.addAll(Arrays.asList(c));
        return this;
    }

    public YamlSection addDefSideComments(String... c) {
        if (c != null)
            this.defaultSideComments.addAll(Arrays.asList(c));
        return this;
    }


    // SET METHODS:

    public UtilsYamlSection getUtils() {
        return utils;
    }

    /**
     * Returns the first key located at index 0.
     */
    public String getFirstKey() {
        return getKeyAt(0);
    }


    public String getLastKey() {
        return getKeyAt(keys.size() - 1);
    }

    public SmartString getLastValue() {
        return getValueAt(values.size() - 1);
    }

    public SmartString getLastDefValue() {
        return getValueAt(defaultValues.size() - 1);
    }

    /**
     * Returns the key by given index or
     * null if there was no index i in the list.
     */
    public String getKeyAt(int i) {
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
    public YamlSection setKeys(String... keys) {
        if (keys != null) return setKeys(Arrays.asList(keys));
        return this;
    }

    /**
     * Clears the list and adds the given keys.
     * Duplicate keys are not allowed,
     * because its the only way of distinguishing modules.
     */
    public YamlSection setKeys(List<String> keys) {
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
    public SmartString getValue() {
        return getValueAt(0);
    }

    /**
     * Returns the value by given index or
     * its default value, if the value is null/empty and {@link Yaml#isReturnDefaultWhenValueIsNullEnabled()} is set to true.
     */
    public SmartString getValueAt(int i) {
        SmartString v = new SmartString((String) null);
        try {
            v = values.get(i);
        } catch (Exception ignored) {
        }

        if (v.asString() == null && yaml.isReturnDefaultWhenValueIsNullEnabled)
            return getDefValueAt(i);
        return v;
    }

    public List<SmartString> getValues() {
        return values;
    }

    /**
     * See {@link #setValues(List)} for details.
     */
    public YamlSection setValues(String... v) {
        setValues(utils.stringArrayToValuesList(v));
        return this;
    }

    /**
     * Not allowed to contain null {@link SmartString}s. <br>
     * See {@link #setValues(List)} for details.
     */
    public YamlSection setValues(SmartString... v) {
        setValues(Arrays.asList(v));
        return this;
    }

    /**
     * Clears the values list and adds the values from the provided list. <br>
     * Note that the list can NOT contain null {@link SmartString}s. <br>
     * {@link SmartString#asString()} may return null though. <br>
     * If you want to remove values, use {@link #removeAllValues()} instead.
     */
    public YamlSection setValues(List<SmartString> v) {
        this.values.clear();
        addValues(v);
        return this;
    }

    /**
     * Returns the first {@link SmartString} in the default values list.
     */
    public SmartString getDefValue() {
        return getDefValueAt(0);
    }

    /**
     * Returns the {@link SmartString} at index i in the default values list.
     */
    public SmartString getDefValueAt(int i) {
        SmartString v = new SmartString((String) null);
        try {
            v = defaultValues.get(i);
        } catch (Exception ignored) {
        }
        return v;
    }

    public List<SmartString> getDefValues() {
        return defaultValues;
    }

    /**
     * See {@link #setDefValues(List)} for details.
     */
    public YamlSection setDefValues(String... v) {
        setDefValues(utils.stringArrayToValuesList(v));
        return this;
    }

    /**
     * See {@link #setDefValues(List)} for details.
     */
    public YamlSection setDefValues(SmartString... v) {
        setDefValues(Arrays.asList(v));
        return this;
    }

    /**
     * The default values are written to the yaml file, when there were no regular values set/added. <br>
     * Further details: <br>
     * {@link Yaml#isWriteDefaultValuesWhenEmptyEnabled()} <br>
     * {@link Yaml#isReturnDefaultWhenValueIsNullEnabled()} <br>
     */
    public YamlSection setDefValues(List<SmartString> v) {
        this.defaultValues.clear();
        addDefValues(v);
        return this;
    }

    /**
     * Returns the first comment at index 0.
     */
    public String getComment() {
        return getCommentAt(0);
    }

    /**
     * Returns a specific comment by its index or null if nothing found at that index.
     */
    public String getCommentAt(int i) {
        try {
            return comments.get(i);
        } catch (Exception ignored) {
        }
        return null;
    }

    public List<String> getComments() {
        return comments;
    }

    public YamlSection setComments(String... c) {
        if (c != null) setComments(Arrays.asList(c));
        return this;
    }

    public YamlSection setComments(List<String> c) {
        if (c != null) {
            this.comments.clear();
            this.comments.addAll(c);
        }
        return this;
    }

    /**
     * Returns the first default comment at index 0.
     */
    public String getDefComment() {
        return getDefCommentAt(0);
    }

    /**
     * Returns a specific default comment by its index or null if nothing found at that index.
     */
    public String getDefCommentAt(int i) {
        try {
            return defaultComments.get(i);
        } catch (Exception ignored) {
        }
        return null;
    }

    public List<String> getDefComments() {
        return defaultComments;
    }

    public YamlSection setDefComments(String... c) {
        if (c != null) setDefComments(Arrays.asList(c));
        return this;
    }

    public YamlSection setDefComments(List<String> c) {
        if (c != null) {
            this.defaultComments.clear();
            this.defaultComments.addAll(c);
        }
        return this;
    }

    /**
     * Returns the first default side comment at index 0.
     */
    public String getSideComment() {
        return getSideCommentAt(0);
    }

    /**
     * Returns a specific default side comment by its index or null if nothing found at that index.
     */
    public String getSideCommentAt(int i) {
        try {
            return sideComments.get(i);
        } catch (Exception ignored) {
        }
        return null;
    }

    public List<String> getSideComments() {
        return sideComments;
    }

    public YamlSection setSideComments(String... c) {
        if (c != null) setSideComments(Arrays.asList(c));
        return this;
    }

    public YamlSection setSideComments(List<String> c) {
        if (c != null) {
            this.sideComments.clear();
            this.sideComments.addAll(c);
        }
        return this;
    }

    /**
     * Returns the first default side comment at index 0.
     */
    public String getDefSideComment() {
        return getDefSideCommentAt(0);
    }

    /**
     * Returns a specific default side comment by its index or null if nothing found at that index.
     */
    public String getDefSideCommentAt(int i) {
        try {
            return defaultSideComments.get(i);
        } catch (Exception ignored) {
        }
        return null;
    }

    public List<String> getDefSideComments() {
        return defaultSideComments;
    }

    public YamlSection setDefSideComments(String... c) {
        if (c != null) setDefSideComments(Arrays.asList(c));
        return this;
    }

    public YamlSection setDefSideComments(List<String> c) {
        if (c != null) {
            this.defaultSideComments.clear();
            this.defaultSideComments.addAll(c);
        }
        return this;
    }

    /**
     * See {@link #putJavaChildSection(Object, boolean)} for details.
     */
    public YamlSection putJavaChildSection(Object obj) throws NotLoadedException, IllegalKeyException, IllegalAccessException {
        return putJavaChildSection(obj, false);
    }

    /**
     * Takes the fields of the provided Java object
     * and converts/adds them to this {@link YamlSection}, as children. <br>
     * @param obj expected to be not primitive and not "big" primitive. <br>
     *            So it should not be of type int.class or Integer.class for example.
     */
    public YamlSection putJavaChildSection(Object obj, boolean includePrivateFields) throws NotLoadedException, IllegalKeyException, IllegalAccessException {
        Class<?> aClass = obj.getClass();
        for (Field field : aClass.getDeclaredFields()) {
            if(Modifier.isPrivate(field.getModifiers())) {
                if(includePrivateFields) field.setAccessible(true);
                else continue;
            }
            Object rawValue = field.get(obj);
            if (rawValue != null) {
                String value = "" + rawValue;
                List<String> keys = new ArrayList<>(this.keys);
                keys.add(field.getName());
                YamlSection section = yaml.put(keys);
                section.setValues(value);
            }
            // else the value is null and nothing is added to yaml (not even the key)
        }
        return this;
    }

    /**
     * See {@link #putDefJavaChildSection(Object, boolean)} for details.
     */
    public YamlSection putDefJavaChildSection(Object obj) throws NotLoadedException, IllegalKeyException, IllegalAccessException {
        return putDefJavaChildSection(obj, false);
    }

    /**
     * Takes the fields of the provided Java object
     * and converts/adds them to this {@link YamlSection}, as children. <br>
     * @param obj expected to be not primitive and not "big" primitive. <br>
     *            So it should not be of type int.class or Integer.class for example.
     */
    public YamlSection putDefJavaChildSection(Object obj, boolean includePrivateFields) throws NotLoadedException, IllegalKeyException, IllegalAccessException {
        Class<?> aClass = obj.getClass();
        for (Field field : aClass.getDeclaredFields()) {
            if(Modifier.isPrivate(field.getModifiers())) {
                if(includePrivateFields) field.setAccessible(true);
                else continue;
            }
            Object rawValue = field.get(obj);
            if (rawValue != null) {
                String value = "" + rawValue;
                List<String> keys = new ArrayList<>(this.keys);
                keys.add(field.getName());
                YamlSection section = yaml.put(keys);
                section.setDefValues(value);
            }
            // else the value is null and nothing is added to yaml (not even the key)
        }
        return this;
    }


    // AS METHODS:


    /**
     * See {@link #as(Class, boolean)} for details.
     */
    public <V> V as(Class<V> type) throws InstantiationException, IllegalAccessException, NotLoadedException, IllegalKeyException, InvocationTargetException {
        return as(type, false);
    }

    /**
     * Deserialises this YAML section
     * (its children) to a Java object of the provided type. <br>
     * Uses the Java reflection API. If the class has constructor parameters initialises them with null. <br>
     *
     * @param type the type to deserialize to
     * @param <V>  the type to get
     * @return the value if present and of the proper type, else null
     */
    @SuppressWarnings("unchecked") // type is verified by the class parameter
    public <V> V as(Class<V> type, boolean includePrivateFields) throws InstantiationException, IllegalAccessException, NotLoadedException, IllegalKeyException, InvocationTargetException {

        // Create an instance/object of the provided type, which then later gets returned:
        V instance;
        if (type.getDeclaredConstructors().length == 0) {
            instance = type.newInstance();
        } else {
            Constructor<?> constructor = type.getDeclaredConstructors()[0];
            Parameter[] params = constructor.getParameters();
            if (params.length > 0) {
                Object[] paramValues = new Object[params.length];
                for (int i = 0; i < constructor.getParameterCount(); i++) {
                    paramValues[i] = 0;
                    if(isPrimitive(constructor.getParameters()[i].getType()))
                        paramValues[i] = 0;
                    else
                        paramValues[i] = null;
                }
                instance = (V) constructor.newInstance(paramValues);
            } else {
                instance = (V) constructor.newInstance();
            }
        }

        // Fill object with data from this sections' children:
        for (Field field : type.getDeclaredFields()) {
            if(Modifier.isPrivate(field.getModifiers())) {
                if(includePrivateFields) field.setAccessible(true);
                else continue;
            }

            List<String> keys = new ArrayList<>(this.keys);
            keys.add(field.getName());
            YamlSection section = yaml.get(keys);

            if (section != null) {
                if (field.getType().equals(String.class) || field.getType().equals(Character.class)) field.set(instance, section.asString());
                else if (field.getType().equals(boolean.class) || field.getType().equals(Boolean.class))
                    field.set(instance, section.asBoolean());
                else if (field.getType().equals(byte.class) || field.getType().equals(Byte.class))
                    field.set(instance, section.asByte());
                else if (field.getType().equals(short.class) || field.getType().equals(Short.class))
                    field.set(instance, section.asShort());
                else if (field.getType().equals(int.class) || field.getType().equals(Integer.class))
                    field.set(instance, section.asInt());
                else if (field.getType().equals(long.class) || field.getType().equals(Long.class))
                    field.set(instance, section.asLong());
                else if (field.getType().equals(float.class) || field.getType().equals(Float.class))
                    field.set(instance, section.asFloat());
                else if (field.getType().equals(double.class) || field.getType().equals(Double.class))
                    field.set(instance, section.asDouble());
                //TODO else do nothing, nested objects are not supported yet, only primitives at the moment
            }
        }
        return (V) instance;
    }

    /**
     * Is primitive check that includes big primitives.
     */
    private boolean isPrimitive(Class<?> clazz){
        return clazz.isPrimitive() || clazz.equals(Boolean.class) ||
                clazz.equals(Byte.class) ||
                clazz.equals(Short.class) ||
                clazz.equals(Integer.class) ||
                clazz.equals(Long.class) ||
                clazz.equals(Float.class) ||
                clazz.equals(Double.class) ||
                clazz.equals(Character.class);
    }

    /**
     * Shortcut for retrieving this {@link YamlSection}s first {@link SmartString} as string. <br>
     * See {@link SmartString#asString()} for details. <br>
     */
    public String asString() {
        return asString(0);
    }

    public String asString(int i) {
        return getValueAt(i).asString();
    }

    /**
     * Shortcut for retrieving this {@link YamlSection}s first {@link SmartString}.
     */
    public SmartString asSmartString() {
        return asSmartString(0);
    }

    public SmartString asSmartString(int i) {
        return getValueAt(i);
    }

    /**
     * Note that this is a copy and not the original list.
     */
    public List<String> asStringList() {
        return utils.valuesListToStringList(this.values);
    }

    /**
     * Shortcut for retrieving this {@link YamlSection}s first {@link SmartString} as char-array.
     */
    public char[] asCharArray() {
        return asCharArray(0);
    }

    public char[] asCharArray(int i) {
        return getValueAt(i).asCharArray();
    }

    /**
     * Shortcut for retrieving this {@link YamlSection}s first {@link SmartString} as boolean.
     */
    public boolean asBoolean() {
        return asBoolean(0);
    }

    public boolean asBoolean(int i) {
        return getValueAt(i).asBoolean();
    }

    /**
     * Shortcut for retrieving this {@link YamlSection}s first {@link SmartString} as byte.
     */
    public byte asByte() {
        return asByte(0);
    }

    public byte asByte(int i) {
        return getValueAt(i).asByte();
    }

    /**
     * Shortcut for retrieving this {@link YamlSection}s first {@link SmartString} as short.
     */
    public short asShort() {
        return asShort(0);
    }

    public short asShort(int i) {
        return getValueAt(i).asShort();
    }

    /**
     * Shortcut for retrieving this {@link YamlSection}s first {@link SmartString} as int.
     */
    public int asInt() {
        return asInt(0);
    }

    public int asInt(int i) {
        return getValueAt(i).asInt();
    }

    /**
     * Shortcut for retrieving this {@link YamlSection}s first {@link SmartString} as long.
     */
    public long asLong() {
        return asLong(0);
    }

    public long asLong(int i) {
        return getValueAt(i).asLong();
    }

    /**
     * Shortcut for retrieving this {@link YamlSection}s first {@link SmartString} as float.
     */
    public float asFloat() {
        return asFloat(0);
    }

    public float asFloat(int i) {
        return getValueAt(i).asFloat();
    }

    /**
     * Shortcut for retrieving this {@link YamlSection}s first {@link SmartString} as double.
     */
    public Double asDouble() {
        return asDouble(0);
    }

    public Double asDouble(int i) {
        return getValueAt(i).asDouble();
    }


    // OTHER METHODS:


    /**
     * <p style="color:red;">Do not modify this directly, unless you know what you are doing!</p>
     * The parent {@link YamlSection} of this {@link YamlSection}, aka the last {@link YamlSection} in the generation before. <br>
     * More about generations here: {@link YamlReader#parseLine(Yaml, DYLine)}.
     */
    public YamlSection getParentSection() {
        return parentSection;
    }

    /**
     * <p style="color:red;">Do not modify this directly, unless you know what you are doing!</p>
     * The parent {@link YamlSection} of this {@link YamlSection}, aka the last {@link YamlSection} in the generation before. <br>
     * More about generations here: {@link YamlReader#parseLine(Yaml, DYLine)}.
     */
    public YamlSection setParentSection(YamlSection parentSection) {
        this.parentSection = parentSection;
        return this;
    }

    /**
     * <p style="color:red;">Do not modify this list directly, unless you know what you are doing!</p>
     * A list containing this modules child modules, aka the next generation. <br>
     * Note that this list does NOT contain generations beyond that. <br>
     * This methods ensures that these modules get added to the 'inEditModules' list, and thus <br>
     * modifying them has also affect to the actual yaml file. <br>
     * More about generations here: {@link YamlReader#parseLine(Yaml, DYLine)}.
     */
    public List<YamlSection> getChildSections() {
        return childSections;
    }

    /**
     * <p style="color:red;">Do not modify this list directly, unless you know what you are doing!</p>
     * A list containing this modules child modules, aka the next generation. <br>
     * Note that this list does NOT contain generations beyond that. <br>
     * More about generations here: {@link YamlReader#parseLine(Yaml, DYLine)}.
     */
    public YamlSection setChildSections(List<YamlSection> childSections) {
        this.childSections = childSections;
        return this;
    }

    public YamlSection addChildSections(YamlSection... cModules) {
        Objects.requireNonNull(cModules);
        childSections.addAll(Arrays.asList(cModules));
        return this;
    }


    /**
     * The count of line breaks before this {@link YamlSection}. <br>
     * Example yaml file:
     * <pre>
     *     m1: value
     *
     *
     *
     *     m2: value
     * </pre>
     * For 'm2' this method would return 3, because there are 3 line breaks before it. <br>
     * For 'm1' this method returns 0.
     */
    public int getCountTopLineBreaks() {
        return countTopLineBreaks;
    }

    /**
     * Set the count of line breaks before this {@link YamlSection}. <br>
     * Example yaml file before:
     * <pre>
     *     m1: value
     *     m2: value
     * </pre>
     * Example yaml file after m2's count of spaces was set to 3 via this method:
     * <pre>
     *     m1: value
     *
     *
     *
     *     m2: value
     * </pre>
     */
    public YamlSection setCountTopLineBreaks(int countTopLineBreaks) {
        this.countTopLineBreaks = countTopLineBreaks;
        return this;
    }
}
