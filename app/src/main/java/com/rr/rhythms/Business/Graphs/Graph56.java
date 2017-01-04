package com.rr.rhythms.Business.Graphs;

import android.graphics.Color;

/**
 * Created by Huruk on 7/5/2016.
 */
public class Graph56 extends GraphBase {
    public static final int PERIOD = 56;

    @Override
    public int GetPeriod() {
        return PERIOD;
    }

    @Override
    public int getColor() {
        return Color.YELLOW;
    }

    @Override
    public String getName() {
        return Integer.toString(PERIOD);
    }
}
