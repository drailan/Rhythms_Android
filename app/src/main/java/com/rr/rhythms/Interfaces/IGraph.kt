package com.rr.rhythms.Interfaces

import com.rr.rhythms.Entities.GraphInnerState
import com.rr.rhythms.Entities.Point

import java.util.ArrayList

/**
 * Created by Huruk on 7/5/2016.
 */
interface IGraph {
    val points: ArrayList<Point>

    val name: String

    val color: Int

    fun generateGraph(totalDays: Int, scale: Int)

    fun getCurrentStates(todalDays: Int): Array<GraphInnerState>
}
