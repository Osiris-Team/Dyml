/*
 *  Copyright Osiris Team
 *  All rights reserved.
 *
 *  This software is licensed work.
 *  Please consult the file "LICENSE" for details.
 */

package com.osiris.dyml;

import com.osiris.dyml.utils.TimeStopper;
import com.osiris.dyml.utils.UtilsFile;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileOutputStream;

import static org.junit.jupiter.api.Assertions.*;

class DYReaderTest {

    @Test
    void testFileReading() {
        File file = new File(System.getProperty("user.dir")+"/src/test/features.yml");
        DreamYaml yaml = new DreamYaml(file);
        System.out.println("Parsing '"+file.getName()+"' from path '"+file.getAbsolutePath()+"'");
        try{
            TimeStopper time = new TimeStopper();
            time.start();
            yaml.load();
            time.stop();
            System.out.println("Finished parsing in "+time.getMillis()+"ms");
            yaml.printAll();
            new UtilsFile().printFile(file);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}