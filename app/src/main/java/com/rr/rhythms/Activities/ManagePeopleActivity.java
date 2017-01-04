package com.rr.rhythms.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.rr.rhythms.Adapters.PeopleListAdapter;
import com.rr.rhythms.Business.BusinessConstants;
import com.rr.rhythms.Entities.People;
import com.rr.rhythms.Entities.Person;
import com.rr.rhythms.Fragments.AddPersonFragment;
import com.rr.rhythms.Fragments.DatePickerFragment;
import com.rr.rhythms.Interfaces.IActivityDismissable;
import com.rr.rhythms.Interfaces.IDateSettable;
import com.rr.rhythms.Interfaces.IPerson;
import com.rr.rhythms.R;
import com.rr.rhythms.Services.SettingsService;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Calendar;

public class ManagePeopleActivity extends AppCompatActivity implements IDateSettable, IActivityDismissable {

    private AddPersonFragment _addPersonFragment;
    private PeopleListAdapter _adapter;
    private ArrayList<IPerson> _people;

    // used when editing
    private IPerson _person;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_people);

        Toolbar myChildToolbar =
                (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myChildToolbar);

        ActionBar ab = getSupportActionBar();

        if (ab != null) {
            getSupportActionBar().setTitle("Manage people");
            ab.setDisplayHomeAsUpEnabled(true);
        }
    }

    public DateTime getFirstBirthDate() {
        if (_person instanceof Person) {
            return ((Person) _person).getBirthDate();
        } else if (_person instanceof People) {
            return ((People) _person).getAt(0).getBirthDate();
        }

        return null;
    }

    public DateTime getSecondBirthDate() {
        if (_person instanceof People) {
            return ((People) _person).getAt(1).getBirthDate();
        }

        return null;
    }

    @Override
    public void finishWithResult(Intent result) {
        setResult(RESULT_OK, result);
        finish();
    }

    @Override
    public void finishWithoutResult() {
        finish();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        ListView _list = (ListView) findViewById(R.id.peopleListView);

        _list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                IPerson p = (IPerson) adapterView.getAdapter().getItem(i);
                Intent result = new Intent();
                result.putExtra(BusinessConstants.MANAGE_PEOPLE_SELECTED_PERSON, p);

                setResult(RESULT_OK, result);
                finish();
            }
        });

        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View footerView = inflater.inflate(R.layout.add_person_list_view_item, null, false);

        _list.addFooterView(footerView);

        loadPeople();

        if (_people == null) {
            _people = new ArrayList<>();

            _people.add(new Person("Denis", new DateTime(1988, 9, 22, 0, 0)));
        }

        _adapter = new PeopleListAdapter(this, _people, _list);
        _adapter.setCallback(this);

        if (_addPersonFragment != null) {
            _addPersonFragment.setAdapter(_adapter);
        }

        footerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (_addPersonFragment == null) {
                    _addPersonFragment = new AddPersonFragment();
                    _addPersonFragment.setAdapter(_adapter);

                    getFragmentManager()
                            .beginTransaction()
                            .add(R.id.fragmentContainer, _addPersonFragment, "addPersonF")
                            .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
                            .commit();
                } else if (!_addPersonFragment.isVisible()) {
                    getFragmentManager()
                            .beginTransaction()
                            .show(_addPersonFragment)
                            .commit();
                }

                _addPersonFragment.resetUi();
                _addPersonFragment.setPerson(null);
            }
        });

        _list.setAdapter(_adapter);
    }

    private void loadPeople() {
        _people = new ArrayList<>();

        Object o = SettingsService.LoadObject(this, BusinessConstants.PREFS_PEOPLE_KEY);

        if (o != null) {
            _people = (ArrayList<IPerson>) o;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    public void showDatePickerDialog(View v) {
        DatePickerFragment dialog = new DatePickerFragment();

        switch (v.getId()) {
            case R.id.firstDatePicker:
                dialog.isMain = true;
                break;
            case R.id.secondDatePicker:
                dialog.isMain = false;
                break;
            default:
                break;
        }

        dialog.show(getSupportFragmentManager(), "datePickerM");
    }

    public void editPerson(IPerson p) {
        _person = p;

        if (_addPersonFragment == null) {
            _addPersonFragment = new AddPersonFragment();

            _addPersonFragment.setPerson(p);
            _addPersonFragment.setAdapter(_adapter);

            getFragmentManager()
                    .beginTransaction()
                    .add(R.id.fragmentContainer, _addPersonFragment, "addPersonF")
                    .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
                    .commit();
        } else if (!_addPersonFragment.isVisible()) {
            _addPersonFragment.setPerson(p);

            getFragmentManager()
                    .beginTransaction()
                    .show(_addPersonFragment)
                    .commit();
        }

        _addPersonFragment.keepUi();
    }

    @Override
    public void setFirstBirthDate(Calendar cal) {
        if (_addPersonFragment != null) {
            _addPersonFragment.setFirstBirthDate(cal);
        }
    }

    @Override
    public void setSecondBirthDate(Calendar cal) {
        if (_addPersonFragment != null) {
            _addPersonFragment.setSecondBirthDate(cal);
        }
    }

    public void dismissFragment() {
        getFragmentManager()
                .beginTransaction()
                .hide(_addPersonFragment)
                .commit();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putSerializable("people", _people);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        _people = (ArrayList<IPerson>) savedInstanceState.getSerializable("people");
        _addPersonFragment = (AddPersonFragment) getFragmentManager().findFragmentById(R.id.fragmentContainer);
    }
}
