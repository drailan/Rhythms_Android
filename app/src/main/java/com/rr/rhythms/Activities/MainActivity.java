package com.rr.rhythms.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.RadioGroup;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.rr.rhythms.Adapters.GraphStateAdapter;
import com.rr.rhythms.Business.BusinessConstants;
import com.rr.rhythms.Business.Graphs.Graph24;
import com.rr.rhythms.Business.Graphs.Graph276;
import com.rr.rhythms.Business.Graphs.Graph28;
import com.rr.rhythms.Business.Graphs.Graph33;
import com.rr.rhythms.Business.Graphs.Graph40;
import com.rr.rhythms.Business.Graphs.Graph56;
import com.rr.rhythms.Business.Graphs.Graph92;
import com.rr.rhythms.Entities.GraphInnerState;
import com.rr.rhythms.Entities.GraphState;
import com.rr.rhythms.Entities.People;
import com.rr.rhythms.Entities.Person;
import com.rr.rhythms.Entities.Point;
import com.rr.rhythms.Fragments.DatePickerFragment;
import com.rr.rhythms.Helpers.CustomXAxisValueFormatter;
import com.rr.rhythms.Helpers.GraphPopupView;
import com.rr.rhythms.Interfaces.IActionBarAware;
import com.rr.rhythms.Interfaces.IDateSettable;
import com.rr.rhythms.Interfaces.IGraph;
import com.rr.rhythms.Interfaces.IPerson;
import com.rr.rhythms.R;

import net.hockeyapp.android.CrashManager;
import net.hockeyapp.android.UpdateManager;

