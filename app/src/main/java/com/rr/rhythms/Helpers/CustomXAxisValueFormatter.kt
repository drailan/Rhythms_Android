package com.rr.rhythms.Helpers

import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.formatter.AxisValueFormatter
import com.rr.rhythms.Business.BusinessConstants

import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter

/**
 * Created by Huruk on 7/7/2016.
 */
class CustomXAxisValueFormatter(private val _scale: Int, private val _today: DateTime) : AxisValueFormatter {

    override fun getFormattedValue(value: Float, axis: AxisBase): String {
        val formatter = DateTimeFormat.forPattern(BusinessConstants.DATE_FORMAT_X_AXIS)

        val minusDays = _scale.toFloat() / 2 - value
        val dValue = _today.minusDays(minusDays.toInt())

        return dValue.toString(formatter)
    }

    override fun getDecimalDigits(): Int {
        return 0
    }
}
