/*
 *  Copyright Osiris Team
 *  All rights reserved.
 *
 *  This software is licensed work.
 *  Please consult the file "LICENSE" for details.
 */

package com.osiris.dyml;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class Dyml {
    /**
     * Contains
     */
    public List<Dyml> childObjects = new ArrayList<>();
    public List<DymlSection> fragments = new ArrayList<>();

    /**
     * Returns the child {@link Dyml} object at the given index.
     */
    public Dyml get(int index){
        return childObjects.get(index);
    }

    /**
     * Returns the {@link DymlSection} with the provided key, or null if not found.
     */
    public DymlSection get(String key){
        for (DymlSection frag :
                fragments) {
            if (frag.key.equals(key))
                return frag;
        }
        return null;
    }

    public static Dyml from(InputStream in){

    }

    public static Dyml from(File file){

    }

    public static Dyml from(String s){

    }

    public static OutputStream to(OutputStream out){

    }

    public static File to(File file){

    }

    public static String to(){

    }
}