import org.joda.time.DateTime;
import org.joda.time.Days;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements OnChartValueSelectedListener, OnChartGestureListener, IDateSettable, IActionBarAware {

    private Calendar _firstBirthDate, _secondBirthDate, _startDate;
    private LineChart _mChart;
    private int _scale = 30;
    private boolean _isMain = true;
    private ArrayList<IGraph> _graphs;
    private Menu _menu;

    public Calendar getFirstBirthDate() {
        return _firstBirthDate;
    }

    public Calendar getSecondBirthDate() {
        return _secondBirthDate;
    }

    public Calendar getStartDate() {
        return _startDate;
    }

    public void setFirstBirthDate(Calendar cal) {
        _firstBirthDate = cal;
        Button b = (Button) findViewById(R.id.mainDatePicker);

        DateFormat format1 = SimpleDateFormat.getDateInstance();
        b.setText(format1.format(cal.getTime()));

        buildChartAndState();
    }

    public void setSecondBirthDate(Calendar cal) {
        _secondBirthDate = cal;
        Button b = (Button) findViewById(R.id.auxDatePicker);

        DateFormat format1 = SimpleDateFormat.getDateInstance();
        b.setText(format1.format(cal.getTime()));
    }

    public void setDate(Calendar cal) {
        _startDate = cal;

        DateFormat format1 = SimpleDateFormat.getDateInstance();

        MenuItem m = _menu.findItem(R.id.pick_date);
        if (m != null) {
            m.setTitle(format1.format(_startDate.getTime()));
        }

        buildChartAndState();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkForUpdates();

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        getSupportActionBar().setTitle("Home");
        initializeGraphs();
        initRadioGroups();
        initButtons();
    }

    private void initializeGraphs() {
        _graphs = new ArrayList<>();
        _graphs.add(new Graph24());
        _graphs.add(new Graph28());
        _graphs.add(new Graph33());
        _graphs.add(new Graph40());
        _graphs.add(new Graph56());
        _graphs.add(new Graph92());
        _graphs.add(new Graph276());
    }

    private void initButtons() {
        final Button b = (Button) findViewById(R.id.mainDatePicker);
        final Button b1 = (Button) findViewById(R.id.auxDatePicker);

        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePickerDialog(view);
            }
        });

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePickerDialog(view);
            }
        });
    }

    private void initRadioGroups() {
        final RadioGroup rg = (RadioGroup) findViewById(R.id.scaleRadioGroup);

        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int index) {
                switch (index) {
                    case R.id.scale30:
                        _scale = 30;
                        break;
                    case R.id.scale60:
                        _scale = 60;
                        break;
                    case R.id.scale90:
                        _scale = 90;
                        break;
                    default:
                        break;
                }

                buildChartAndState();
            }
        });

        RadioGroup rg1 = (RadioGroup) findViewById(R.id.graphRadioGroup);

        rg1.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int index) {
                switch (index) {
                    case R.id.graphMain:
                        _isMain = true;
                        break;
                    case R.id.graphAux:
                        _isMain = false;
                        break;
                    default:
                        break;
                }

                buildChartAndState();
            }
        });
    }

    private void buildChartAndState() {
        if (_firstBirthDate != null) {
            for (int i = 0; i < _graphs.size(); ++i) {
                _graphs.get(i).generateGraph(getTotalDays(_firstBirthDate), _isMain ? _scale : BusinessConstants.AUX_DATE_SCALE);
            }

            DrawChart();
//            BuildGraphState();
        }
    }

    private void BuildGraphState() {
//        GridView grid = (GridView) findViewById(R.id.graph_state_grid);
//
//        if (grid != null) {
//
//            ArrayList<GraphState> graphStates = new ArrayList<>();
//
//            for (IGraph graph : _graphs.subList(0, _graphs.size() - 3)) {
//                GraphInnerState[] currentStates = graph.getCurrentStates(getTotalDays(_firstBirthDate));
//                graphStates.add(new GraphState(graph, currentStates));
//            }
//
//            GraphStateAdapter _adapter = new GraphStateAdapter(this, graphStates);
//
//            grid.setNumColumns(graphStates.get(0).states.length);
//            grid.setAdapter(_adapter);
//
//            setDynamicWidth(grid);
//        }
    }

    private void setDynamicWidth(GridView gridView) {
        ListAdapter gridViewAdapter = gridView.getAdapter();
        if (gridViewAdapter == null) {
            return;
        }
        int totalWidth;
        int items = gridViewAdapter.getCount();
        View listItem = gridViewAdapter.getView(0, null, gridView);
        listItem.measure(0, 0);
        totalWidth = listItem.getMeasuredWidth();
        totalWidth = totalWidth * items;
        ViewGroup.LayoutParams params = gridView.getLayoutParams();
        params.width = totalWidth;
        gridView.setLayoutParams(params);
    }

    private int getTotalDays(Calendar birthDate) {
        DateTime today = _startDate == null ? DateTime.now() : new DateTime(_startDate);
        DateTime birthday = new DateTime(birthDate.getTime());

        Days days = Days.daysBetween(birthday, today);

        return days.getDays();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        _menu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                Intent settingsIntent = new Intent(this, SettingsActivity.class);
                startActivity(settingsIntent);
                return true;
            case R.id.action_add:
                Intent managePeopleIntent = new Intent(this, ManagePeopleActivity.class);
                startActivityForResult(managePeopleIntent, BusinessConstants.MANAGE_PEOPLE_ACTIIVY_REQUEST_CODE);
                return true;
            case R.id.pick_date:
                showDatePickerDialog();
                return true;
            case R.id.action_today:
                setDate(DateTime.now().toCalendar(Locale.getDefault()));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkForCrashes();
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterManagers();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterManagers();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if (_firstBirthDate != null) {
            outState.putLong("mainDate", _firstBirthDate.getTimeInMillis());
        }

        if (_secondBirthDate != null) {
            outState.putLong("auxDate", _secondBirthDate.getTimeInMillis());
        }
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        restoreUI(savedInstanceState);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == BusinessConstants.MANAGE_PEOPLE_ACTIIVY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                IPerson selectedPerson = (IPerson) data.getExtras().getSerializable(BusinessConstants.MANAGE_PEOPLE_SELECTED_PERSON);

                if (selectedPerson instanceof Person) {
                    _firstBirthDate = ((Person) selectedPerson).getBirthDate().toCalendar(Locale.getDefault());
                } else {
                    assert selectedPerson != null;
                    _firstBirthDate = ((People) selectedPerson).getAt(0).getBirthDate().toCalendar(Locale.getDefault());
                    _secondBirthDate = ((People) selectedPerson).getAt(1).getBirthDate().toCalendar(Locale.getDefault());
                }

                setButtonText();
                buildChartAndState();
            }
        } else if (requestCode == BusinessConstants.ACTION_BAR_DATE_PICKER_REQUEST_CODE) {

        }
    }

    private void restoreUI(Bundle savedState) {
//region Date picker buttons
        long t1 = savedState.getLong("mainDate", 0);
        long t2 = savedState.getLong("auxDate", 0);

        DateFormat format1 = SimpleDateFormat.getDateInstance();
        Button b;

        if (t1 != 0) {
            _firstBirthDate = Calendar.getInstance();
            _firstBirthDate.setTimeInMillis(t1);

            b = (Button) findViewById(R.id.mainDatePicker);
            b.setText(format1.format(_firstBirthDate.getTime()));
        }

        if (t2 != 0) {
            _secondBirthDate = Calendar.getInstance();
            _secondBirthDate.setTimeInMillis(t2);

            b = (Button) findViewById(R.id.auxDatePicker);
            b.setText(format1.format(_secondBirthDate.getTime()));
        }

//endregion

        buildChartAndState();
    }

    private void setButtonText() {
        DateFormat format1 = SimpleDateFormat.getDateInstance();

        Button b;

        if (_firstBirthDate != null) {
            b = (Button) findViewById(R.id.mainDatePicker);
            b.setText(format1.format(_firstBirthDate.getTime()));
        }

        if (_secondBirthDate != null) {
            b = (Button) findViewById(R.id.auxDatePicker);
            b.setText(format1.format(_secondBirthDate.getTime()));
        }
    }

    private void checkForCrashes() {
        CrashManager.register(this);
    }

    private void checkForUpdates() {
        // Remove this for store builds!
        UpdateManager.register(this);
    }

    private void unregisterManagers() {
        UpdateManager.unregister();
    }

    public void showDatePickerDialog(View v) {
        DatePickerFragment dialog = new DatePickerFragment();

        switch (v.getId()) {
            case R.id.mainDatePicker:
                dialog.isMain = true;
                break;
            case R.id.auxDatePicker:
                dialog.isMain = false;
                break;
            default:
                break;
        }

        dialog.show(getSupportFragmentManager(), "datePicker");
    }

    public void showDatePickerDialog() {
        DatePickerFragment dialog = new DatePickerFragment();
        dialog.isFromToolbar = true;
        dialog.show(getSupportFragmentManager(), "datePicker");
    }

    private void DrawChart() {
        _mChart = (LineChart) findViewById(R.id.chart);
        _mChart.setOnChartGestureListener(this);
        _mChart.setOnChartValueSelectedListener(this);
        _mChart.setDrawGridBackground(false);

        // no description text
        _mChart.setDescription("");
        _mChart.setNoDataTextDescription("You need to provide data for the chart.");

        // enable touch gestures
        _mChart.setTouchEnabled(true);

        // enable scaling and dragging
        _mChart.setDragEnabled(true);
        _mChart.setScaleEnabled(true);
        _mChart.setPinchZoom(true);

        DateTime today = _startDate == null ? DateTime.now() : new DateTime(_startDate);

        GraphPopupView mv = new GraphPopupView(this, R.layout.graph_popup, _isMain ? _scale : BusinessConstants.AUX_DATE_SCALE, today);
        _mChart.setMarkerView(mv);

        XAxis xAxis = _mChart.getXAxis();
        xAxis.enableGridDashedLine(10f, 10f, 0f);
        xAxis.setValueFormatter(new CustomXAxisValueFormatter(_isMain ? _scale : BusinessConstants.AUX_DATE_SCALE, today));

        _mChart.getAxisLeft().setEnabled(false);
        _mChart.getAxisRight().setEnabled(true);

        // addPerson data
        setRealData();

        _mChart.animateX(2500);

        // get the legend (only possible after setting data)
        Legend l = _mChart.getLegend();

        l.setForm(Legend.LegendForm.LINE);
        _mChart.invalidate();
    }

    private void setRealData() {
        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        List<IGraph> tempList;

        if (_isMain) {
            tempList = _graphs.subList(0, _graphs.size() - 3);
        } else {
            tempList = _graphs.subList(_graphs.size() - 3, _graphs.size());
        }

        for (int i = 0; i < tempList.size(); ++i) {
            ArrayList<Entry> values = new ArrayList<>();
            IGraph g = tempList.get(i);
            ArrayList<Point> points = g.getPoints();

            if (_isMain) {
                for (int j = 0; j < points.size(); ++j) {
                    values.add(new Entry(points.get(j).GetX(), points.get(j).GetY()));
                }
            } else {
                for (int j = 0; j < points.size(); j += 5) {
                    values.add(new Entry(points.get(j).GetX(), points.get(j).GetY()));
                }
            }

            // create a dataset and give it a type
            LineDataSet set = new LineDataSet(values, g.getName());

            // set the line to be drawn like this "- - - - - -"
            set.enableDashedLine(10f, 5f, 0f);
            set.enableDashedHighlightLine(10f, 5f, 0f);
            set.setColor(g.getColor());
            set.setCircleColor(g.getColor());
            set.setLineWidth(1f);
            set.setCircleRadius(3f);
            set.setDrawCircleHole(false);
            set.setValueTextSize(9f);
            set.setDrawFilled(false);

            dataSets.add(set);
        }

        LineData data = new LineData(dataSets);
        _mChart.setData(data);
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {

    }

    @Override
    public void onNothingSelected() {

    }

    @Override
    public void onChartGestureStart(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {

    }

    @Override
    public void onChartGestureEnd(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {
        if (lastPerformedGesture != ChartTouchListener.ChartGesture.SINGLE_TAP) {
            _mChart.highlightValues(null); // or highlightTouch(null) for callback to onNothingSelected(...)
        }
    }

    @Override
    public void onChartLongPressed(MotionEvent me) {

    }

    @Override
    public void onChartDoubleTapped(MotionEvent me) {

    }

    @Override
    public void onChartSingleTapped(MotionEvent me) {

    }

    @Override
    public void onChartFling(MotionEvent me1, MotionEvent me2, float velocityX, float velocityY) {

    }

    @Override
    public void onChartScale(MotionEvent me, float scaleX, float scaleY) {

    }

    @Override
    public void onChartTranslate(MotionEvent me, float dX, float dY) {

    }
}
