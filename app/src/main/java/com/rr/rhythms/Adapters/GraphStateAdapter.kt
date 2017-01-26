package com.rr.rhythms.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView

import com.rr.rhythms.Business.BusinessConstants
import com.rr.rhythms.Entities.GraphState
import com.rr.rhythms.R

import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter

import java.util.ArrayList

/**
 * Created by Huruk on 7/15/2016.
 */
class GraphStateAdapter(private val _context: Context, private val _graphStates: ArrayList<GraphState>) : BaseAdapter() {

    override fun getCount(): Int {
        return _graphStates[0].states.size
    }

    override fun getItem(i: Int): GraphState {
        return _graphStates[i]
    }

    override fun getItemId(i: Int): Long {
        return 0
    }

    override fun getView(i: Int, convertView: View?, parent: ViewGroup): View {
        val inflater = _context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        var view: View? = null
        val formatter = DateTimeFormat.forPattern(BusinessConstants.DATE_FORMAT)

        if (convertView == null) {
            view = inflater.inflate(R.layout.graph_state_cell, parent, false)
        } else {
            view = convertView
        }

        val dt = _graphStates[0].states[i].date
        val isToday = dt!!.toLocalDate().isEqual(DateTime.now().toLocalDate())

        val d = _graphStates[0].states[i].date!!.toString(formatter)

        val dateTextBox = view!!.findViewById(R.id.grid_cell_date) as TextView
        dateTextBox.text = d

        val physical = view.findViewById(R.id.grid_cell_text_physical) as TextView
        val emotional = view.findViewById(R.id.grid_cell_text_emotional) as TextView
        val intellect = view.findViewById(R.id.grid_cell_text_intellect) as TextView
        val intuition = view.findViewById(R.id.grid_cell_text_intuition) as TextView

        physical.text = _context.resources.getString(R.string.percentage, _graphStates[0].states[i].state)
        emotional.text = _context.resources.getString(R.string.percentage, _graphStates[1].states[i].state)
        intellect.text = _context.resources.getString(R.string.percentage, _graphStates[2].states[i].state)
        intuition.text = _context.resources.getString(R.string.percentage, _graphStates[3].states[i].state)

        val i1 = view.findViewById(R.id.grid_cell_image_physical) as ImageView
        val i2 = view.findViewById(R.id.grid_cell_image_emotional) as ImageView
        val i3 = view.findViewById(R.id.grid_cell_image_intellect) as ImageView
        val i4 = view.findViewById(R.id.grid_cell_image_intuition) as ImageView

        if (_graphStates[0].states[i].isGrowing) {
            i1.setImageResource(R.drawable.graph_state_up)
        } else {
            i1.setImageResource(R.drawable.graph_state_down)
        }

        if (_graphStates[1].states[i].isGrowing) {
            i2.setImageResource(R.drawable.graph_state_up)
        } else {
            i2.setImageResource(R.drawable.graph_state_down)
        }

        if (_graphStates[2].states[i].isGrowing) {
            i3.setImageResource(R.drawable.graph_state_up)
        } else {
            i3.setImageResource(R.drawable.graph_state_down)
        }

        if (_graphStates[3].states[i].isGrowing) {
            i4.setImageResource(R.drawable.graph_state_up)
        } else {
            i4.setImageResource(R.drawable.graph_state_down)
        }

        if (isToday) {
            dateTextBox.setBackgroundResource(R.drawable.red_border)
            //            view.setBackgroundResource(R.drawable.red_border);
        }
        return view
    }
}
