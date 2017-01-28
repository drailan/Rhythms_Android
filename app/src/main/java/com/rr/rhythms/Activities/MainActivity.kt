package com.rr.rhythms.Activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.GridView
import android.widget.RadioGroup

import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.github.mikephil.charting.listener.ChartTouchListener
import com.github.mikephil.charting.listener.OnChartGestureListener
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.rr.rhythms.Adapters.GraphStateAdapter
import com.rr.rhythms.Business.BusinessConstants
import com.rr.rhythms.Business.Graphs.Graph24
import com.rr.rhythms.Business.Graphs.Graph276
import com.rr.rhythms.Business.Graphs.Graph28
import com.rr.rhythms.Business.Graphs.Graph33
import com.rr.rhythms.Business.Graphs.Graph40
import com.rr.rhythms.Business.Graphs.Graph56
import com.rr.rhythms.Business.Graphs.Graph92
import com.rr.rhythms.Entities.GraphInnerState
import com.rr.rhythms.Entities.GraphState
import com.rr.rhythms.Entities.People
import com.rr.rhythms.Entities.Person
import com.rr.rhythms.Fragments.DatePickerFragment
import com.rr.rhythms.Helpers.CustomXAxisValueFormatter
import com.rr.rhythms.Helpers.GraphPopupView
import com.rr.rhythms.Interfaces.IActionBarAware
import com.rr.rhythms.Interfaces.IDateSettable
import com.rr.rhythms.Interfaces.IGraph
import com.rr.rhythms.Interfaces.IPerson
import com.rr.rhythms.R
import kotlinx.android.synthetic.main.graph_state_cell.*

import net.hockeyapp.android.CrashManager
import net.hockeyapp.android.UpdateManager

import org.joda.time.DateTime
import org.joda.time.Days

import java.text.SimpleDateFormat
import java.util.ArrayList
import java.util.Calendar
import java.util.Locale

class MainActivity : AppCompatActivity(), OnChartValueSelectedListener, OnChartGestureListener, IDateSettable, IActionBarAware {

    private var _firstBirthDate: Calendar? = null
    private var _secondBirthDate: Calendar? = null
    var startDate: Calendar? = null
        private set
    private var _mChart: LineChart? = null
    private var _scale = 30
    private var _isMain = true
    private var _graphs: ArrayList<IGraph>? = null
    private var _menu: Menu? = null

    fun getFirstBirthDate(): Calendar? {
        return _firstBirthDate
    }

    fun getSecondBirthDate(): Calendar? {
        return _secondBirthDate
    }

    override fun setFirstBirthDate(cal: Calendar) {
        _firstBirthDate = cal
        val b = findViewById(R.id.mainDatePicker) as Button

        val format1 = SimpleDateFormat.getDateInstance()
        b.text = format1.format(cal.time)

        buildChartAndState()
    }

    override fun setSecondBirthDate(cal: Calendar) {
        _secondBirthDate = cal
        val b = findViewById(R.id.auxDatePicker) as Button

        val format1 = SimpleDateFormat.getDateInstance()
        b.text = format1.format(cal.time)
    }

    override fun setDate(cal: Calendar) {
        startDate = cal

        val format1 = SimpleDateFormat.getDateInstance()

        val m = _menu!!.findItem(R.id.pick_date)
        if (m != null) {
            m.title = format1.format(startDate!!.time)
        }

        buildChartAndState()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        checkForUpdates()

        val myToolbar = findViewById(R.id.my_toolbar) as Toolbar
        setSupportActionBar(myToolbar)

        supportActionBar!!.title = "Home"
        initializeGraphs()
        initRadioGroups()
        initButtons()
    }

    private fun initializeGraphs() {
        _graphs = ArrayList<IGraph>()
        _graphs!!.add(Graph24())
        _graphs!!.add(Graph28())
        _graphs!!.add(Graph33())
        _graphs!!.add(Graph40())
        _graphs!!.add(Graph56())
        _graphs!!.add(Graph92())
        _graphs!!.add(Graph276())
    }

    private fun initButtons() {
        val b = findViewById(R.id.mainDatePicker) as Button
        val b1 = findViewById(R.id.auxDatePicker) as Button

        b.setOnClickListener { view -> showDatePickerDialog(view) }

        b1.setOnClickListener { view -> showDatePickerDialog(view) }
    }

    private fun initRadioGroups() {
        val rg = findViewById(R.id.scaleRadioGroup) as RadioGroup

        rg.setOnCheckedChangeListener { radioGroup, index ->
            when (index) {
                R.id.scale30 -> _scale = 30
                R.id.scale60 -> _scale = 60
                R.id.scale90 -> _scale = 90
                else -> {
                }
            }

            buildChartAndState()
        }

        val rg1 = findViewById(R.id.graphRadioGroup) as RadioGroup

        rg1.setOnCheckedChangeListener { radioGroup, index ->
            when (index) {
                R.id.graphMain -> _isMain = true
                R.id.graphAux -> _isMain = false
                else -> {
                }
            }

            buildChartAndState()
        }
    }

