/*
 *  Copyright Osiris Team
 *  All rights reserved.
 *
 *  This software is licensed work.
 *  Please consult the file "LICENSE" for details.
 */

package com.osiris.dyml.utils;

import org.junit.jupiter.api.Test;

public class CharTest {

    @Test
    void listChars() {
        System.out.println(" ");
        System.out.println("Chars and points: ");
        String chars = "abcdefghijklmnopqrstuvxyz1234567890!\"²§³$%&/{()[]=}?ß`´+*~#',;.:-_<>|^°";
        for (int i = 0; i < chars.length(); i++) {
            System.out.println(chars.charAt(i)+" : "+chars.codePointAt(i));
        }
    }

    @Test
    void searchSpeedTest() throws Exception {
        UtilsTimeStopper timer1 = new UtilsTimeStopper();
        UtilsTimeStopper timer2 = new UtilsTimeStopper();
        // We search for the number 5.
        // The first string has that number at the end.
        // The second string has that number in the middle.
        String s1 = "mmxyc,mcyxnilflifdsjjnvioiio901908021h809u3ub9c9bu8923h7biausbiu8905";
        String s2 = "mmxyc,mcyxnilflifdsjjnvioiio9019085021h809u3ub9c9bu8923h7biausbiu890";

        for (int i = 0; i < 10; i++) {
            // Method 1
            timer1.start();
            s1.contains("5");
            timer1.stop();

            // Method 2
            timer2.start();
            for (int j = 0; j < s1.length(); j++) {
                if (s1.codePointAt(j)==53) // 53 equals 5
                    break;
            }
            timer2.stop();
            System.out.println("Timer1: "+timer1.getMillis()+"ms  Timer2: "+timer2.getMillis()+"ms");
        }


    }
}
