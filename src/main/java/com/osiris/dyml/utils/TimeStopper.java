/*
 *  Copyright Osiris Team
 *  All rights reserved.
 *
 *  This software is licensed work.
 *  Please consult the file "LICENSE" for details.
 */

package com.osiris.dyml.utils;

import java.time.Duration;
import java.time.Instant;

public class TimeStopper {
    private Instant time1;
    private Instant time2;

    public void start(){time1 = Instant.now();}
    public void stop(){time2 = Instant.now();}

    public long getSeconds() throws Exception{
        check();
        return Duration.between(time1, time2).toMillis()/1000;
    }

    public long getMillis() throws Exception{
        check();
        return Duration.between(time1, time2).toMillis();
    }

    public long getNanos() throws Exception{
        check();
        return Duration.between(time1, time2).toNanos();
    }

    private void check() throws Exception{
        if (time1==null || time2==null){
            throw new Exception("Time 1 or time 2 are null. Ensure that you have started and stopped the counter before retrieving the result!");
        }
    }
}
