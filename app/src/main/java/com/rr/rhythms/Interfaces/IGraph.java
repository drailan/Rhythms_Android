package com.rr.rhythms.Interfaces;

import com.rr.rhythms.Entities.GraphInnerState;
import com.rr.rhythms.Entities.Point;

import java.util.ArrayList;

/**
 * Created by Huruk on 7/5/2016.
 */
public interface IGraph {
    ArrayList<Point> getPoints();

    int getColor();

    String getName();

    void generateGraph(int totalDays, int scale);

    GraphInnerState[] getCurrentStates(int todalDays);
}
