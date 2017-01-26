package com.rr.rhythms.Entities

import java.io.Serializable

/**
 * Created by Huruk on 7/5/2016.
 */
class Point(private val X: Float, private val Y: Float) : Serializable {

    fun GetX(): Float {
        return X
    }

    fun GetY(): Float {
        return Y
    }
}
