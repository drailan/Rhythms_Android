package com.rr.rhythms.Entities;

import com.rr.rhythms.Interfaces.IGraph;

import org.joda.time.DateTime;

/**
 * Created by Huruk on 7/14/2016.
 */
public class GraphState {
    public IGraph parent;
    public DateTime date;
    public GraphInnerState[] states;

    public GraphState(IGraph p, GraphInnerState[] s) {
        parent = p;
        states = s;
    }
}
