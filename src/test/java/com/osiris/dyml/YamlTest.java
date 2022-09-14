package com.osiris.dyml;

import com.osiris.dyml.exceptions.*;
import com.osiris.dyml.watcher.DirWatcher;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.osiris.dyml.U.N;
import static org.junit.jupiter.api.Assertions.*;

class YamlTest {
    File file = new File(System.getProperty("user.dir") + "/src/test/tests.yml");

    @Test
    void codingStyle() throws IOException, DuplicateKeyException, YamlReaderException, IllegalListException, NotLoadedException, IllegalKeyException, YamlWriterException {

    }

    @Test
    void threadSafetyTest() throws IOException, DuplicateKeyException, YamlReaderException, IllegalListException, NotLoadedException, IllegalKeyException, YamlWriterException, InterruptedException {
        List<Thread> threads = new ArrayList<>();
        new Yaml(file).load().put("thread-safety-val").setValues("0").getYaml().save();
        for (int i = 0; i < 10; i++) { // Count to 1000 on 10 threads inside the same yaml file
            threads.add(new Thread(() -> {
                Yaml yaml = new Yaml(file);
                for (int f = 0; f < 100; f++) {
                    try {
                        yaml.lockFile();
                        yaml.load();
                        int val = yaml.put("thread-safety-val").asInt();
                        val++;
                        yaml.put("thread-safety-val").setValues(""+val);
                        yaml.save();
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        yaml.unlockFile();
                    }

                }
            }));
        }

        for (Thread t :
                threads) {
            t.start();
        }

        for (Thread t :
                threads) {
            t.join();
        }
        assertEquals(1000, new Yaml(file).load().get("thread-safety-val").asInt());
    }

    @Test
    void runLoadTwice() throws IOException, DuplicateKeyException, YamlReaderException, IllegalListException {
        Yaml yaml = new Yaml(System.getProperty("user.dir") + "/src/test/tests.yml", true, true);
        yaml.load();
        yaml.load();
    }

    @Test
    void remove() throws YamlWriterException, IOException, DuplicateKeyException, YamlReaderException, IllegalListException, NotLoadedException, IllegalKeyException {
        Yaml yaml = new Yaml(System.getProperty("user.dir") + "/src/test/tests.yml", true);
        yaml.load();
        yaml.reset();
        yaml.add("test");
        yaml.saveAndLoad();
        yaml.remove("test");
        yaml.saveAndLoad();
        assertNull(yaml.get("test"));
    }

    @Test
    void add() throws IOException, DuplicateKeyException, YamlReaderException, IllegalListException, YamlWriterException, NotLoadedException, IllegalKeyException {
        Yaml yaml = new Yaml(System.getProperty("user.dir") + "/src/test/tests.yml", true);
        yaml.load();
        yaml.reset();
        YamlSection m1 = yaml.add("test-put").setValues("value");
        YamlSection m2 = yaml.add("test-put", "c1").setValues("value");
        yaml.saveAndLoad();

        assertNotNull(m1);
        assertNotNull(m2);

        assertNotNull(m1.asString());
        assertNotNull(m2.asString());

        // Test putting/adding a new module to the file
        yaml.add("test-put-new").setValues("wow!").setComments("This is a comment!");
        yaml.saveAndLoad();
        yaml.printFile();

        // Test putting/adding a new module to the file (in a hierarchy)
        yaml.add("test-put", "c1", "c2").setComments("This is a comment!").setValues("value!");
        yaml.saveAndLoad();
        yaml.printFile();

        // Test putting/adding a new module to the file (in a hierarchy with multiple modules in the same generation)
        yaml.add("test-put", "c1-1").setComments("This is a comment!").setValues("value!");
        yaml.add("test-put", "c1-2").setComments("This is a comment!").setValues("value!");
        yaml.add("test-put", "c1-3").setComments("This is a comment!").setValues("value!");
        yaml.saveAndLoad();
        yaml.add("test-put", "c1-4").setComments("This is a comment!").setValues("value!");
        yaml.saveAndLoad();
    }

