/*
 *  Copyright Osiris Team
 *  All rights reserved.
 *
 *  This software is licensed work.
 *  Please consult the file "LICENSE" for details.
 */

package com.osiris.dyml;

import java.util.List;

/**
 * Useful for very lazy people.
 */
public class M extends DYModule{
    public M(DreamYaml yaml) {
        super(yaml);
    }

    public M(DreamYaml yaml, String... keys) {
        super(yaml, keys);
    }

    public M(DreamYaml yaml, List<String> keys, List<DYValueContainer> defaultValues, List<DYValueContainer> values, List<String> comments) {
        super(yaml, keys, defaultValues, values, comments);
    }

    /**
     * Shortcut for {@link #setValues(String...)}.
     */
    public DYModule v(String... v) {
        return super.setValues(v);
    }

    /**
     * Shortcut for {@link #setDefValues(String...)}.
     */
    public DYModule dv(String... v) {
        return super.setDefValues(v);
    }

    /**
     * Shortcut for {@link #setComments(String...)}.
     */
    public DYModule c(String... c) {
        return super.setComments(c);
    }

    /**
     * Shortcut for {@link #setDefComments(String...)}.
     */
    public DYModule dc(String... c) {
        return super.setDefComments(c);
    }
}
