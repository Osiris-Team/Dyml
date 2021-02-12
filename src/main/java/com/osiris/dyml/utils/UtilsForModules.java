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
     * If this method is used, it must mean that overwrite is false.
     * This method returns a unified list containing the loaded modules as base, extended by the added modules.
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
     * @param addedModules
     * @param loadedModules
     * @return
     */
    public List<DYModule> createUnifiedList(List<DYModule> addedModules, List<DYModule> loadedModules){
        List<DYModule> copyAddedModules = new CopyOnWriteArrayList<>();
        copyAddedModules.addAll(addedModules);

        List<DYModule> unifiedList = new ArrayList<>();
        // Go through the loadedModules list
        for (DYModule m :
                loadedModules) {
            // Check if there is the same 'added module' available
            DYModule existing = getExisting(m, copyAddedModules);
            if (existing!=null) {
                unifiedList.add(existing);
                // Also remove it from its own list, so at the end there are only 'new' modules in that list
                copyAddedModules.remove(existing);
            }
            else
                unifiedList.add(m);
        }

        // Now the unified list, has its structure from the 'loaded modules'
        // and its latest module values from the 'added modules'.
        // The only missing thing, is that there could be NEW modules in the 'added modules' list.
        // Since we want to keep the loaded modules structure, we insert these new modules to their respective parents end.
        // The 'added modules list' now only contains the NEW modules (modules that didn't match any loaded modules keys).
        // In the following we go through these NEW modules and determine their future positions in the unified list.
        List<Integer> lastModulesPositions = new ArrayList<>();
        for (DYModule m :
                copyAddedModules) {

            int size = m.getKeys().size();
            int position = 0; // The current lists position.
            int highestKeyMatches = 0; // The amount of equal keys (when comparing a 'unified lists module' with the NEW module.

            // This is what we are looking for. The last module before a different parent and its position:
            int lastModulePos = 0;

            for (DYModule uM :
                    unifiedList) {
                if (uM.getKeys().size() <= size){
                    // Compare each key
                    int keyMatches = 0;
                    for (int i = 0; i < uM.getKeys().size(); i++) {
                        if (uM.getKeys().get(i).equals(m.getKeys().get(i)))
                            keyMatches++;
                    }
                    // If this has more matches than the highest matches count, overwrite it and set the lastModule
                    if (keyMatches >= highestKeyMatches) {
                        highestKeyMatches = keyMatches;
                        lastModulePos = position;
                    }
                }

                position++;
            }

            // After going through the whole list, we now have the information we need and may add it to the hashmap
            lastModulesPositions.add(lastModulePos);
        }

        if (lastModulesPositions.isEmpty())
            return unifiedList;
        else{
            // Since we now have the last modules positions we create a new unifiedList and add the NEW modules AFTER the last module
            List<DYModule> fullUnifiedList = new ArrayList<>();

            // We create a new List for each interval. At the end of each list we add the NEW module and add the list to the fullUnifiedList.
            // Example 0-3: will contain all modules from index 0 to 3 PLUS the NEW module at index 4
            // Next example 3-9: 3 gets incremented to 4, so the last module isn't duplicated, so this list contains 4-9 and the new Module at the last index 10
            int lastPos = 0;
            int cycle = 0;
            for (Integer pos :
                    lastModulesPositions) {
                List<DYModule> modules = new ArrayList<>();
                if (lastPos!=0) lastPos++; // Increment lastPos, so that the last module doesn't get duplicated
                for (int i = lastPos; i < pos; i++) {
                    modules.add(unifiedList.get(i));
                }
                modules.add(copyAddedModules.get(cycle));
                fullUnifiedList.addAll(modules);
                lastPos = pos;
                cycle++;
            }
            return fullUnifiedList;
        }
    }

}
