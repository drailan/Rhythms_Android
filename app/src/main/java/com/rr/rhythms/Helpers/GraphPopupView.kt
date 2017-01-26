package com.rr.rhythms.Helpers

import android.content.Context
import android.widget.TextView

import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.rr.rhythms.R

import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter

/**
 * Created by Huruk on 7/4/2016.
 */
class GraphPopupView(context: Context, layoutResource: Int) : MarkerView(context, layoutResource) {
    private val tvContent: TextView
    private var _today: DateTime = DateTime.now()
    private var _scale: Int = 30

    init {
        // this markerview only displays a textview
        tvContent = findViewById(R.id.tvContent) as TextView
    }

    constructor(context: Context, layoutResource: Int, scale: Int, today: DateTime) : this(context, layoutResource) {
        _scale = scale
        _today = today
    }

    // callbacks everytime the MarkerView is redrawn, can be used to update the
    // content (user-interface)
    override fun refreshContent(e: Entry, highlight: Highlight) {
        val formatter = DateTimeFormat.forPattern("MMMM d")

        val minusDays = _scale.toFloat() / 2 - e.x
        val dValue = _today.minusDays(minusDays.toInt())

        tvContent.text = dValue.toString(formatter)
    }

    override fun getXOffset(xpos: Float): Int {
        // this will center the marker-view horizontally
        return -(width / 2)
    }

    override fun getYOffset(ypos: Float): Int {
        // this will cause the marker-view to be above the selected value
        return -height
    }
}
