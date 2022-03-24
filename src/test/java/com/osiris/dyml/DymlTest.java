package com.osiris.dyml;

import com.osiris.dyml.exceptions.IllegalListException;
import com.osiris.dyml.exceptions.YamlReaderException;
import com.osiris.dyml.exceptions.YamlWriterException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static com.osiris.dyml.U.N;

class DymlTest {

    @Test
    void testCodeStyle() throws IOException, YamlReaderException, IllegalListException, YamlWriterException {
        Dyml dyml = new Dyml("" +
                "key val\n" +
                "  c1 val\n" +
                "  c2 val\n" +
                "    g1 val\n");
        dyml.debugPrint(System.out);
        System.out.println(dyml.saveToText());
    }

    @Test
    void put() throws YamlReaderException, IOException, IllegalListException, YamlWriterException {
        Dyml dyml = new Dyml("key value");
        dyml.put("key").value.set(10);
        Assertions.assertEquals("key 10" + N, dyml.saveToText());
        dyml.put("key2").value.set(6999);
        dyml.debugPrint(System.out);
        Assertions.assertEquals("key 10" + N +
                "key2 6999" + N, dyml.saveToText());
        dyml.put("key2", "c1").value.set("12");
        dyml.debugPrint(System.out);
        Assertions.assertEquals(
                "key 10" + N +
                        "key2 6999" + N +
                        "  c1 12" + N, dyml.saveToText());
    }

    @Test
    void put2() throws YamlWriterException, IOException {
        Dyml dyml = new Dyml();
        dyml.put("key", "c1").value.set(10);
        Assertions.assertEquals("key " + N +
                "  c1 10" + N, dyml.saveToText());
    }

    @Test
    void put3() throws YamlWriterException, IOException, YamlReaderException, IllegalListException {
        Dyml dyml = new Dyml("key \n" +
                "  c1 val\n" +
                "  c2 val\n");
        dyml.debugPrint(System.out, dyml.children);
        //dyml.put("key");
        System.out.println("\n\n");
        dyml.debugPrint(System.out, dyml.children);
        Assertions.assertEquals("key " + N +
                "  c1 val" + N +
                "  c2 val" + N, dyml.saveToText());
    }
}