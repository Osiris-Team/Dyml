/*
 *  Copyright Osiris Team
 *  All rights reserved.
 *
 *  This software is licensed work.
 *  Please consult the file "LICENSE" for details.
 */

package com.osiris.dyml.watcher;

public interface DYFileEventListener<E extends DYFileEvent> {
    void runOnEvent(E event);
}
