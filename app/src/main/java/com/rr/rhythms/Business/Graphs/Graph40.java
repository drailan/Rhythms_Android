package com.rr.rhythms.Business.Graphs;

import android.graphics.Color;

/**
 * Created by Huruk on 7/5/2016.
 */
public class Graph40 extends GraphBase {
    public static final int PERIOD = 40;

    @Override
    public int GetPeriod() {
        return PERIOD;
    }

    @Override
    public int getColor() {
        return Color.BLACK;
    }

    @Override
    public String getName() {
        return Integer.toString(PERIOD);
    }
}
