/*
 *  Copyright Osiris Team
 *  All rights reserved.
 *
 *  This software is licensed work.
 *  Please consult the file "LICENSE" for details.
 */

package com.osiris.dyml.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class UtilsFile {

    public void printFile(File file) {
        if (file == null) {
            System.err.println("File is null! Nothing to print!");
            return;
        }

        if (!file.exists()) {
            System.err.println("File doesn't exist! Nothing to print!");
            return;
        }
        System.out.println(" ");
        System.out.println("Printing '" + file.getName() + "' from path '" + file.getAbsolutePath() + "'");
        System.out.println("CONTENT START =================================");
        try {
            UtilsTimeStopper time = new UtilsTimeStopper();
            time.start();
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;
            while (true) {
                line = reader.readLine();
                if (line != null) {
                    System.out.println(line);
                } else
                    break;
            }
            time.stop();
            System.out.println("CONTENT END =================================");
            System.out.println("Read time in " + time.getMillis() + "ms");
            System.out.println(" ");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
