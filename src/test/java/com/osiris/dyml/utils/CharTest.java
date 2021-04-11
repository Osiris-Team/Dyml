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
}
