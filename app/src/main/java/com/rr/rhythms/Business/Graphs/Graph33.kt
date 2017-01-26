package com.rr.rhythms.Business.Graphs

import android.graphics.Color

/**
 * Created by Huruk on 7/5/2016.
 */
class Graph33 : GraphBase() {
    override val color: Int = Color.GREEN

    override val name: String = period.toString()

    override val period: Int
        get() = 33
}
