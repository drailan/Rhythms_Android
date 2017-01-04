package com.rr.rhythms.Fragments;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;

import com.rr.rhythms.Activities.MainActivity;
import com.rr.rhythms.Activities.ManagePeopleActivity;
import com.rr.rhythms.Interfaces.IActionBarAware;
import com.rr.rhythms.Interfaces.IDateSettable;

import org.joda.time.DateTime;

import java.util.Calendar;
import java.util.Locale;

public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    public boolean isMain;
    public boolean isFromToolbar;

    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Activity act = getActivity();
        Calendar savedCalendar = null;

        if (act instanceof MainActivity) {
            MainActivity activity = (MainActivity) getActivity();
            if (!isFromToolbar) {
                savedCalendar = isMain ? activity.getFirstBirthDate() : activity.getSecondBirthDate();
            } else {
                savedCalendar = activity.getStartDate();
            }
        } else if (act instanceof ManagePeopleActivity) {
            ManagePeopleActivity activity = (ManagePeopleActivity) getActivity();
            DateTime bdate = (isMain ? activity.getFirstBirthDate() : activity.getSecondBirthDate());

            if (bdate != null) {
                savedCalendar = bdate.toCalendar(Locale.getDefault());
            }
        }

        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        if (savedCalendar != null) {
            year = savedCalendar.get(Calendar.YEAR);
            month = savedCalendar.get(Calendar.MONTH);
            day = savedCalendar.get(Calendar.DAY_OF_MONTH);
        }

        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    @Override
    public void onDateSet(android.widget.DatePicker datePicker, int year, int month, int day) {
        Calendar cal = Calendar.getInstance();
        cal.set(year, month, day);

        Activity act = getActivity();

        if (act instanceof IDateSettable) {
            IDateSettable activity = (IDateSettable) getActivity();

            if (!isFromToolbar) {
                if (isMain) {
                    activity.setFirstBirthDate(cal);
                } else {
                    activity.setSecondBirthDate(cal);
                }
            }
        }

        if (act instanceof IActionBarAware) {
            IActionBarAware activity = (IActionBarAware) getActivity();

            if (isFromToolbar) {
                activity.setDate(cal);
            }
        }
    }
}
