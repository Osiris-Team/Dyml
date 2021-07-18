package com.osiris.dyml;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

public class RegularTests {

    @Test
    void nullInList() {
        List<String> list = new ArrayList<>();
        list.add(null);
        System.out.println(list);
        list.remove(null);
        System.out.println(list);
    }

    @Test
    void nullInArrays() {
        String[] arr = returnArray(null, null);
        String[] list = arr;
        for (String s :
                list) {
            Assertions.assertNull(s);
        }
    }

    private String[] returnArray(String... arr) {
        return arr;
    }
}
