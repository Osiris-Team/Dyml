/*
 *  Copyright Osiris Team
 *  All rights reserved.
 *
 *  This software is licensed work.
 *  Please consult the file "LICENSE" for details.
 */

package com.osiris.dyml.watcher;

public interface FileEventListener<E extends FileEvent> {
    void runOnEvent(E event);
}
