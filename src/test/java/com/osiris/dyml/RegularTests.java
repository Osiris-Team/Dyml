package com.osiris.dyml;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
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
}
