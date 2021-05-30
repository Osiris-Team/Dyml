/*
 *  Copyright Osiris Team
 *  All rights reserved.
 *
 *  This software is licensed work.
 *  Please consult the file "LICENSE" for details.
 */

package com.osiris.dyml.utils;

import java.text.DecimalFormat;

public class UtilsTimeStopper {
    private final DecimalFormat df = new DecimalFormat();
    private long time1 = 0;
    private long time2 = 0;

    public void start() {
        time1 = System.nanoTime();
    }

    public void stop() {
        time2 = System.nanoTime();
    }

    public Double getSeconds() {
        return ((time2 - time1) / 1000000D) / 1000D;
    }

    public Double getMillis() {
        return (time2 - time1) / 1000000D;
    }

    public Double getNanos() {
        return (time2 - time1) + 0D;
    }

    public String getFormattedSeconds() {
        Double d = getSeconds();
        return df.format(d).replace(".", ",");
    }

    public String getFormattedMillis() {
        Double d = getMillis();
        return df.format(d).replace(".", ",");
    }

    public String getFormattedNanos() {
        Double d = getNanos();
        return df.format(d).replace(".", ",");
    }

    private void check() throws Exception {
        if (time1 == 0 || time2 == 0) {
            throw new Exception("Time 1 or time 2 are null. Ensure that you have started and stopped the counter before retrieving the result!");
        }
    }
}