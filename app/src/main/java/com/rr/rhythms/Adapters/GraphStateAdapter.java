package com.rr.rhythms.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.rr.rhythms.Business.BusinessConstants;
import com.rr.rhythms.Entities.GraphState;
import com.rr.rhythms.R;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;

/**
 * Created by Huruk on 7/15/2016.
 */
public class GraphStateAdapter extends BaseAdapter {

    private final Context _context;
    private final ArrayList<GraphState> _graphStates;

    public GraphStateAdapter(Context context, ArrayList<GraphState> graphStates) {
        _context = context;
        _graphStates = graphStates;
    }

    @Override
    public int getCount() {
        return _graphStates.get(0).states.length;
    }

    @Override
    public GraphState getItem(int i) {
        return _graphStates.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) _context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = null;
        DateTimeFormatter formatter = DateTimeFormat.forPattern(BusinessConstants.DATE_FORMAT);

        if (convertView == null) {
            view = inflater.inflate(R.layout.graph_state_cell, parent, false);
        } else {
            view = convertView;
        }

        DateTime dt = _graphStates.get(0).states[i].date;
        boolean isToday = dt.toLocalDate().isEqual(DateTime.now().toLocalDate());

        String d = _graphStates.get(0).states[i].date.toString(formatter);

        TextView dateTextBox = (TextView) view.findViewById(R.id.grid_cell_date);
        dateTextBox.setText(d);

        TextView physical = (TextView) view.findViewById(R.id.grid_cell_text_physical);
        TextView emotional = (TextView) view.findViewById(R.id.grid_cell_text_emotional);
        TextView intellect = (TextView) view.findViewById(R.id.grid_cell_text_intellect);
        TextView intuition = (TextView) view.findViewById(R.id.grid_cell_text_intuition);

        physical.setText(_context.getResources().getString(R.string.percentage, _graphStates.get(0).states[i].state));
        emotional.setText(_context.getResources().getString(R.string.percentage, _graphStates.get(1).states[i].state));
        intellect.setText(_context.getResources().getString(R.string.percentage, _graphStates.get(2).states[i].state));
        intuition.setText(_context.getResources().getString(R.string.percentage, _graphStates.get(3).states[i].state));

        ImageView i1 = (ImageView) view.findViewById(R.id.grid_cell_image_physical);
        ImageView i2 = (ImageView) view.findViewById(R.id.grid_cell_image_emotional);
        ImageView i3 = (ImageView) view.findViewById(R.id.grid_cell_image_intellect);
        ImageView i4 = (ImageView) view.findViewById(R.id.grid_cell_image_intuition);

        if (_graphStates.get(0).states[i].isGrowing) {
            i1.setImageResource(R.drawable.graph_state_up);
        } else {
            i1.setImageResource(R.drawable.graph_state_down);
        }

        if (_graphStates.get(1).states[i].isGrowing) {
            i2.setImageResource(R.drawable.graph_state_up);
        } else {
            i2.setImageResource(R.drawable.graph_state_down);
        }

        if (_graphStates.get(2).states[i].isGrowing) {
            i3.setImageResource(R.drawable.graph_state_up);
        } else {
            i3.setImageResource(R.drawable.graph_state_down);
        }

        if (_graphStates.get(3).states[i].isGrowing) {
            i4.setImageResource(R.drawable.graph_state_up);
        } else {
            i4.setImageResource(R.drawable.graph_state_down);
        }

        if (isToday) {
            dateTextBox.setBackgroundResource(R.drawable.red_border);
//            view.setBackgroundResource(R.drawable.red_border);
        }
        return view;
    }
}
