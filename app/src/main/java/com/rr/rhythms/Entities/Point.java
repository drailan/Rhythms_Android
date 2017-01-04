package com.rr.rhythms.Entities;

import java.io.Serializable;

/**
 * Created by Huruk on 7/5/2016.
 */
public class Point implements Serializable {
    private float X;
    private float Y;

    public Point(float x, float y) {
        X = x;
        Y = y;
    }

    public float GetX() {
        return X;
    }

    public float GetY() {
        return Y;
    }
}
