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
import java.util.Objects;
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

    public void trimValuesComments(List<DYValue> values) {
        List<DYValue> copy = new ArrayList<>(values); // Iterate thorough a copy, but do changes to the original and avoid ConcurrentModificationException.
        for (DYValue value :
                copy) {
            if (value.hasComment())
                value.setComment(value.getComment().trim());
        }
    }

    public void trimComments(List<String> comments) {
        List<String> copy = new ArrayList<>(comments); // Iterate thorough a copy, but do changes to the original and avoid ConcurrentModificationException.
        for (String comment :
                copy) {
            if (comment != null) {
                comment = comment.trim();
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
    public synchronized List<DYModule> createUnifiedList(List<DYModule> inEditModules, List<DYModule> loadedModules) {
        if (loadedModules.isEmpty()) return inEditModules;

        List<DYModule> copyInEditModules = new CopyOnWriteArrayList<>(inEditModules);
        List<DYModule> unifiedList = new ArrayList<>();
        // Go through the loadedModules list
        //System.out.println("Go through the loadedModules list: ");
        for (DYModule m :
                loadedModules) {
            // Check if there is the same 'added module' available
            DYModule existing = getExisting(m, copyInEditModules);
            if (existing != null) {
                unifiedList.add(existing);
                // Also remove it from its own list, so at the end there are only 'new' modules in that list
                copyInEditModules.remove(existing);
                //System.out.println("Added an 'added module' to unified and removed from copyAdded.");
            } else {
                unifiedList.add(m);
                //System.out.println("Added an 'loaded module' to unified.");
            }
        }

        //System.out.println("");
        //System.out.println("Go through the copyInEditModules("+copyInEditModules.size()+") list and add NEW ones to unified: ");
        for (DYModule m :
                unifiedList) {
            //System.out.println("unifiedList: KEY" + m.getKeys());
        }

        for (DYModule m :
                copyInEditModules) {
            //System.out.println("copyInEditModules: KEY" + m.getKeys());
        }

        // The copyInEditModules, now only contains completely new modules.
        // Go through that list, add G0 modules to the end of the unifiedModules list and
        // other generations to their respective parents, as first module.
        for (DYModule m :
                copyInEditModules) {

            if (m.getKeys().size() > 1) {
                DYModule parent = new UtilsDYModule().getExisting(m.getKeys().subList(0, m.getKeys().size() - 1), loadedModules);
                Objects.requireNonNull(parent);
                int parentIndex = 0;
                for (DYModule uM :
                        unifiedList) {
                    if (uM.getKeys().equals(parent.getKeys())) { // Do this to find the parents position in the unified list
                        unifiedList.add(parentIndex + 1, m);
                        break;
                    }
                    parentIndex++;
                }
            } else {
                unifiedList.add(m); // G0 modules get added to the end of the file
            }
        }
        return unifiedList;
    }
}