    @Test
    void put() throws IOException, DuplicateKeyException, YamlReaderException, IllegalListException, YamlWriterException, NotLoadedException, IllegalKeyException {
        Yaml yaml = new Yaml(System.getProperty("user.dir") + "/src/test/tests.yml");
        yaml.load();
        yaml.reset();
        YamlSection m1 = yaml.put("test-put").setValues("value");
        YamlSection m2 = yaml.put("test-put", "c1").setValues("value");
        yaml.saveAndLoad();

        assertNotNull(m1);
        assertNotNull(m2);

        assertNotNull(m1.asString());
        assertNotNull(m2.asString());

        // Test putting/adding a new module to the file
        yaml.put("test-put-new").setValues("wow!").setComments("This is a comment!");
        yaml.saveAndLoad();
        yaml.printFile();

        // Test putting/adding a new module to the file (in a hierarchy)
        yaml.put("test-put", "c1", "c2").setComments("This is a comment!").setValues("value!");
        yaml.saveAndLoad();
        yaml.printFile();

        // Test putting/adding a new module to the file (in a hierarchy with multiple modules in the same generation)
        yaml.put("test-put", "c1-1").setComments("This is a comment!").setValues("value!");
        yaml.put("test-put", "c1-2").setComments("This is a comment!").setValues("value!");
        yaml.put("test-put", "c1-3").setComments("This is a comment!").setValues("value!");
        yaml.saveAndLoad();
        yaml.put("test-put", "c1-4").setComments("This is a comment!").setValues("value!");
        yaml.saveAndLoad();
        yaml.printAll();
        yaml.printFile();
    }

    @Test
    void get() throws IOException, DuplicateKeyException, YamlReaderException, IllegalListException, YamlWriterException, NotLoadedException, IllegalKeyException {
        Yaml yaml = new Yaml(System.getProperty("user.dir") + "/src/test/tests.yml");
        yaml.load();
        yaml.put("test-get").setValues("value");
        yaml.put("test-get", "c1").setValues("value");
        yaml.saveAndLoad();

        YamlSection m1 = yaml.get("test-get");
        YamlSection m2 = yaml.get("test-get", "c1");

        assertNotNull(m1);
        assertNotNull(m2);

        assertNotNull(m1.asString());
        assertNotNull(m2.asString());
    }

    @Test
    void getAddedModuleByKeys() throws Exception {
        Yaml yaml = new Yaml(System.getProperty("user.dir") + "/src/test/tests.yml");
        yaml.load();
        yaml.put("hello").setDefValues("world");
        yaml.save(true);
        assertEquals("world", yaml.get("hello").asString());
    }

    @Test
    void getLoadedModuleByKeys() throws Exception {
        Yaml yaml = new Yaml(System.getProperty("user.dir") + "/src/test/tests.yml");
        yaml.load();
        yaml.put("hello").setDefValues("world");
        yaml.save(true);
        yaml.load(); // Reload the config so we got the loaded Module
        assertEquals("world", yaml.get("hello").asString());
    }

    @Test
    void reset() throws Exception {
        Yaml yaml = new Yaml(System.getProperty("user.dir") + "/src/test/reset-test.yml");
        yaml.load();
        yaml.put("key").setDefValues("value");
        yaml.save();
        yaml.reset();
        assertEquals(0, yaml.getFile().length());
    }

    @Test
    void save() throws Exception {
        Yaml yaml = new Yaml(System.getProperty("user.dir") + "/src/test/save-test.yml");
        yaml.load();
        yaml.reset();
        yaml.put("p1");
        yaml.put("p2");
        yaml.put("p3");
        yaml.save(true);
        yaml.load();
        assertEquals("p1", yaml.getAllLoaded().get(0).getFirstKey());
        assertEquals("p2", yaml.getAllLoaded().get(1).getFirstKey());
        assertEquals("p3", yaml.getAllLoaded().get(2).getFirstKey());
        // Test if save() orders the modules to the right parents
        yaml.getAllInEdit().clear();
        yaml.put("p1", "c1");
        yaml.put("p2", "c1");
        yaml.put("p3", "c1");
        yaml.save(true);
        yaml.load();
        yaml.printAll();
        assertEquals("c1", yaml.getAllLoaded().get(1).getKeyAt(1));
        assertEquals("c1", yaml.getAllLoaded().get(3).getKeyAt(1));
        assertEquals("c1", yaml.getAllLoaded().get(5).getKeyAt(1));
    }

    @Test
    void readValueWithLineBreaks() throws IOException, DuplicateKeyException, YamlReaderException, IllegalListException {
        Yaml yaml = new Yaml("key: Hello\nThere!", "");
        yaml.load();
        assertEquals("Hello\nThere!", yaml.get("key").asString());
        yaml.inString = "key: Hello\n" +
                "There\n" +
                "         there\n" +
                "key2: hello\n";
        yaml.load();
        assertEquals("Hello\nThere\n         there", yaml.get("key").asString());
        assertEquals("hello", yaml.get("key2").asString());
    }

    @Test
    void writeValueWithLineBreaks() throws IOException, DuplicateKeyException, YamlReaderException, IllegalListException, NotLoadedException, IllegalKeyException, YamlWriterException {
        Yaml yaml = new Yaml("", "");
        yaml.load();
        yaml.put("key").setValues("Hello\nThere!");
        yaml.put("key2").setDefValues("val");
        yaml.save();
        assertEquals("Hello\nThere!", yaml.get("key").asString());
        assertEquals("val", yaml.get("key2").asString());
    }

