package com.rr.rhythms.Fragments;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.rr.rhythms.Activities.ManagePeopleActivity;
import com.rr.rhythms.Adapters.PeopleListAdapter;
import com.rr.rhythms.Business.BusinessConstants;
import com.rr.rhythms.Entities.People;
import com.rr.rhythms.Entities.Person;
import com.rr.rhythms.Interfaces.IDateSettable;
import com.rr.rhythms.Interfaces.IPerson;
import com.rr.rhythms.Interfaces.IPersonSettable;
import com.rr.rhythms.R;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by Huruk on 7/8/2016.
 */
public class AddPersonFragment extends Fragment implements IDateSettable, IPersonSettable {
    private PeopleListAdapter _parentAdapter;
    private View _parentView;

    private Calendar _firstBirthDate;
    private Calendar _secondBirthDate;
    private boolean _isVisible = true;

    // used when editing
    private IPerson _person;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            _isVisible = savedInstanceState.getBoolean("_isVisible");
        }

        if (!_isVisible) {
            getFragmentManager()
                    .beginTransaction()
                    .hide(this)
                    .commit();
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        _isVisible = !hidden;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.add_person_dialog, container, false);

        _parentView = v;

        initUi(_parentView);

        if (savedInstanceState != null) {
            restoreInstance(v, savedInstanceState);
        }

        attachOnClickHandlers(v);
        return v;
    }

    private void attachOnClickHandlers(View v) {
        final Button cancelButton = (Button) v.findViewById(R.id.cancelAddPerson);
        final Button savePerson = (Button) v.findViewById(R.id.savePerson);
        final EditText firstName = (EditText) v.findViewById(R.id.firstPerson);
        final EditText secondName = (EditText) v.findViewById(R.id.secondPerson);
        final Button firstDatepicker = (Button) v.findViewById(R.id.firstDatePicker);
        final Button secondDatepicker = (Button) v.findViewById(R.id.secondDatePicker);

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firstName.setText("");
                secondName.setText("");
                firstDatepicker.setText(R.string.pickDate);
                secondDatepicker.setText(R.string.pickDate);

                ((ManagePeopleActivity) getActivity()).dismissFragment();
            }
        });

        savePerson.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean first = !firstName.getText().toString().isEmpty();
                boolean second = !secondName.getText().toString().isEmpty();

                if (first && second) {
                    if (_person != null) {
                        _parentAdapter.editPerson(_person, new People(new Person[]{
                                new Person(firstName.getText().toString(), new DateTime(_firstBirthDate)),
                                new Person(secondName.getText().toString(), new DateTime(_secondBirthDate))
                        }));
                        _person = null;
                    } else {
                        _parentAdapter.addPerson(new People(new Person[]{
                                new Person(firstName.getText().toString(), new DateTime(_firstBirthDate)),
                                new Person(secondName.getText().toString(), new DateTime(_secondBirthDate))
                        }));
                    }
                } else if (first || second) {
                    if (first) {
                        if (_person != null) {
                            _parentAdapter.editPerson(_person, new Person(firstName.getText().toString(), new DateTime(_firstBirthDate)));
                            _person = null;
                        } else {
                            _parentAdapter.addPerson(new Person(firstName.getText().toString(), new DateTime(_firstBirthDate)));
                        }
                    } else {
                        if (_person != null) {
                            _parentAdapter.editPerson(_person, new Person(secondName.getText().toString(), new DateTime(_secondBirthDate)));
                        } else {
                            _parentAdapter.addPerson(new Person(secondName.getText().toString(), new DateTime(_secondBirthDate)));
                        }
                    }
                }

                firstName.setText("");
                firstName.setText("");
                firstDatepicker.setText("");
                secondDatepicker.setText("");

                ((ManagePeopleActivity) getActivity()).dismissFragment();
            }
        });
    }

    private void restoreInstance(View v, Bundle savedInstanceState) {
        final EditText firstName = (EditText) v.findViewById(R.id.firstPerson);
        final EditText secondName = (EditText) v.findViewById(R.id.secondPerson);
        final Button firstDatepicker = (Button) v.findViewById(R.id.firstDatePicker);
        final Button secondDatepicker = (Button) v.findViewById(R.id.secondDatePicker);

        firstName.setText(savedInstanceState.getString("firstName"));
        secondName.setText(savedInstanceState.getString("secondName"));

        long t1 = savedInstanceState.getLong("mainDate", 0);
        long t2 = savedInstanceState.getLong("auxDate", 0);

        DateFormat format1 = SimpleDateFormat.getDateInstance();

        if (t1 != 0) {
            _firstBirthDate = Calendar.getInstance();
            _firstBirthDate.setTimeInMillis(t1);

            firstDatepicker.setText(format1.format(_firstBirthDate.getTime()));
        }

        if (t2 != 0) {
            _secondBirthDate = Calendar.getInstance();
            _secondBirthDate.setTimeInMillis(t2);

            secondDatepicker.setText(format1.format(_secondBirthDate.getTime()));
        }
    }

    private void initUi(View v) {
        final EditText firstName = (EditText) v.findViewById(R.id.firstPerson);
        final EditText secondName = (EditText) v.findViewById(R.id.secondPerson);
        final Button firstDatepicker = (Button) v.findViewById(R.id.firstDatePicker);
        final Button secondDatepicker = (Button) v.findViewById(R.id.secondDatePicker);

        if (_person != null) {
            DateTimeFormatter fmt = DateTimeFormat.forPattern(BusinessConstants.BDAY_FORMAT);

            if (_person instanceof Person) {
                String name = ((Person) _person).getName();
                DateTime date = ((Person) _person).getBirthDate();

                firstName.setText(name);
                firstDatepicker.setText(fmt.print(date));

                secondDatepicker.setText(R.string.pickDate);

            } else if (_person instanceof People) {
                Person[] people = ((People) _person).getPeople();

                String name1 = people[0].getName();
                String name2 = people[1].getName();

                DateTime date1 = people[0].getBirthDate();
                DateTime date2 = people[1].getBirthDate();

                firstName.setText(name1);
                firstDatepicker.setText(fmt.print(date1));

                secondName.setText(name2);
                secondDatepicker.setText(fmt.print(date2));
            }
        }
    }

    public void keepUi() {
        if (_parentView != null) {
            initUi(_parentView);
        }
    }

    public void resetUi() {
        if (_parentView != null) {
            final EditText firstName = (EditText) _parentView.findViewById(R.id.firstPerson);
            final EditText secondName = (EditText) _parentView.findViewById(R.id.secondPerson);
            final Button firstDatepicker = (Button) _parentView.findViewById(R.id.firstDatePicker);
            final Button secondDatepicker = (Button) _parentView.findViewById(R.id.secondDatePicker);

            firstName.setText("");
            secondName.setText("");
            firstDatepicker.setText(R.string.pickDate);
            secondDatepicker.setText(R.string.pickDate);
        }
    }

    @Override
    public void setFirstBirthDate(Calendar cal) {
        _firstBirthDate = cal;

        Button b = (Button) getView().findViewById(R.id.firstDatePicker);

        DateFormat format1 = SimpleDateFormat.getDateInstance();
        b.setText(format1.format(cal.getTime()));
    }

    @Override
    public void setSecondBirthDate(Calendar cal) {
        _secondBirthDate = cal;

        Button b = (Button) getView().findViewById(R.id.secondDatePicker);

        DateFormat format1 = SimpleDateFormat.getDateInstance();
        b.setText(format1.format(cal.getTime()));
    }

    public void setAdapter(PeopleListAdapter adapter) {
        _parentAdapter = adapter;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (outState != null) {
            outState.putBoolean("_isVisible", _isVisible);
        }

        if (_firstBirthDate != null) {
            assert outState != null;
            outState.putLong("mainDate", _firstBirthDate.getTimeInMillis());
        }

        if (_secondBirthDate != null) {
            assert outState != null;
            outState.putLong("auxDate", _secondBirthDate.getTimeInMillis());
        }

        EditText firstName = (EditText) getView().findViewById(R.id.firstPerson);
        EditText secondName = (EditText) getView().findViewById(R.id.secondPerson);

        if (!firstName.getText().toString().isEmpty()) {
            assert outState != null;
            outState.putString("firstName", firstName.getText().toString());
        }

        if (!secondName.getText().toString().isEmpty()) {
            assert outState != null;
            outState.putString("secondName", secondName.getText().toString());
        }
    }

    @Override
    public void setPerson(IPerson p) {
        _person = p;
    }
}
