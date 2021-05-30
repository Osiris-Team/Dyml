package com.osiris.dyml;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RegularTests {

    @Test
    void nullInList() {
        List<String> list = new ArrayList<>();
        list.add(null);
        System.out.println(list.toString());
        list.remove(null);
        System.out.println(list.toString());
    }

    @Test
    void nullInArrays() {
        String[] arr = returnArray(null, null);
        List<String> list = Arrays.asList(arr);
        for (String s :
                list) {
            Assertions.assertNull(s);
        }
    }

    private String[] returnArray(String... arr){
        return arr;
    }
}
