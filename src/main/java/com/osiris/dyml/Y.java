/*
 *  Copyright Osiris Team
 *  All rights reserved.
 *
 *  This software is licensed work.
 *  Please consult the file "LICENSE" for details.
 */

package com.osiris.dyml;

import com.osiris.dyml.exceptions.IllegalKeyException;
import com.osiris.dyml.exceptions.NotLoadedException;

import java.io.File;
import java.io.InputStream;

/**
 * Useful for very lazy people.
 */
public class Y extends DreamYaml {
    public Y(InputStream inputStream) {
        super(inputStream);
    }

    public Y(InputStream inputStream, boolean isDebugEnabled) {
        super(inputStream, isDebugEnabled);
    }

    public Y(File file) {
        super(file);
    }

    public Y(File file, boolean isDebugEnabled) {
        super(file, isDebugEnabled);
    }

    public Y(String filePath) {
        super(filePath);
    }

    public Y(String filePath, boolean isDebugEnabled) {
        super(filePath, isDebugEnabled);
    }

    public Y(InputStream inputStream, boolean isPostProcessingEnabled, boolean isDebugEnabled) {
        super(inputStream, isPostProcessingEnabled, isDebugEnabled);
    }

    public Y(File file, boolean isPostProcessingEnabled, boolean isDebugEnabled) {
        super(file, isPostProcessingEnabled, isDebugEnabled);
    }

    public Y(String filePath, boolean isPostProcessingEnabled, boolean isDebugEnabled) {
        super(filePath, isPostProcessingEnabled, isDebugEnabled);
    }

    /**
     * Shortcut for {@link #put(String...)}.
     */
    public M k(String... keys) throws NotLoadedException, IllegalKeyException {
        //TODO return super.put(keys);
        return null;
    }

}
