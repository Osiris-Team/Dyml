package com.osiris.dyml;

import com.osiris.dyml.exceptions.IllegalListException;
import com.osiris.dyml.exceptions.YamlReaderException;
import com.osiris.dyml.exceptions.YamlWriterException;

import java.io.*;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class Dyml {
    public String key;
    public SmartString value;
    public List<String> comments;
    public Dyml parent;
    public List<Dyml> children;

    /**
     * <p style="color:red">Use the static method {@link #from(String)} instead to create {@link Dyml} objects.</p>
     */
    public Dyml(String key, SmartString value, List<String> comments) {
        this.key = key;
        this.value = value;
        this.comments = comments;
        this.children = new ArrayList<>();
    }

    /**
     * Reads the dyml content from the provided InputStream and returns a new {@link Dyml} object representing it.
     */
    public static Dyml from(InputStream inputStream) throws IOException, YamlReaderException, IllegalListException {
        Dyml dyml = new Dyml(null, null, null);
        dyml.children = new DymlReader().parse(null, inputStream, null);
        return dyml;
    }

    /**
     * Reads the dyml content from the provided String and returns a new {@link Dyml} object representing it.
     */
    public static Dyml from(String string) throws IOException, YamlReaderException, IllegalListException {
        Dyml dyml = new Dyml(null, null, null);
        dyml.children = new DymlReader().parse(null, null, string);
        return dyml;
    }

    /**
     * Reads the dyml content from the provided file and returns a new {@link Dyml} object representing it.
     */
    public static Dyml fromFile(File file) throws IOException, YamlReaderException, IllegalListException {
        Dyml dyml = new Dyml(null, null, null);
        dyml.children = new DymlReader().parse(file, null, null);
        return dyml;
    }
    /**
     * Reads the dyml content from the provided file and returns a new {@link Dyml} object representing it.
     */
    public static Dyml fromFile(String filePath) throws IOException, YamlReaderException, IllegalListException {
        Dyml dyml = new Dyml(null, null, null);
        dyml.children = new DymlReader().parse(new File(filePath), null, null);
        return dyml;
    }
    /**
     * Reads the dyml content from the provided file and returns a new {@link Dyml} object representing it.
     */
    public static Dyml fromFile(Path filePath) throws IOException, YamlReaderException, IllegalListException {
        Dyml dyml = new Dyml(null, null, null);
        dyml.children = new DymlReader().parse(filePath.toFile(), null, null);
        return dyml;
    }


    /**
     * Parses this {@link Dyml} object and writes it to the provided output.
     */
    public OutputStream toOutput(OutputStream out) throws YamlWriterException, IOException {
        new DymlWriter().parse(this, null, out, null, false);
        return out;
    }

    /**
     * Parses this {@link Dyml} object and writes it to a {@link String}, which gets returned.
     */
    public String toText() throws YamlWriterException, IOException {
        return new DymlWriter().parse(this, null, null, "", false);
    }

    /**
     * Parses this {@link Dyml} object and writes it to the provided output.
     */
    public File toFile(File file) throws YamlWriterException, IOException {
        new DymlWriter().parse(this, file, null, null, false);
        return file;
    }
    /**
     * Parses this {@link Dyml} object and writes it to the provided output.
     */
    public File toFile(String filePath) throws YamlWriterException, IOException {
        File file = new File(filePath);
        new DymlWriter().parse(this, file, null, null, false);
        return file;
    }
    /**
     * Parses this {@link Dyml} object and writes it to the provided output.
     */
    public File toFile(Path filePath) throws YamlWriterException, IOException {
        File file = filePath.toFile();
        new DymlWriter().parse(this, file, null, null, false);
        return file;
    }


    /**
     * Returns the {@link Dyml} with the provided key(s), or null if not found.
     */
    public Dyml get(String... keys) {
        Dyml foundSection = null;
        List<Dyml> listToSearch = children;
        for (int i = 0; i < keys.length; i++) {
            String key = keys[i];
            for (Dyml section :
                    listToSearch) {
                if (section.key.equals(key)) {
                    if (i == keys.length - 1)
                        foundSection = section;
                    else
                        listToSearch = section.children;

                }
            }
        }
        return foundSection;
    }

    /**
     * Returns the child {@link Dyml} at the provided index.
     */
    public Dyml getAt(int index) {
        return children.get(index);
    }

    public void printSections(PrintStream out) {
        printSections(out, children);
    }

    public void printSections(PrintStream out, List<Dyml> sections) {
        if (sections.size() == 0) System.err.println("List is empty!");
        for (int i = 0; i < sections.size(); i++) {
            Dyml section = sections.get(i);
            String spaces = "";
            for (int j = 0; j < section.countSpaces(); j++) {
                spaces += " ";
            }
            out.println(spaces+"I:"+i + " KEY:'" + section.key + "' VAL:'" + section.value.asString() + "' COM:'" + section.comments.toString() + "'");
            if (!section.children.isEmpty()){
                printSections(out, section.children);
            }
        }
    }

    /**
     * Returns the child with the provided key. If not existing creates one.
     */
    public Dyml put(String... keys) {

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
    public List<Dyml> getAllParents(){
        List<Dyml> parents = new ArrayList<>();
        Dyml parent = this.parent;
        while(parent!=null){
            parents.add(parent);
            parent = parent.parent;
        }
        return parents;
    }

    public int countParents() {
        int count = 0;
        Dyml parent = this.parent;
        while(parent!=null){
            count++;
            parent = parent.parent;
        }
        return count;
    }

    /**
     * Determines the amount of spaces before this sections' key via its parents. <br>
     * Example: <br>
     * <pre>
     *     g0 val
     *       g1 val
     * </pre>
     * Returns 0 for g0 and 2 for g1. <br>
     */
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
