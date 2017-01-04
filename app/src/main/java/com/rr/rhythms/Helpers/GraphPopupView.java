package com.rr.rhythms.Helpers;

import android.content.Context;
import android.widget.TextView;

import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.rr.rhythms.R;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * Created by Huruk on 7/4/2016.
 */
public class GraphPopupView extends MarkerView {
    private TextView tvContent;
    private DateTime _today;
    private int _scale;

    public GraphPopupView(Context context, int layoutResource) {
        super(context, layoutResource);
        // this markerview only displays a textview
        tvContent = (TextView) findViewById(R.id.tvContent);
    }

    public GraphPopupView(Context context, int layoutResource, int scale, DateTime today) {
        this(context, layoutResource);

        _scale = scale;
        _today = today;
    }

    // callbacks everytime the MarkerView is redrawn, can be used to update the
    // content (user-interface)
    @Override
    public void refreshContent(Entry e, Highlight highlight) {
        DateTimeFormatter formatter = DateTimeFormat.forPattern("MMMM d");

        float minusDays = (float)_scale / 2 - e.getX();
        DateTime dValue = _today.minusDays((int)minusDays);

        tvContent.setText(dValue.toString(formatter));
    }

    @Override
    public int getXOffset(float xpos) {
        // this will center the marker-view horizontally
        return -(getWidth() / 2);
    }

    @Override
    public int getYOffset(float ypos) {
        // this will cause the marker-view to be above the selected value
        return -getHeight();
    }
}
