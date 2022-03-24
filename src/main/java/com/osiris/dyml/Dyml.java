package com.osiris.dyml;

import com.osiris.dyml.exceptions.IllegalListException;
import com.osiris.dyml.exceptions.YamlReaderException;
import com.osiris.dyml.exceptions.YamlWriterException;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Can be parent or child of another {@link Dyml} object. <br>
 * Represents a single section like this:
 * <pre>
 *      Comment
 *     key value
 * </pre>
 * Example .dyml content:
 * <pre>
 *     g0a val
 *       g1a val
 *       g1b val
 *     gb0 val
 * </pre>
 * If we parse the above with {@link #Dyml(String)}, <br>
 * we end up with 5 {@link Dyml} objects, even though we have 4 sections. <br>
 * That's because we need a root {@link Dyml} object, aka root parent, <br>
 * to work with the other sections. <br>
 */
public class Dyml {
    public File file;
    /**
     * Null string if this is the root section.
     */
    public String key;
    @NotNull
    public SmartString value;
    @NotNull
    public List<String> comments;
    /**
     * Null if this is the root section.
     */
    public Dyml parent;
    /**
     * <p style="color:red">Do not modify this list directly. Use the methods provided by {@link Dyml} instead.</p>
     * Contains this sections', child sections. <br>
     * Aka all the child sections got this section as {@link #parent}. <br>
     * If you still really want to traverse this list use {@link #getChildren()}. <br>
     */
    @NotNull
    public List<Dyml> children = new ArrayList<>();

    /**
     * <p style="color:red">Note that this creates a parent {@link Dyml} object, and should not be used by you.</p>
     * To parse .dyml content use these constructors instead: <br>
     * - {@link #Dyml(File)} <br>
     * - {@link #Dyml(String)} <br>
     * - {@link #Dyml(InputStream)} <br>
     */
    public Dyml() {
        this.key = "";
        this.value = new SmartString();
        this.comments = new ArrayList<>();
    }

    public Dyml(String key, @NotNull SmartString value, @NotNull List<String> comments) {
        this.key = key;
        this.value = value;
        this.comments = comments;
    }

    /**
     * Reads the dyml content from the provided InputStream and returns a new {@link Dyml} object representing it.
     */
    public Dyml(InputStream inputStream) throws IOException, YamlReaderException, IllegalListException {
        new DymlReader().parse(this, null, inputStream, null);
    }

    /**
     * Reads the dyml content from the provided String and returns a new {@link Dyml} object representing it.
     */
    public Dyml(String string) throws IOException, YamlReaderException, IllegalListException {
        new DymlReader().parse(this, null, null, string);
    }

    /**
     * Reads the dyml content from the provided file and returns a new {@link Dyml} object representing it.
     */
    public Dyml(File file) throws IOException, YamlReaderException, IllegalListException {
        new DymlReader().parse(this, (this.file = file), null, null);
    }

    /**
     * Reads the dyml content from the provided file and returns a new {@link Dyml} object representing it.
     */
    public Dyml(Path filePath) throws IOException, YamlReaderException, IllegalListException {
        new DymlReader().parse(this, (this.file = filePath.toFile()), null, null);
    }

    /**
     * Reads the dyml content from the provided InputStream and loads it into the current {@link Dyml} object.
     */
    public Dyml load(InputStream inputStream) throws IOException, YamlReaderException, IllegalListException {
        new DymlReader().parse(this, null, inputStream, null);
        return this;
    }

    /**
     * Reads the dyml content from the provided String and loads it into the current {@link Dyml} object.
     */
    public Dyml load(String string) throws IOException, YamlReaderException, IllegalListException {
        new DymlReader().parse(this, null, null, string);
        return this;
    }

    /**
     * Reads the dyml content from the provided file and loads it into the current {@link Dyml} object.
     */
    public Dyml load(File file) throws IOException, YamlReaderException, IllegalListException {
        new DymlReader().parse(this, (this.file = file), null, null);
        return this;
    }

    /**
     * Reads the dyml content from the provided file and loads it into the current {@link Dyml} object.
     */
    public Dyml load(Path filePath) throws IOException, YamlReaderException, IllegalListException {
        new DymlReader().parse(this, (this.file = filePath.toFile()), null, null);
        return this;
    }


    /**
     * Parses the {@link #children} list and writes it to the provided output.
     */
    public OutputStream saveToOutput(OutputStream out) throws YamlWriterException, IOException {
        new DymlWriter().parse(this.children, null, out, null, false);
        return out;
    }

    /**
     * Parses the {@link #children} list and writes it to a {@link String}, which gets returned.
     */
    public String saveToText() throws YamlWriterException, IOException {
        return new DymlWriter().parse(this.children, null, null, "", false);
    }

    /**
     * Parses the {@link #children} list and writes it to the provided output. <br>
     * Note that this only works when {@link #file} is not null. <br>
     */
    public File saveToFile() throws YamlWriterException, IOException {
        new DymlWriter().parse(this.children, file, null, null, false);
        return file;
    }

    /**
     * Parses the {@link #children} list and writes it to the provided output.
     */
    public File saveToFile(File file) throws YamlWriterException, IOException {
        new DymlWriter().parse(this.children, file, null, null, false);
        return file;
    }

    /**
     * Parses the {@link #children} list and writes it to the provided output.
     */
    public File saveToFile(String filePath) throws YamlWriterException, IOException {
        File file = new File(filePath);
        new DymlWriter().parse(this.children, file, null, null, false);
        return file;
    }

    /**
     * Parses the {@link #children} list and writes it to the provided output.
     */
    public File saveToFile(Path filePath) throws YamlWriterException, IOException {
        File file = filePath.toFile();
        new DymlWriter().parse(this.children, file, null, null, false);
        return file;
    }


    /**
     * Returns the child {@link Dyml} with the provided key(s), or null if not found.
     */
    public Dyml get(String... keys) {
        Dyml foundSection = null;
        synchronized (children) {
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
        }
        return foundSection;
    }

    /**
     * Returns an unmodifiable copy of {@link #children}. <br>
     * See {@link Collections#unmodifiableList(List)}. <br>
     */
    public List<Dyml> getChildren() {
        synchronized (children) {
            return Collections.unmodifiableList(children);
        }
    }

    /**
     * Returns the child {@link Dyml} at the provided index.
     */
    public Dyml get(int index) {
        synchronized (children) {
            return children.get(index);
        }
    }

    /**
     * Returns the child {@link Dyml}  with the provided key(s). If not existing creates one.
     */
    public Dyml put(String... keys) {
        Dyml lastParent = this;
        Dyml foundSection = null;
        for (String key : keys) {
            foundSection = get(key);
            if (foundSection == null) foundSection = lastParent.add(key);
            lastParent = foundSection;
        }
        return foundSection;
    }

    /**
     * Adds a new {@link Dyml} with the provided key, to the provided index in {@link #children}. <br>
     * Behaves like {@link List#add(int, Object)}. <br>
     */
    public Dyml add(int index, String key) {
        Dyml child = new Dyml(key, new SmartString(null), new ArrayList<>());
        synchronized (children) {
            children.add(index, child);
            child.parent = this;
        }
        return child;
    }

    /**
     * Adds a new {@link Dyml} with the provided key, to the end of {@link #children}. <br>
     * Behaves like {@link List#add(Object)}. <br>
     */
    public Dyml add(String key) {
        return add(new Dyml(key, new SmartString(null), new ArrayList<>()));
    }

    /**
     * Adds a new {@link Dyml} with the provided key, to the end of {@link #children}. <br>
     * Behaves like {@link List#add(Object)}. Also sets {@link #parent} of child to the current section.<br>
     */
    public Dyml add(Dyml child) {
        synchronized (children) {
            children.add(child);
            child.parent = this;
        }
        return child;
    }

    /**
     * Searches the child section by the provided keys.
     * If null does nothing.
     */
    public Dyml remove(String... keys) {
        Dyml child = get(keys);
        if (child != null) remove(child);
        return this;
    }

    /**
     * Removes the provided child from {@link #children}. <br>
     * Also sets the {@link #parent} of the child to null. <br>
     */
    public Dyml remove(Dyml child) {
        synchronized (children) {
            children.remove(child);
            child.parent = null;
        }
        return this;
    }

    /**
     * Adds a new comment to the end of {@link #comments}. <br>
     * Each comment in that list represents one comment line in the file. <br>
     * Behaves like {@link List#add(Object)}. <br>
     */
    public void addComments(String... comment) {
        comments.addAll(Arrays.asList(comment));
    }

    /**
     * See {@link #debugPrint(PrintStream, List)}.
     */
    public void debugPrint(PrintStream out) {
        debugPrint(out, children);
    }

    /**
     * Prints the provided sections (and their children) to the provided output with the most info possible.
     */
    public void debugPrint(PrintStream out, List<Dyml> sections) {
        if (sections.size() == 0) System.err.println("Sections list is empty!");
        for (int i = 0; i < sections.size(); i++) {
            Dyml section = sections.get(i);
            String spaces = "";
            for (int j = 0; j < section.countParents() - 1; j++) { // -1 bc of the root section
                spaces += "  ";
            }
            out.println(spaces + "I:" + i + " KEY:'" + section.key + "' VAL:'" + section.value.asString() +
                    "' COM:'" + section.comments + "' C-COUNT:'" + section.children.size() + "'");
            if (!section.children.isEmpty()) {
                debugPrint(out, section.children);
            }
        }
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
    public List<Dyml> getAllParents() {
        List<Dyml> parents = new ArrayList<>();
        Dyml parent = this.parent;
        while (parent != null) {
            parents.add(parent);
            parent = parent.parent;
        }
        return parents;
    }

    public int countParents() {
        int count = 0;
        Dyml parent = this.parent;
        while (parent != null) {
            count++;
            parent = parent.parent;
        }
        return count;
    }

    public Dyml firstChild() {
        return children.get(0);
    }

    public Dyml lastChild() {
        return children.get(children.size() - 1);
    }

    /**
     * Shortcut for returning the {@link #value}.<br>
     * Note that this can be null but never empty. <br>
     */
    public String asString() {
        return value.asString();
    }

    /**
     * Shortcut for returning the {@link #value}.<br>
     * Note that this can be null. <br>
     */
    public char[] asCharArray() {
        return value.asCharArray();
    }

    /**
     * Shortcut for returning the {@link #value}.<br>
     * Note that this can be null. <br>
     */
    public Boolean asBoolean() {
        return value.asBoolean();
    }

    /**
     * Shortcut for returning the {@link #value}.<br>
     * Note that this can be null. <br>
     */
    public Byte asByte() {
        return value.asByte();
    }

    /**
     * Shortcut for returning the {@link #value}.<br>
     * Note that this can be null. <br>
     */
    public Short asShort() {
        return value.asShort();
    }

    /**
     * Shortcut for returning the {@link #value}.<br>
     * Note that this can be null. <br>
     */
    public Integer asInt() {
        return value.asInt();
    }

    /**
     * Shortcut for returning the {@link #value}.<br>
     * Note that this can be null. <br>
     */
    public Long asLong() {
        return value.asLong();
    }

    /**
     * Shortcut for returning the {@link #value}.<br>
     * Note that this can be null. <br>
     */
    public Float asFloat() {
        return value.asFloat();
    }

    /**
     * Shortcut for returning the {@link #value}.<br>
     * Note that this can be null. <br>
     */
    public Double asDouble() {
        return value.asDouble();
    }

    /**
     * Shortcut for returning the {@link #value}.<br>
     * Note that this can be null. <br>
     */
    public String[] asArraySplitBySpaces() {
        return value.asArraySplitBySpaces();
    }

    /**
     * Shortcut for returning the {@link #value}.<br>
     * Note that this can be null. <br>
     */
    public List<String> asListSplitBySpaces() {
        return value.asListSplitBySpaces();
    }

    /**
     * Shortcut for returning the {@link #value}.<br>
     * Note that this can be null. <br>
     */
    public String[] asArraySplitByColons() {
        return value.asArraySplitByColons();
    }

    /**
     * Shortcut for returning the {@link #value}.<br>
     * Note that this can be null. <br>
     */
    public List<String> asListSplitByColons() {
        return value.asListSplitByColons();
    }
}
