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
import com.osiris.dyml.utils.UtilsFile;
import com.osiris.dyml.utils.UtilsTimeStopper;
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

}