    private fun buildChartAndState() {
        if (_firstBirthDate != null) {
            for (i in _graphs!!.indices) {
                _graphs!![i].generateGraph(getTotalDays(_firstBirthDate!!), if (_isMain) _scale else BusinessConstants.AUX_DATE_SCALE)
            }

            DrawChart()
            //BuildGraphState()
        }
    }

    private fun BuildGraphState() {
//        var grid = (GridView) findViewById(R.id.graph_state_grid);
//        val test = graph_state_grid;
//
//        if (grid != null) {
//
//            val graphStates = ArrayList<GraphState>()
//
//            for (graph in _graphs!!.subList(0, _graphs!!.size - 3)) {
//                val currentStates = graph.getCurrentStates(getTotalDays(_firstBirthDate!!))
//                graphStates.add(GraphState(graph, currentStates))
//            }
//
//            val _adapter = GraphStateAdapter(this, graphStates)
//
//            grid.setNumColumns(graphStates[0].states.size)
//            grid.setAdapter(_adapter);
//
//            setDynamicWidth(grid);
//        }
    }

    private fun setDynamicWidth(gridView: GridView) {
        val gridViewAdapter = gridView.adapter ?: return
        var totalWidth: Int
        val items = gridViewAdapter.count
        val listItem = gridViewAdapter.getView(0, null, gridView)
        listItem.measure(0, 0)
        totalWidth = listItem.measuredWidth
        totalWidth = totalWidth * items
        val params = gridView.layoutParams
        params.width = totalWidth
        gridView.layoutParams = params
    }

