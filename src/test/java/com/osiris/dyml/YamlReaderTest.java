/*
 *  Copyright Osiris Team
 *  All rights reserved.
 *
 *  This software is licensed work.
 *  Please consult the file "LICENSE" for details.
 */

package com.osiris.dyml;

import com.osiris.dyml.exceptions.DuplicateKeyException;
import com.osiris.dyml.exceptions.IllegalListException;
import com.osiris.dyml.exceptions.YamlReaderException;
import com.osiris.dyml.exceptions.YamlWriterException;
import com.osiris.dyml.utils.UtilsFile;
import com.osiris.dyml.utils.UtilsTimeStopper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

class YamlReaderTest {

    @Test
    void testFileReading() throws IOException, YamlReaderException, IllegalListException, DuplicateKeyException {
        File file = new File(System.getProperty("user.dir") + "/src/test/features.yml");
        Yaml yaml = new Yaml(file);
        yaml.load();
        System.out.println("Parsing '" + file.getName() + "' from path '" + file.getAbsolutePath() + "'");
        try {
            UtilsTimeStopper time = new UtilsTimeStopper();
            time.start();
            yaml.load();
            time.stop();
            System.out.println("Finished parsing in " + time.getMillis() + "ms");
            yaml.printAll();
            new UtilsFile().printFile(file);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Test
    void testHyphenInValue() throws YamlReaderException, IOException, DuplicateKeyException, IllegalListException, YamlWriterException {
        Yaml yaml1 = new Yaml("key: -value", "");
        yaml1.load();
        Assertions.assertEquals("-value", yaml1.get("key").asString());
        yaml1.save();
        System.out.println(yaml1.outString);
        Assertions.assertTrue(yaml1.outString.contains("key: -value")); // assertTrue bc of line sperators


        Yaml yaml2 = new Yaml("key: - value", "");
        yaml2.load();
        Assertions.assertEquals("- value", yaml2.get("key").asString());
        yaml2.save();
        System.out.println(yaml2.outString);
        Assertions.assertTrue(yaml2.outString.contains("key: - value")); // assertTrue bc of line sperators
    }

    @Test
    void testDoubleHashtagInValue() throws YamlReaderException, IOException, DuplicateKeyException, IllegalListException, YamlWriterException {
        Yaml yaml1 = new Yaml("key: #value # comment", "");
        yaml1.load();
        Assertions.assertEquals("#value", yaml1.get("key").asString());
        Assertions.assertEquals("comment", yaml1.get("key").getSideComment());
        yaml1.save();
        System.out.println(yaml1.outString);
        Assertions.assertTrue(yaml1.outString.contains("key: #value # comment")); // assertTrue bc of line sperators
    }

    @Test
    void testSideComments() throws YamlReaderException, IOException, DuplicateKeyException, IllegalListException, YamlWriterException {
        Yaml yaml1 = new Yaml(
                "key: \n" +
                "  - #value # comment\n" +
                "  - value # comment2\n", "");
        yaml1.load();
        Assertions.assertEquals("#value", yaml1.get("key").getValue().asString());
        Assertions.assertEquals("comment", yaml1.get("key").getSideComment());
        yaml1.save();
        System.out.println(yaml1.outString);
        Assertions.assertTrue(yaml1.outString.contains("key: #value # comment")); // assertTrue bc of line sperators
    }
}