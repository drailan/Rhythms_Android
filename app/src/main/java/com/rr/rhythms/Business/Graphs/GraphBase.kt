package com.rr.rhythms.Business.Graphs

import android.graphics.Color

import com.rr.rhythms.Entities.GraphInnerState
import com.rr.rhythms.Entities.Point
import com.rr.rhythms.Interfaces.IGraph

import org.joda.time.DateTime

import java.io.Serializable
import java.util.ArrayList

/**
 * Created by Huruk on 7/5/2016.
 */
abstract class GraphBase : Serializable, IGraph {
    override val points: ArrayList<Point> = ArrayList()
    override val color: Int = Color.BLACK;

    abstract val period: Int

    abstract override val name: String

    private fun GetY(quarter: Int, dayInPeriod: Float, daysInQuarter: Float): Float {
        var state = 0.0f

        when (quarter) {
            1 -> state = dayInPeriod
            2 -> state = daysInQuarter * 2 - dayInPeriod
            3 -> state = -dayInPeriod + daysInQuarter * 2
            4 -> state = -daysInQuarter * 4 + dayInPeriod
            else -> {
            }
        }

        return state
    }

    override fun generateGraph(totalDays: Int, scale: Int) {
        points.clear();

        val daysInQuarter = period.toFloat() / 4

        for (day in 0..scale - 1) {
            val remainder = (totalDays - scale / 2 + day).toFloat() / period % 1.0f
            val dayInPeriod = period * remainder
            val quarter = GetQuarter(dayInPeriod.toDouble(), daysInQuarter.toDouble())

            val state = GetY(quarter, dayInPeriod, daysInQuarter)

            points.add(Point(day.toFloat(), state))
        }
    }

    protected fun GetQuarter(dayInPeriod: Double, daysInQuarter: Double): Int {
        return if (dayInPeriod <= daysInQuarter)
            1
        else if (dayInPeriod <= daysInQuarter * 2)
            2
        else if (dayInPeriod <= daysInQuarter * 3)
            3
        else if (dayInPeriod <= daysInQuarter * 4) 4 else -1
    }

    override fun getCurrentStates(totalDays: Int): Array<GraphInnerState> {
        val daysInQuarter = period.toFloat() / 4
        val states = ArrayList<GraphInnerState>(3)
        val days = intArrayOf(-3, -2, -1, 0, 1, 2, 3)


        for (day in days) {
            val remainder = (totalDays + day).toFloat() / period % 1.0f
            val dayInPeriod = period * remainder

            val quarter = GetQuarter(dayInPeriod.toDouble(), daysInQuarter.toDouble())

            val state = GetY(quarter, dayInPeriod, daysInQuarter)

            var t = 0f

            if (state < 0.0) {
                t = state / (period / 4)
            } else if (state > 0.0) {
                t = state / (period / 4)
            }

            val gs = GraphInnerState()
            gs.isGrowing = quarter == 1 || quarter == 4
            gs.state = t * 100
            gs.date = DateTime.now().plusDays(day)

            states.add(gs)
        }

        return states.toTypedArray()
    }
}
