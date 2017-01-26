package com.rr.rhythms.Fragments

import android.app.Fragment
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText

import com.rr.rhythms.Activities.ManagePeopleActivity
import com.rr.rhythms.Adapters.PeopleListAdapter
import com.rr.rhythms.Business.BusinessConstants
import com.rr.rhythms.Entities.People
import com.rr.rhythms.Entities.Person
import com.rr.rhythms.Interfaces.IDateSettable
import com.rr.rhythms.Interfaces.IPerson
import com.rr.rhythms.Interfaces.IPersonSettable
import com.rr.rhythms.R

import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter

import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Calendar

/**
 * Created by Huruk on 7/8/2016.
 */
class AddPersonFragment : Fragment(), IDateSettable, IPersonSettable {
    private var _parentAdapter: PeopleListAdapter? = null
    private var _parentView: View? = null

    private var _firstBirthDate: Calendar? = null
    private var _secondBirthDate: Calendar? = null
    private var _isVisible = true

    // used when editing
    private var _person: IPerson? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (savedInstanceState != null) {
            _isVisible = savedInstanceState.getBoolean("_isVisible")
        }

        if (!_isVisible) {
            fragmentManager
                    .beginTransaction()
                    .hide(this)
                    .commit()
        }
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        _isVisible = !hidden
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.add_person_dialog, container, false)

        _parentView = v

        initUi(_parentView!!)

        if (savedInstanceState != null) {
            restoreInstance(v, savedInstanceState)
        }

        attachOnClickHandlers(v)
        return v
    }

    private fun attachOnClickHandlers(v: View) {
        val cancelButton = v.findViewById(R.id.cancelAddPerson) as Button
        val savePerson = v.findViewById(R.id.savePerson) as Button
        val firstName = v.findViewById(R.id.firstPerson) as EditText
        val secondName = v.findViewById(R.id.secondPerson) as EditText
        val firstDatepicker = v.findViewById(R.id.firstDatePicker) as Button
        val secondDatepicker = v.findViewById(R.id.secondDatePicker) as Button

        cancelButton.setOnClickListener {
            firstName.setText("")
            secondName.setText("")
            firstDatepicker.setText(R.string.pickDate)
            secondDatepicker.setText(R.string.pickDate)

            (activity as ManagePeopleActivity).dismissFragment()
        }

        savePerson.setOnClickListener {
            val first = !firstName.text.toString().isEmpty()
            val second = !secondName.text.toString().isEmpty()

            if (first && second) {
                if (_person != null) {
                    _parentAdapter!!.editPerson(_person!!, People(arrayOf(Person(firstName.text.toString(), DateTime(_firstBirthDate)), Person(secondName.text.toString(), DateTime(_secondBirthDate)))))
                    _person = null
                } else {
                    _parentAdapter!!.addPerson(People(arrayOf(Person(firstName.text.toString(), DateTime(_firstBirthDate)), Person(secondName.text.toString(), DateTime(_secondBirthDate)))))
                }
            } else if (first || second) {
                if (first) {
                    if (_person != null) {
                        _parentAdapter!!.editPerson(_person!!, Person(firstName.text.toString(), DateTime(_firstBirthDate)))
                        _person = null
                    } else {
                        _parentAdapter!!.addPerson(Person(firstName.text.toString(), DateTime(_firstBirthDate)))
                    }
                } else {
                    if (_person != null) {
                        _parentAdapter!!.editPerson(_person!!, Person(secondName.text.toString(), DateTime(_secondBirthDate)))
                    } else {
                        _parentAdapter!!.addPerson(Person(secondName.text.toString(), DateTime(_secondBirthDate)))
                    }
                }
            }

            firstName.setText("")
            firstName.setText("")
            firstDatepicker.text = ""
            secondDatepicker.text = ""

            (activity as ManagePeopleActivity).dismissFragment()
        }
    }

    private fun restoreInstance(v: View, savedInstanceState: Bundle) {
        val firstName = v.findViewById(R.id.firstPerson) as EditText
        val secondName = v.findViewById(R.id.secondPerson) as EditText
        val firstDatepicker = v.findViewById(R.id.firstDatePicker) as Button
        val secondDatepicker = v.findViewById(R.id.secondDatePicker) as Button

        firstName.setText(savedInstanceState.getString("firstName"))
        secondName.setText(savedInstanceState.getString("secondName"))

        val t1 = savedInstanceState.getLong("mainDate", 0)
        val t2 = savedInstanceState.getLong("auxDate", 0)

        val format1 = SimpleDateFormat.getDateInstance()

        if (t1 != 0.toLong()) {
            _firstBirthDate = Calendar.getInstance()
            _firstBirthDate!!.timeInMillis = t1

            firstDatepicker.text = format1.format(_firstBirthDate!!.time)
        }

        if (t2 != 0.toLong()) {
            _secondBirthDate = Calendar.getInstance()
            _secondBirthDate!!.timeInMillis = t2

            secondDatepicker.text = format1.format(_secondBirthDate!!.time)
        }
    }

    private fun initUi(v: View) {
        val firstName = v.findViewById(R.id.firstPerson) as EditText
        val secondName = v.findViewById(R.id.secondPerson) as EditText
        val firstDatepicker = v.findViewById(R.id.firstDatePicker) as Button
        val secondDatepicker = v.findViewById(R.id.secondDatePicker) as Button

        if (_person != null) {
            val fmt = DateTimeFormat.forPattern(BusinessConstants.BDAY_FORMAT)

            if (_person is Person) {
                val name = (_person as Person).name
                val date = (_person as Person).birthDate

                firstName.setText(name)
                firstDatepicker.text = fmt.print(date)

                secondDatepicker.setText(R.string.pickDate)

            } else if (_person is People) {
                val people = (_person as People).people

                val name1 = people[0].name
                val name2 = people[1].name

                val date1 = people[0].birthDate
                val date2 = people[1].birthDate

                firstName.setText(name1)
                firstDatepicker.text = fmt.print(date1)

                secondName.setText(name2)
                secondDatepicker.text = fmt.print(date2)
            }
        }
    }

    fun keepUi() {
        if (_parentView != null) {
            initUi(_parentView!!)
        }
    }

    fun resetUi() {
        if (_parentView != null) {
            val firstName = _parentView!!.findViewById(R.id.firstPerson) as EditText
            val secondName = _parentView!!.findViewById(R.id.secondPerson) as EditText
            val firstDatepicker = _parentView!!.findViewById(R.id.firstDatePicker) as Button
            val secondDatepicker = _parentView!!.findViewById(R.id.secondDatePicker) as Button

            firstName.setText("")
            secondName.setText("")
            firstDatepicker.setText(R.string.pickDate)
            secondDatepicker.setText(R.string.pickDate)
        }
    }

    override fun setFirstBirthDate(cal: Calendar) {
        _firstBirthDate = cal

        val b = view!!.findViewById(R.id.firstDatePicker) as Button

        val format1 = SimpleDateFormat.getDateInstance()
        b.text = format1.format(cal.time)
    }

    override fun setSecondBirthDate(cal: Calendar) {
        _secondBirthDate = cal

        val b = view!!.findViewById(R.id.secondDatePicker) as Button

        val format1 = SimpleDateFormat.getDateInstance()
        b.text = format1.format(cal.time)
    }

    fun setAdapter(adapter: PeopleListAdapter) {
        _parentAdapter = adapter
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.putBoolean("_isVisible", _isVisible)

        if (_firstBirthDate != null) {
            assert(outState != null)
            outState!!.putLong("mainDate", _firstBirthDate!!.timeInMillis)
        }

        if (_secondBirthDate != null) {
            assert(outState != null)
            outState!!.putLong("auxDate", _secondBirthDate!!.timeInMillis)
        }

        val firstName = view!!.findViewById(R.id.firstPerson) as EditText
        val secondName = view!!.findViewById(R.id.secondPerson) as EditText

        if (!firstName.text.toString().isEmpty()) {
            assert(outState != null)
            outState!!.putString("firstName", firstName.text.toString())
        }

        if (!secondName.text.toString().isEmpty()) {
            assert(outState != null)
            outState!!.putString("secondName", secondName.text.toString())
        }
    }

    override fun setPerson(p: IPerson?) {
        _person = p
    }
}
