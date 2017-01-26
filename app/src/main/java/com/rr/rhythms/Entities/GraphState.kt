package com.rr.rhythms.Entities

import com.rr.rhythms.Interfaces.IGraph

import org.joda.time.DateTime

/**
 * Created by Huruk on 7/14/2016.
 */
class GraphState(var parent: IGraph, var states: Array<GraphInnerState>) {
    var date: DateTime? = null
}
