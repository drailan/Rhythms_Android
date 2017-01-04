package com.rr.rhythms.Business.Graphs;

import android.graphics.Color;

import com.rr.rhythms.Entities.GraphInnerState;
import com.rr.rhythms.Entities.Point;
import com.rr.rhythms.Interfaces.IGraph;

import org.joda.time.DateTime;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Huruk on 7/5/2016.
 */
public abstract class GraphBase implements Serializable, IGraph {
    protected ArrayList<Point> points;
    protected int color;

    public abstract int GetPeriod();

    public abstract int getColor();

    public abstract String getName();

    public GraphBase() {
        points = new ArrayList<>();
        color = Color.BLACK;
    }

    private float GetY(int quarter, float dayInPeriod, float daysInQuarter) {
        float state = 0.0f;

        switch (quarter) {
            case 1:
                state = dayInPeriod;
                break;
            case 2:
                state = daysInQuarter * 2 - dayInPeriod;
                break;
            case 3:
                state = -dayInPeriod + daysInQuarter * 2;
                break;
            case 4:
                state = -daysInQuarter * 4 + dayInPeriod;
                break;
            default:
                break;
        }

        return state;
    }

    public void generateGraph(int totalDays, int scale) {
        points = new ArrayList<>();
        float daysInQuarter = (float) GetPeriod() / 4;

        for (int day = 0; day < scale; ++day) {
            float remainder = ((float) (totalDays - scale / 2 + day) / GetPeriod()) % 1.0f;
            float dayInPeriod = GetPeriod() * remainder;
            int quarter = GetQuarter(dayInPeriod, daysInQuarter);

            float state = GetY(quarter, dayInPeriod, daysInQuarter);

            points.add(new Point(day, state));
        }
    }

    protected final int GetQuarter(double dayInPeriod, double daysInQuarter) {
        return dayInPeriod <= daysInQuarter ? 1 :
                dayInPeriod <= daysInQuarter * 2 ? 2 :
                        dayInPeriod <= daysInQuarter * 3 ? 3 :
                                dayInPeriod <= daysInQuarter * 4 ? 4 : -1;
    }

    public final ArrayList<Point> getPoints() {
        return points;
    }

    public final GraphInnerState[] getCurrentStates(int totalDays) {
        float daysInQuarter = (float) GetPeriod() / 4;
        ArrayList<GraphInnerState> states = new ArrayList<>(3);
        int[] days = new int[]{-3, -2, -1, 0, 1, 2, 3};


        for (int day : days) {
            float remainder = ((float) (totalDays + day) / GetPeriod()) % 1.0f;
            float dayInPeriod = GetPeriod() * remainder;

            int quarter = GetQuarter(dayInPeriod, daysInQuarter);

            float state = GetY(quarter, dayInPeriod, daysInQuarter);

            float t = 0;

            if (state < 0.0) {
                t = state / (GetPeriod() / 4);
            } else if (state > 0.0) {
                t = state / (GetPeriod() / 4);
            }

            GraphInnerState gs = new GraphInnerState();
            gs.isGrowing = quarter == 1 || quarter == 4;
            gs.state = t * 100;
            gs.date = DateTime.now().plusDays(day);

            states.add(gs);
        }

        return states.toArray(new GraphInnerState[states.size()]);
    }
}
