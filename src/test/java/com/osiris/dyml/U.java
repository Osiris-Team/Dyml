package com.osiris.dyml;

import org.junit.jupiter.api.condition.OS;

public class U {
    public static String N = "\n";

    static {
        if (OS.WINDOWS.isCurrentOs())
            N = "\r\n";
    }
}
