package com.osiris.dyml;

import com.osiris.dyml.exceptions.*;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DreamYamlTest {

    @Test
    void codingStyle() throws IOException, DuplicateKeyException, DYReaderException, IllegalListException, NotLoadedException, IllegalKeyException, DYWriterException {

    }

    @Test
    void threadSafetyTest() throws IOException, DuplicateKeyException, DYReaderException, IllegalListException, NotLoadedException, IllegalKeyException, DYWriterException, InterruptedException {

        List<Thread> threads = new ArrayList<>();
        for (int i = 0; i < 10; i++) { // Don't make this too big, since github actions will take very long and abort the build then
            threads.add(new Thread(() -> {
                for (int f = 0; f < 10; f++) {
                    DreamYaml yaml = new DreamYaml(System.getProperty("user.dir") + "/src/test/tests.yml");
                    yaml.lockFile();
                    try {
                        System.out.println(Thread.currentThread().getName() + " is waiting...");
                        yaml.load();
                        System.out.println(Thread.currentThread().getName() + " is WRITE");
                        yaml.put("m1").setDefValues("hello");
                        yaml.put("m2").setDefValues("hello");
                        yaml.save();
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        yaml.unlockFile();
                    }

                }
                try {

                } catch (Exception e) {
                    e.printStackTrace();
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
    }

    @Test
    void runLoadTwice() throws IOException, DuplicateKeyException, DYReaderException, IllegalListException {
        DreamYaml yaml = new DreamYaml(System.getProperty("user.dir") + "/src/test/tests.yml", true, true);
        yaml.load();
        yaml.load();
    }

    @Test
    void remove() throws DYWriterException, IOException, DuplicateKeyException, DYReaderException, IllegalListException, NotLoadedException, IllegalKeyException {
        DreamYaml yaml = new DreamYaml(System.getProperty("user.dir") + "/src/test/tests.yml", true);
        yaml.load();
        yaml.reset();
        yaml.add("test");
        yaml.saveAndLoad();
        yaml.remove("test");
        yaml.saveAndLoad();
        assertNull(yaml.get("test"));
    }

    @Test
    void add() throws IOException, DuplicateKeyException, DYReaderException, IllegalListException, DYWriterException, NotLoadedException, IllegalKeyException {
        DreamYaml yaml = new DreamYaml(System.getProperty("user.dir") + "/src/test/tests.yml", true);
        yaml.load();
        yaml.reset();
        DYModule m1 = yaml.add("test-put").setValues("value");
        DYModule m2 = yaml.add("test-put", "c1").setValues("value");
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
    void put() throws IOException, DuplicateKeyException, DYReaderException, IllegalListException, DYWriterException, NotLoadedException, IllegalKeyException {
        DreamYaml yaml = new DreamYaml(System.getProperty("user.dir") + "/src/test/tests.yml");
        yaml.load();
        yaml.reset();
        DYModule m1 = yaml.put("test-put").setValues("value");
        DYModule m2 = yaml.put("test-put", "c1").setValues("value");
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
    void get() throws IOException, DuplicateKeyException, DYReaderException, IllegalListException, DYWriterException, NotLoadedException, IllegalKeyException {
        DreamYaml yaml = new DreamYaml(System.getProperty("user.dir") + "/src/test/tests.yml");
        yaml.load();
        yaml.put("test-get").setValues("value");
        yaml.put("test-get", "c1").setValues("value");
        yaml.saveAndLoad();

        DYModule m1 = yaml.get("test-get");
        DYModule m2 = yaml.get("test-get", "c1");

        assertNotNull(m1);
        assertNotNull(m2);

        assertNotNull(m1.asString());
        assertNotNull(m2.asString());
    }

    @Test
    void getAddedModuleByKeys() throws Exception {
        DreamYaml yaml = new DreamYaml(System.getProperty("user.dir") + "/src/test/tests.yml");
        yaml.load();
        yaml.put("hello").setDefValues("world");
        yaml.save(true);
        assertEquals("world", yaml.get("hello").asString());
    }

    @Test
    void getLoadedModuleByKeys() throws Exception {
        DreamYaml yaml = new DreamYaml(System.getProperty("user.dir") + "/src/test/tests.yml");
        yaml.load();
        yaml.put("hello").setDefValues("world");
        yaml.save(true);
        yaml.load(); // Reload the config so we got the loaded Module
        assertEquals("world", yaml.get("hello").asString());
    }

    @Test
    void reset() throws Exception {
        DreamYaml yaml = new DreamYaml(System.getProperty("user.dir") + "/src/test/reset-test.yml");
        yaml.load();
        yaml.put("key").setDefValues("value");
        yaml.save();
        yaml.reset();
        assertEquals(0, yaml.getFile().length());
    }

    @Test
    void save() throws Exception {
        DreamYaml yaml = new DreamYaml(System.getProperty("user.dir") + "/src/test/save-test.yml");
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
        assertEquals("c1", yaml.getAllLoaded().get(1).getKeyByIndex(1));
        assertEquals("c1", yaml.getAllLoaded().get(3).getKeyByIndex(1));
        assertEquals("c1", yaml.getAllLoaded().get(5).getKeyByIndex(1));
    }

    @Test
    void readValueWithLineBreaks() throws IOException, DuplicateKeyException, DYReaderException, IllegalListException {
        DreamYaml yaml = new DreamYaml("key: Hello\nThere!", "");
        yaml.load();
        assertEquals("Hello\nThere!", yaml.get("key").asString());
        yaml.inString = "key: Hello\n" +
                "There\n" +
                "         there\n" +
                "key2: hello\n";
        assertEquals("Hello\nThere\nthere", yaml.get("key").asString());
        assertEquals("hello", yaml.get("key2").asString());
    }

    @Test
    void writeValueWithLineBreaks() throws IOException, DuplicateKeyException, DYReaderException, IllegalListException, NotLoadedException, IllegalKeyException, DYWriterException {
        DreamYaml yaml = new DreamYaml("", "");
        yaml.load();
        yaml.put("key").setValues("Hello\nThere!");
        yaml.save();
        assertEquals("key: Hello\nThere!", yaml.outString);
    }

    @Test
    void readValueAsModule() {

    }

    @Test
    void writeValueAsModule() throws IOException, DuplicateKeyException, DYReaderException, IllegalListException {
    }


}