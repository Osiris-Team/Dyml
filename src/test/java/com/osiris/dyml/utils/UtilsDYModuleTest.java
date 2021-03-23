package com.osiris.dyml.utils;

import com.osiris.dyml.DYModule;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

class UtilsDYModuleTest {

    @Test
    void createUnifiedList() {
        List<DYModule> addedModules = new ArrayList<>();
        List<DYModule> loadedModules = new ArrayList<>();

        loadedModules.add(new DYModule("p1", "c1"));
        loadedModules.add(new DYModule("p1", "c2"));
        loadedModules.add(new DYModule("p2", "c1", "cc1"));

        addedModules.add(new DYModule("p1", "c1"));
        addedModules.add(new DYModule("p1", "c1", "cc1")); // NEW
        addedModules.add(new DYModule("p1", "c2"));
        addedModules.add(new DYModule("p2", "c1")); // NEW
        addedModules.add(new DYModule("p2", "c1", "cc1"));
        addedModules.add(new DYModule("p3", "c1", "cc1")); // NEW
        addedModules.add(new DYModule("p4", "c1", "cc1")); // NEW

        List<DYModule> unified = new UtilsDYModule().createUnifiedList(addedModules, loadedModules);
        for (DYModule m :
                unified) {
            System.out.println("KEYS" + m.getKeys());
        }



    }
}