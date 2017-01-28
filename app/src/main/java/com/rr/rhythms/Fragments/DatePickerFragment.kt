package com.rr.rhythms.Fragments

import android.app.Activity
import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment

import com.rr.rhythms.Activities.MainActivity
import com.rr.rhythms.Activities.ManagePeopleActivity
import com.rr.rhythms.Interfaces.IActionBarAware
import com.rr.rhythms.Interfaces.IDateSettable

import org.joda.time.DateTime

import java.util.Calendar
import java.util.Locale

class DatePickerFragment : DialogFragment(), DatePickerDialog.OnDateSetListener {

    var isMain: Boolean = false
    var isFromToolbar: Boolean = false

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val act = activity
        var savedCalendar: Calendar? = null

        if (act is MainActivity) {
            val activity = activity as MainActivity
            if (!isFromToolbar) {
                savedCalendar = if (isMain) activity.getFirstBirthDate() else activity.getSecondBirthDate()
            } else {
                savedCalendar = activity.startDate
            }
        } else if (act is ManagePeopleActivity) {
            val activity = activity as ManagePeopleActivity
            val bdate = if (isMain) activity.firstBirthDate else activity.secondBirthDate

            if (bdate != null) {
                savedCalendar = bdate.toCalendar(Locale.getDefault())
            }
        }

        val c = Calendar.getInstance()
        var year = c.get(Calendar.YEAR)
        var month = c.get(Calendar.MONTH)
        var day = c.get(Calendar.DAY_OF_MONTH)

        if (savedCalendar != null) {
            year = savedCalendar.get(Calendar.YEAR)
            month = savedCalendar.get(Calendar.MONTH)
            day = savedCalendar.get(Calendar.DAY_OF_MONTH)
        }

        // Create a new instance of DatePickerDialog and return it
        return DatePickerDialog(activity, this, year, month, day)
    }

    override fun onDateSet(datePicker: android.widget.DatePicker, year: Int, month: Int, day: Int) {
        val cal = Calendar.getInstance()
        cal.set(year, month, day)

        val act = activity

        if (act is IDateSettable) {
            val activity = activity as IDateSettable

            if (!isFromToolbar) {
                if (isMain) {
                    activity.setFirstBirthDate(cal)
                } else {
                    activity.setSecondBirthDate(cal)
                }
            }
        }

        if (act is IActionBarAware) {
            val activity = activity as IActionBarAware

            if (isFromToolbar) {
                activity.setDate(cal)
            }
        }
    }
}
