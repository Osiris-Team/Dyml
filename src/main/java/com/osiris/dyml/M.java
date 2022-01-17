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
public class M extends YamlSection {
    public M(Yaml yaml) {
        super(yaml);
    }

    public M(Yaml yaml, String... keys) {
        super(yaml, keys);
    }

    public M(Yaml yaml, List<String> keys, List<YamlValue> defaultValues, List<YamlValue> values, List<String> comments) {
        super(yaml, keys, defaultValues, values, comments);
    }

    /**
     * Shortcut for {@link #setValues(String...)}.
     */
    public YamlSection v(String... v) {
        return super.setValues(v);
    }

    /**
     * Shortcut for {@link #setDefValues(String...)}.
     */
    public YamlSection dv(String... v) {
        return super.setDefValues(v);
    }

    /**
     * Shortcut for {@link #setComments(String...)}.
     */
    public YamlSection c(String... c) {
        return super.setComments(c);
    }

    /**
     * Shortcut for {@link #setDefComments(String...)}.
     */
    public YamlSection dc(String... c) {
        return super.setDefComments(c);
    }
}
