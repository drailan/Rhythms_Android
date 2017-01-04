package com.rr.rhythms.Helpers;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.AxisValueFormatter;
import com.rr.rhythms.Business.BusinessConstants;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * Created by Huruk on 7/7/2016.
 */
public class CustomXAxisValueFormatter implements AxisValueFormatter {

    private int _scale;
    private DateTime _today;

    public CustomXAxisValueFormatter(int scale, DateTime today) {
        _scale = scale;
        _today = today;
    }

    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        DateTimeFormatter formatter = DateTimeFormat.forPattern(BusinessConstants.DATE_FORMAT_X_AXIS);

        float minusDays = (float)_scale / 2 - value;
        DateTime dValue = _today.minusDays((int)minusDays);

        return dValue.toString(formatter);
    }

    @Override
    public int getDecimalDigits() {
        return 0;
    }
}
