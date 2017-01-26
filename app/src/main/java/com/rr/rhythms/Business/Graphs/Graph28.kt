package com.rr.rhythms.Business.Graphs

import android.graphics.Color

/**
 * Created by Huruk on 7/5/2016.
 */
class Graph28 : GraphBase() {
    override val color: Int = Color.BLUE

    override val name: String = period.toString()

    override val period: Int
        get() = 28
}
