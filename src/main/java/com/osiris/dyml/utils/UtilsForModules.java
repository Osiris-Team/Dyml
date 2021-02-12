/*
 *  Copyright Osiris Team
 *  All rights reserved.
 *
 *  This software is licensed work.
 *  Please consult the file "LICENSE" for details.
 */

package com.osiris.dyml.utils;

import com.osiris.dyml.DYModule;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class UtilsForModules {

    /**
     * Searches for a module with the same keys and returns it if it finds it, else null.
     * @param queryModule use this modules keys to search for a matching module.
     * @param modules the list in which to search for the module.
     * @return a module containing exactly the same keys or null if it doesn't.
     */
    public DYModule getAlreadyExistingModuleByKeys(DYModule queryModule, List<DYModule> modules) {
        int size = queryModule.getKeys().size();
        for (DYModule listModule :
                modules) {
            if (size == listModule.getKeys().size())
                for (int i = 0; i < size; i++) {
                    if (!queryModule.getKeys().get(i).equals(listModule.getKeys().get(i)))
                        break;
                    if (i==(size-1))
                        return listModule;
                }
        }
        return null;
    }

    //TODO Check which (above or below) method is more efficient
    /**
     * Check for duplicate objects (objects with same key).
     * @param modules
     * @param queryModule
     * @return the already existing object, otherwise null.
     */
    public DYModule getExisting(DYModule queryModule, List<DYModule> modules){
        for (DYModule listModule :
                modules) {
            if (listModule.getKeys().equals(queryModule.getKeys()))
                return listModule;
        }
        return null;
    }

    public DYModule getExisting(List<String> keys, List<DYModule> modules){
        for (DYModule listModule :
                modules) {
            if (listModule.getKeys().equals(keys))
                return listModule;
        }
        return null;
    }

    /**
     * Before writing the modules to file we need to unify the defaultModules list with the loadedModules list.
     * For simplicity we call the defaultModules list D-List and the loadedModules list L-List.
     * Logic:
     * 1. Check which list is bigger. D-List or L-List?
     * 2. Pick the bigger list and go through its modules and complete its data.
     * 3. If the bigger list is the D-List its 'real-values' wont be overwritten.
     * 4. If the bigger list is the L-List its 'real-values' get overwritten from the D-List
     * Create a new list, then go thorough the defaultModules list, and check if there is a matching key in the loadedModules list.
     * If there is a match add that module instead, and overwrite its defaultValues and comments.
     * This ensures, that loaded modules which do not exist in the defaultModules list do not get saved to file.
     * Using save() will result in overwriting the file with the current modules. The current modules get their values from the loadedModules.
     * LIST A (5)
     * LIST B (2)
     * 'hello'
     * 'hello' 'hi'
     * 'hello' 'hi' 'sup'
     *
     * 'hello'
     * 'hello' 'hi'
     * 'hello' 'boi'
     * 'hello' 'boi' 'sup'
     * @param defaultModules
     * @param loadedModules
     * @return
     */
    public List<DYModule> createUnifiedList(List<DYModule> defaultModules, List<DYModule> loadedModules){
        List<DYModule> copyDefaultModules = new CopyOnWriteArrayList<>();
        copyDefaultModules.addAll(defaultModules);
        List<DYModule> copyLoadedModules = new CopyOnWriteArrayList<>();
        copyLoadedModules.addAll(loadedModules);


        List<DYModule> raw1UnifiedList = new ArrayList<>();
        List<DYModule> raw2UnifiedList = new ArrayList<>();

        // First go through the loaded list
        DYModule existing;
        for (DYModule m :
                loadedModules) {
            existing = getExisting(m, defaultModules);
            if (existing!=null)
                raw1UnifiedList.add(existing);
            else
                raw1UnifiedList.add(m);
        }

        // Then go through the default list and add missing modules
        // Only add a default module if its not already existing in the unified list
        for (DYModule m :
                defaultModules) {
            existing = getExisting(m, raw1UnifiedList);
            if (existing==null)
                raw1UnifiedList.add(m);
        }

        // Then we sort the list by their keys size
        // This gives a performance boost
        // For that we need to determine the highest keys size before
        int biggest = 0;
        for (DYModule m :
                raw1UnifiedList) {
            if (biggest < m.getKeys().size())
                biggest = m.getKeys().size();
        }

        // Then we sort them by their sizes
        for (int i = 0; i < biggest; i++) {
            for (DYModule m :
                    raw1UnifiedList) {
                if (m.getKeys().size() == i)
                    raw2UnifiedList.add(m);
            }
        }


        for (DYModule m0 :
                defaultModules) {
            DYModule m1 = getExisting(m0, loadedModules); // Check the loadedModules list for an already existing module with the same key as this default module and get it
            if (m1!=null && !m1.getValues().isEmpty()) { // Only add the default module if the loaded module has no values
                m1.setDefValues(m0.getDefaultValues());
                m1.setComments(m0.getComments());
                raw1UnifiedList.add(m1);
            }
            else
                raw1UnifiedList.add(m0);
        }
        return raw1UnifiedList;
    }

}
