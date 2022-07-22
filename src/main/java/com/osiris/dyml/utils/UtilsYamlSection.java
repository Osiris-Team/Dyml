/*
 *  Copyright Osiris Team
 *  All rights reserved.
 *
 *  This software is licensed work.
 *  Please consult the file "LICENSE" for details.
 */

package com.osiris.dyml.utils;

import com.osiris.dyml.SmartString;
import com.osiris.dyml.YamlSection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UtilsYamlSection {

    /**
     * Searches for a module with the same keys and returns it if it finds it, else null.
     *
     * @param queryModule use this modules keys to search for a matching module.
     * @param modules     the list in which to search for the module.
     * @return a module containing exactly the same keys or null if it doesn't.
     */
    public YamlSection getAlreadyExistingModuleByKeys(YamlSection queryModule, List<YamlSection> modules) {
        int size = queryModule.getKeys().size();
        for (YamlSection listModule :
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
    public YamlSection getExisting(YamlSection queryModule, List<YamlSection> modules) {
        for (YamlSection listModule :
                modules) {
            if (listModule.getKeys().equals(queryModule.getKeys()))
                return listModule;
        }
        return null;
    }

    public YamlSection getExisting(List<String> keys, List<YamlSection> modules) {
        for (YamlSection listModule :
                modules) {
            if (listModule.getKeys().equals(keys))
                return listModule;
        }
        return null;
    }

    /**
     * Example:
     * g0:
     *   g1:
     *     g2:
     *  We got the section with keys "g0", "g1", "g2",
     *  but the modules list doesn't contain it, thus we need to find
     *  a parent like "g0", "g1".
     */
    public int getClosestParentIndex(List<String> keys, List<YamlSection> sections) {
        int index = -1;
        int highestCountMatchedKeys = 0;
        int currentIndex = 0;
        for (YamlSection section : sections) {
            if(section.getKeys().size() <= keys.size()){
                int countMatched = 0;
                for (int i = 0; i < section.getKeys().size(); i++) {
                    if(section.getKeys().get(i).equals(keys.get(i))){
                        countMatched++;
                    } else break;
                }
                if(countMatched != 0 && countMatched >= highestCountMatchedKeys){
                    highestCountMatchedKeys = countMatched;
                    index = currentIndex;
                }
            }
            currentIndex++;
        }
        return index;
    }

    public List<SmartString> stringArrayToValuesList(String[] array) {
        return stringListToValuesList(Arrays.asList(array));
    }

    public List<SmartString> stringListToValuesList(List<String> list) {
        List<SmartString> values = new ArrayList<>();
        for (String s :
                list) {
            values.add(new SmartString(s));
        }
        return values;
    }

    public List<String> valuesListToStringList(List<SmartString> list) {
        List<String> stringList = new ArrayList<>();
        for (SmartString value :
                list) {
            stringList.add(value.asString());
        }
        return stringList;
    }

    /**
     * We assume that none of the {@link SmartString}s in the list is null. {@link SmartString#asString()} however can be null.
     */
    public void trimValues(List<SmartString> values) {
        List<SmartString> copy = new ArrayList<>(values); // Iterate thorough a copy, but do changes to the original and avoid ConcurrentModificationException.
        for (SmartString value :
                copy) {
            String s;
            if ((s = value.asString()) != null) {
                s = s.trim();
                value.set(s);
            }
        }
    }

    public void trimComments(List<String> comments) {
        List<String> copy = new ArrayList<>(comments); // Iterate thorough a copy, but do changes to the original and avoid ConcurrentModificationException.
        for (int i = 0; i < copy.size(); i++) {
            String c = copy.get(i);
            if (c != null)
                comments.set(i, copy.get(i).trim());
        }
    }

    /**
     * Removes "" and '' from those encapsulated values.<br>
     * Its recommended, that each value was trimmed before, to achieve the best results. <br>
     * We assume that none of the {@link SmartString}s in the list is null. {@link SmartString#asString()} however can be null. <br>
     */
    public void removeQuotesFromValues(List<SmartString> values) {
        List<SmartString> copy = new ArrayList<>(values); // Iterate thorough a copy, but do changes to the original and avoid ConcurrentModificationException.
        for (SmartString value :
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
     * We assume that none of the {@link SmartString}s in the list is null. {@link SmartString#asString()} however can be null.
     */
    public void removeNullValues(List<SmartString> values) {
        List<SmartString> copy = new ArrayList<>(values); // Iterate thorough a copy, but do changes to the original and avoid ConcurrentModificationException.
        for (int i = 0; i < copy.size(); i++) {
            if (copy.get(i).asString() == null) values.remove(i);
        }
    }

}