    @Test
    void removeQuotes() throws YamlReaderException, IOException, DuplicateKeyException, IllegalListException, YamlWriterException {
        Yaml yaml = new Yaml("key: \"hello there\"\n" +
                "key2: test\n", "");
        yaml.load();
        yaml.save();
        assertEquals("hello there", yaml.get("key").asString()); // Expect removed quotes
        yaml = new Yaml("key: \"hello there\" mate!\n" +
                "key2: test\n", "");
        yaml.load();
        yaml.save();
        assertEquals("\"hello there\" mate!", yaml.get("key").asString()); // Do not remove quotes
    }

    @Test
    void testDirWatcher() throws YamlReaderException, YamlWriterException, IOException, DuplicateKeyException, IllegalListException, InterruptedException {
        File testFile = new File(file.getParentFile()+"/dir-watcher-test.txt");
        testFile.delete();
        DirWatcher dirWatcher = DirWatcher.get(file.getParentFile(), false);
        AtomicBoolean hasCreateEvent = new AtomicBoolean(false);
        AtomicBoolean hasModifyEvent = new AtomicBoolean(false);
        AtomicBoolean hasDeleteEvent = new AtomicBoolean(false);
        dirWatcher.setListeners(event -> {
            if(event.isCreateEvent()) hasCreateEvent.set(true);
            if(event.isModifyEvent()) hasModifyEvent.set(true);
            if(event.isDeleteEvent()) hasDeleteEvent.set(true);
        });
        testFile.createNewFile(); // executes create event
        Files.write(testFile.toPath(), "Hello!\n".getBytes(StandardCharsets.UTF_8));// executes modify event
        testFile.delete(); // executes delete event

        Thread.sleep(1000);
        assertTrue(hasCreateEvent.get());
        assertTrue(hasModifyEvent.get());
        assertTrue(hasDeleteEvent.get());
    }

    @Test
    void testYamlFileWatcher() throws YamlReaderException, YamlWriterException, IOException, DuplicateKeyException, IllegalListException, InterruptedException {
        Yaml yaml = new Yaml(file);
        AtomicBoolean isChanged = new AtomicBoolean(false);
        yaml.addFileEventListener(event -> {
            isChanged.set(true);
        });
        yaml.load();
        yaml.save();
        Thread.sleep(1000);
        assertTrue(isChanged.get());
    }

    @Test
    void testNotSortedKeys() throws YamlReaderException, IOException, DuplicateKeyException, IllegalListException, NotLoadedException, IllegalKeyException, YamlWriterException {
        Yaml yaml = new Yaml("", "");
        yaml.load();
        yaml.removeAll();
        yaml.put("key1");
        yaml.put("key2");
        yaml.put("key1", "child");
        yaml.save();
        assertEquals(
                "key1: " +N+
                "  child: " +N+
                        "key2: "+N, yaml.outString);
    }

    @Test
    void multipleColonsInKey() throws YamlReaderException, IOException, DuplicateKeyException, IllegalListException {
        Yaml yaml = new Yaml("hello:there: my : friend:", "");
        yaml.load();
        assertNotNull(yaml.get("hello:there"));
    }

    @Test
    void testSerialization() throws YamlReaderException, IOException, DuplicateKeyException, IllegalListException, NotLoadedException, IllegalKeyException, InstantiationException, IllegalAccessException, InvocationTargetException, YamlWriterException {
        class Person extends Object{
            public int age;
            public String name;
        }
        Yaml yaml = new Yaml(
                "person: " +N+
                        "  age: 44" +N+
                        "  name: Peter"+N, "");
        yaml.load();
        Person p = yaml.get("person").as(Person.class);
        assertEquals(yaml.get("person", "age").asInt(), p.age);
        assertEquals(yaml.get("person", "name").asString(), p.name);

        Yaml yaml2 = new Yaml("", "");
        yaml2.load();
        yaml2.put("person").putJavaChildSection(p);
        yaml2.save();

        assertEquals(yaml.inString, yaml2.outString);
    }

    @Test
    void testQuotesHandling() throws YamlReaderException, IOException, DuplicateKeyException, IllegalListException, YamlWriterException {
        Yaml yaml = new Yaml("key: \"val\"", "");
        yaml.load();
        YamlSection key = yaml.get("key");
        assertTrue(key.getValue().isInsideQuotes);
        assertEquals("val", key.asString());
        assertEquals("\"val\"", key.getValue().asOutputString());
        key.getValue().isInsideQuotes = false;
        yaml.save();
        assertEquals("key: val"+N, yaml.outString);
        key.getValue().isInsideQuotes = true;
        yaml.save();
        assertEquals("key: \"val\""+N, yaml.outString);
    }
}