/*
 *  Copyright Osiris Team
 *  All rights reserved.
 *
 *  This software is licensed work.
 *  Please consult the file "LICENSE" for details.
 */

package com.osiris.dyml.utils;

import com.osiris.dyml.DYModule;
import com.osiris.dyml.DYValueContainer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

    public List<DYValueContainer> stringArrayToValuesList(String[] array) {
        return stringListToValuesList(Arrays.asList(array));
    }

    public List<DYValueContainer> stringListToValuesList(List<String> list) {
        List<DYValueContainer> values = new ArrayList<>();
        for (String s :
                list) {
            values.add(new DYValueContainer(s));
        }
        return values;
    }

    public List<String> valuesListToStringList(List<DYValueContainer> list) {
        List<String> stringList = new ArrayList<>();
        for (DYValueContainer value :
                list) {
            stringList.add(value.asString());
        }
        return stringList;
    }

    /**
     * We assume that none of the {@link DYValueContainer}s in the list is null. {@link DYValueContainer#asString()} however can be null.
     */
    public void trimValues(List<DYValueContainer> values) {
        List<DYValueContainer> copy = new ArrayList<>(values); // Iterate thorough a copy, but do changes to the original and avoid ConcurrentModificationException.
        for (DYValueContainer value :
                copy) {
            String s;
            if ((s = value.asString()) != null) {
                s = s.trim();
                value.set(s);
            }
        }
    }

    public void trimValuesComments(List<DYValueContainer> values) {
        List<DYValueContainer> copy = new ArrayList<>(values); // Iterate thorough a copy, but do changes to the original and avoid ConcurrentModificationException.
        for (DYValueContainer value :
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
     * We assume that none of the {@link DYValueContainer}s in the list is null. {@link DYValueContainer#asString()} however can be null. <br>
     */
    public void removeQuotesFromValues(List<DYValueContainer> values) {
        List<DYValueContainer> copy = new ArrayList<>(values); // Iterate thorough a copy, but do changes to the original and avoid ConcurrentModificationException.
        for (DYValueContainer value :
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
     * We assume that none of the {@link DYValueContainer}s in the list is null. {@link DYValueContainer#asString()} however can be null.
     */
    public void removeNullValues(List<DYValueContainer> values) {
        List<DYValueContainer> copy = new ArrayList<>(values); // Iterate thorough a copy, but do changes to the original and avoid ConcurrentModificationException.
        for (int i = 0; i < copy.size(); i++) {
            if (copy.get(i).asString() == null) values.remove(i);
        }
    }
}
