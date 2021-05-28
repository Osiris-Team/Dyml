/*
 *  Copyright Osiris Team
 *  All rights reserved.
 *
 *  This software is licensed work.
 *  Please consult the file "LICENSE" for details.
 */

package com.osiris.dyml.utils;

import com.osiris.dyml.DYModule;
import com.osiris.dyml.DYValue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class UtilsDYModule {

    /**
     * Searches for a module with the same keys and returns it if it finds it, else null.
     *
     * @param queryModule use this modules keys to search for a matching module.
     * @param modules     the list in which to search for the module.
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
                    if (i == (size - 1))
                        return listModule;
                }
        }
        return null;
    }

    //TODO Check which (above or below) method is more efficient

    /**
     * Check for duplicate objects (objects with same key).
     *
     * @param modules     the list where to search for the duplicate.
     * @param queryModule the module which should be checked.
     * @return the already existing object, otherwise null.
     */
    public DYModule getExisting(DYModule queryModule, List<DYModule> modules) {
        for (DYModule listModule :
                modules) {
            if (listModule.getKeys().equals(queryModule.getKeys()))
                return listModule;
        }
        return null;
    }

    public DYModule getExisting(List<String> keys, List<DYModule> modules) {
        for (DYModule listModule :
                modules) {
            if (listModule.getKeys().equals(keys))
                return listModule;
        }
        return null;
    }

    public List<DYValue> stringArrayToValuesList(String[] array) {
        return stringListToValuesList(Arrays.asList(array));
    }

    public List<DYValue> stringListToValuesList(List<String> list) {
        List<DYValue> values = new ArrayList<>();
        for (String s :
                list) {
            values.add(new DYValue(s));
        }
        return values;
    }

    public List<String> valuesListToStringList(List<DYValue> list) {
        List<String> stringList = new ArrayList<>();
        for (DYValue value :
                list) {
            stringList.add(value.asString());
        }
        return stringList;
    }

    /**
     * We assume that none of the {@link DYValue}s in the list is null. {@link DYValue#asString()} however can be null.
     */
    public void trimValues(List<DYValue> values) {
        List<DYValue> copy = new ArrayList<>(values); // Iterate thorough a copy, but do changes to the original and avoid ConcurrentModificationException.
        for (DYValue value :
                copy) {
            String s;
            if ((s = value.asString()) != null) {
                s = s.trim();
                value.set(s);
            }
        }
    }

    /**
     * Removes "" and '' from those encapsulated values.<br>
     * Its recommended, that each value was trimmed before, to achieve the best results. <br>
     * We assume that none of the {@link DYValue}s in the list is null. {@link DYValue#asString()} however can be null. <br>
     */
    public void removeQuotesFromValues(List<DYValue> values) {
        List<DYValue> copy = new ArrayList<>(values); // Iterate thorough a copy, but do changes to the original and avoid ConcurrentModificationException.
        for (DYValue value :
                copy) {
            String s;
            if ((s = value.asString()) != null && !s.isEmpty()) {
                // This string must be an already optimized/trimmed string without spaces at the start/end
                char firstChar = s.charAt(0);
                char lastChar = s.charAt(s.length() - 1);
                if (firstChar == lastChar) { // Check if the value is encapsulated
                    int firstPoint = s.codePointAt(0); // Since first and last are the same we only need one of them
                    if (firstPoint == 34 || firstPoint == 39) { // " is 34 and ' is 39
                        s = s.substring(1, s.length() - 1); // Remove the first and last chars
                        value.set(s);
                    }
                }
            }
        }
    }

    /**
     * We assume that none of the {@link DYValue}s in the list is null. {@link DYValue#asString()} however can be null.
     */
    public void removeNullValues(List<DYValue> values) {
        List<DYValue> copy = new ArrayList<>(values); // Iterate thorough a copy, but do changes to the original and avoid ConcurrentModificationException.
        for (int i = 0; i < copy.size(); i++) {
            if (copy.get(i).asString() == null) values.remove(i);
        }
    }

    /**
     * This method returns a new unified list containing the loaded and added modules merged together. <br>
     * The loaded modules list is used as 'base' and is overwritten/extended by the added modules list. <br>
     * This ensures, that the structure(hierarchies) of the loaded file stay the same <br>
     * and that new modules are inserted in the correct position. <br>
     * Logic: <br>
     * 1. If the loaded modules list is empty, nothing needs to be done! Return added modules. <br>
     * 2. Else go through the loaded modules and compare each module with the added modules list.
     * If there is an added module with the same keys, add it to the unified list instead of the loaded module. <br>
     * 3. If there are NEW modules in the added modules list, insert them into the right places of unified list. <br>
     *
     * @return a fresh unified list containing loaded modules extended by added modules.
     */
    public synchronized List<DYModule> createUnifiedList(List<DYModule> addedModules, List<DYModule> loadedModules) {
        List<DYModule> copyAddedModules = new CopyOnWriteArrayList<>();
        copyAddedModules.addAll(addedModules);

        if (loadedModules.isEmpty()) return addedModules;

        List<DYModule> unifiedList = new ArrayList<>();
        // Go through the loadedModules list
        //System.out.println("");
        //System.out.println("Go through the loadedModules list: ");
        for (DYModule m :
                loadedModules) {
            // Check if there is the same 'added module' available
            DYModule existing = getExisting(m, copyAddedModules);
            if (existing != null) {
                unifiedList.add(existing);
                // Also remove it from its own list, so at the end there are only 'new' modules in that list
                copyAddedModules.remove(existing);
                //System.out.println("Added an 'added module' to unified and removed from copyAdded.");
            } else {
                unifiedList.add(m);
                //System.out.println("Added an 'loaded module' to unified.");
            }
        }

        //System.out.println("");
        //System.out.println("Go through the copyAddedModules("+copyAddedModules.size()+") list and add NEW ones to unified: ");
        for (DYModule m :
                unifiedList) {
            //System.out.println("unifiedList: KEY" + m.getKeys());
        }

        for (DYModule m :
                copyAddedModules) {
            //System.out.println("copyAddedModules: KEY" + m.getKeys());
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
                if (uM.getKeys().size() <= size) {
                    // Compare each key
                    int keyMatches = 0;
                    for (int i = 0; i < uM.getKeys().size(); i++) {
                        if (uM.getKeys().get(i).equals(m.getKeys().get(i)))
                            keyMatches++;
                        else
                            break;
                    }
                    // If this has more matches than the highest matches count, overwrite it and set the lastModule
                    if (keyMatches >= highestKeyMatches) {
                        highestKeyMatches = keyMatches;
                        lastModulePos = position;
                        //System.out.println("New high-score: "+highestKeyMatches+" at pos: "+lastModulePos);
                    }
                }

                position++;
            }

            // After going through the whole list, we now have the information we need and may add it to the hashmap
            lastModulesPositions.add(lastModulePos);
        }

        if (lastModulesPositions.isEmpty())
            return unifiedList;
        else {
            // Since we now have the last modules positions we create a new unifiedList and add the NEW modules AFTER the last module
            List<DYModule> fullUnifiedList = new ArrayList<>();

            // We create a new List for each interval. At the end of each list we add the NEW module and add the list to the fullUnifiedList.
            // Example 0-3: will contain all modules from index 0 to 3 PLUS the NEW module at index 4
            // Next example 3-9: 3 gets incremented to 4, so the last module isn't duplicated, so this list contains 4-9 and the new Module at the last index 10
            int lastPos = 0;
            int cycle = 0;
            for (Integer pos : // The last modules index position in the unifiedList
                    lastModulesPositions) {
                //System.out.println("Cycle: "+cycle+" with pos: "+pos);
                List<DYModule> modules = new ArrayList<>();
                // Increment lastPos, so that the last module doesn't get duplicated.
                // Only don't do this for the first cycle, so index 0 doesn't get skipped.
                if (cycle != 0) lastPos++;
                for (int i = lastPos; i <= pos; i++) {
                    modules.add(unifiedList.get(i));
                    //System.out.println("modules added: KEY"+unifiedList.get(i).getKeys());
                }
                modules.add(copyAddedModules.get(cycle));
                //System.out.println("modules added: KEY"+copyAddedModules.get(cycle).getKeys());
                fullUnifiedList.addAll(modules);
                lastPos = pos;
                cycle++;
            }
            return fullUnifiedList;
        }
    }

}