    private fun getTotalDays(birthDate: Calendar): Int {
        val today = if (startDate == null) DateTime.now() else DateTime(startDate)
        val birthday = DateTime(birthDate.time)

        val days = Days.daysBetween(birthday, today)

        return days.days
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        _menu = menu
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_settings -> {
                val settingsIntent = Intent(this, SettingsActivity::class.java)
                startActivity(settingsIntent)
                return true
            }
            R.id.action_add -> {
                val managePeopleIntent = Intent(this, ManagePeopleActivity::class.java)
                startActivityForResult(managePeopleIntent, BusinessConstants.MANAGE_PEOPLE_ACTIIVY_REQUEST_CODE)
                return true
            }
            R.id.pick_date -> {
                showDatePickerDialog()
                return true
            }
            R.id.action_today -> {
                setDate(DateTime.now().toCalendar(Locale.getDefault()))
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onResume() {
        super.onResume()
        checkForCrashes()
    }

    public override fun onPause() {
        super.onPause()
        unregisterManagers()
    }

    public override fun onDestroy() {
        super.onDestroy()
        unregisterManagers()
    }

    public override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        if (_firstBirthDate != null) {
            outState.putLong("mainDate", _firstBirthDate!!.timeInMillis)
        }

        if (_secondBirthDate != null) {
            outState.putLong("auxDate", _secondBirthDate!!.timeInMillis)
        }
    }

    public override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        restoreUI(savedInstanceState)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        if (requestCode == BusinessConstants.MANAGE_PEOPLE_ACTIIVY_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                val selectedPerson = data.extras.getSerializable(BusinessConstants.MANAGE_PEOPLE_SELECTED_PERSON) as IPerson

                if (selectedPerson is Person) {
                    _firstBirthDate = selectedPerson.birthDate.toCalendar(Locale.getDefault())
                } else {
                    _firstBirthDate = (selectedPerson as People).getAt(0).birthDate.toCalendar(Locale.getDefault())
                    _secondBirthDate = selectedPerson.getAt(1).birthDate.toCalendar(Locale.getDefault())
                }

                setButtonText()
                buildChartAndState()
            }
        } else if (requestCode == BusinessConstants.ACTION_BAR_DATE_PICKER_REQUEST_CODE) {

        }
    }

    private fun restoreUI(savedState: Bundle) {
        //region Date picker buttons
        val t1 = savedState.getLong("mainDate", 0)
        val t2 = savedState.getLong("auxDate", 0)

        val format1 = SimpleDateFormat.getDateInstance()
        var b: Button

        if (t1 != 0.toLong()) {
            _firstBirthDate = Calendar.getInstance()
            _firstBirthDate!!.timeInMillis = t1

            b = findViewById(R.id.mainDatePicker) as Button
            b.text = format1.format(_firstBirthDate!!.time)
        }

        if (t2 != 0.toLong()) {
            _secondBirthDate = Calendar.getInstance()
            _secondBirthDate!!.timeInMillis = t2

            b = findViewById(R.id.auxDatePicker) as Button
            b.text = format1.format(_secondBirthDate!!.time)
        }

        //endregion

        buildChartAndState()
    }

    private fun setButtonText() {
        val format1 = SimpleDateFormat.getDateInstance()

        var b: Button

        if (_firstBirthDate != null) {
            b = findViewById(R.id.mainDatePicker) as Button
            b.text = format1.format(_firstBirthDate!!.time)
        }

        if (_secondBirthDate != null) {
            b = findViewById(R.id.auxDatePicker) as Button
            b.text = format1.format(_secondBirthDate!!.time)
        }
    }

    private fun checkForCrashes() {
        CrashManager.register(this)
    }

    private fun checkForUpdates() {
        // Remove this for store builds!
        UpdateManager.register(this)
    }

    private fun unregisterManagers() {
        UpdateManager.unregister()
    }

    fun showDatePickerDialog(v: View) {
        val dialog = DatePickerFragment()

        when (v.id) {
            R.id.mainDatePicker -> dialog.isMain = true
            R.id.auxDatePicker -> dialog.isMain = false
            else -> {
            }
        }

        dialog.show(supportFragmentManager, "datePicker")
    }

    fun showDatePickerDialog() {
        val dialog = DatePickerFragment()
        dialog.isFromToolbar = true
        dialog.show(supportFragmentManager, "datePicker")
    }

    private fun DrawChart() {
        _mChart = findViewById(R.id.chart) as LineChart
        _mChart!!.onChartGestureListener = this
        _mChart!!.setOnChartValueSelectedListener(this)
        _mChart!!.setDrawGridBackground(false)

        // no description text
        _mChart!!.setDescription("")
        _mChart!!.setNoDataTextDescription("You need to provide data for the chart.")

        // enable touch gestures
        _mChart!!.setTouchEnabled(true)

        // enable scaling and dragging
        _mChart!!.isDragEnabled = true
        _mChart!!.setScaleEnabled(true)
        _mChart!!.setPinchZoom(true)

        val today = if (startDate == null) DateTime.now() else DateTime(startDate)

        val mv = GraphPopupView(this, R.layout.graph_popup, if (_isMain) _scale else BusinessConstants.AUX_DATE_SCALE, today)
        _mChart!!.markerView = mv

        val xAxis = _mChart!!.xAxis
        xAxis.enableGridDashedLine(10f, 10f, 0f)
        xAxis.valueFormatter = CustomXAxisValueFormatter(if (_isMain) _scale else BusinessConstants.AUX_DATE_SCALE, today)

        _mChart!!.axisLeft.isEnabled = false
        _mChart!!.axisRight.isEnabled = true

        // addPerson data
        setRealData()

        _mChart!!.animateX(2500)

        // get the legend (only possible after setting data)
        val l = _mChart!!.legend

        l.form = Legend.LegendForm.LINE
        _mChart!!.invalidate()
    }

    private fun setRealData() {
        val dataSets = ArrayList<ILineDataSet>()
        val tempList: List<IGraph>

        if (_isMain) {
            tempList = _graphs!!.subList(0, _graphs!!.size - 3)
        } else {
            tempList = _graphs!!.subList(_graphs!!.size - 3, _graphs!!.size)
        }

        for (i in tempList.indices) {
            val values = ArrayList<Entry>()
            val g = tempList[i]
            val points = g.points

            if (_isMain) {
                for (j in points.indices) {
                    values.add(Entry(points[j].GetX(), points[j].GetY()))
                }
            } else {
                var j = 0
                while (j < points.size) {
                    values.add(Entry(points[j].GetX(), points[j].GetY()))
                    j += 5
                }
            }

            // create a dataset and give it a type
            val set = LineDataSet(values, g.name)

            // set the line to be drawn like this "- - - - - -"
            set.enableDashedLine(10f, 5f, 0f)
            set.enableDashedHighlightLine(10f, 5f, 0f)
            set.color = g.color
            set.setCircleColor(g.color)
            set.lineWidth = 1f
            set.circleRadius = 3f
            set.setDrawCircleHole(false)
            set.valueTextSize = 9f
            set.setDrawFilled(false)

            dataSets.add(set)
        }

        val data = LineData(dataSets)
        _mChart!!.data = data
    }

    override fun onValueSelected(e: Entry, h: Highlight) {

    }

    override fun onNothingSelected() {

    }

    override fun onChartGestureStart(me: MotionEvent, lastPerformedGesture: ChartTouchListener.ChartGesture) {

    }

    override fun onChartGestureEnd(me: MotionEvent, lastPerformedGesture: ChartTouchListener.ChartGesture) {
        if (lastPerformedGesture != ChartTouchListener.ChartGesture.SINGLE_TAP) {
            _mChart!!.highlightValues(null) // or highlightTouch(null) for callback to onNothingSelected(...)
        }
    }

    override fun onChartLongPressed(me: MotionEvent) {

    }

    override fun onChartDoubleTapped(me: MotionEvent) {

    }

    override fun onChartSingleTapped(me: MotionEvent) {

    }

    override fun onChartFling(me1: MotionEvent, me2: MotionEvent, velocityX: Float, velocityY: Float) {

    }

    override fun onChartScale(me: MotionEvent, scaleX: Float, scaleY: Float) {

    }

    override fun onChartTranslate(me: MotionEvent, dX: Float, dY: Float) {

    }
}
