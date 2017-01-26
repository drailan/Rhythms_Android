package com.rr.rhythms.Activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.ActionBar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.ListView

import com.rr.rhythms.Adapters.PeopleListAdapter
import com.rr.rhythms.Business.BusinessConstants
import com.rr.rhythms.Entities.People
import com.rr.rhythms.Entities.Person
import com.rr.rhythms.Fragments.AddPersonFragment
import com.rr.rhythms.Fragments.DatePickerFragment
import com.rr.rhythms.Interfaces.IActivityDismissable
import com.rr.rhythms.Interfaces.IDateSettable
import com.rr.rhythms.Interfaces.IPerson
import com.rr.rhythms.R
import com.rr.rhythms.Services.SettingsService

import org.joda.time.DateTime

import java.util.ArrayList
import java.util.Calendar

class ManagePeopleActivity : AppCompatActivity(), IDateSettable, IActivityDismissable {

    private var _addPersonFragment: AddPersonFragment? = null
    private var _adapter: PeopleListAdapter? = null
    private var _people: ArrayList<IPerson>? = null

    // used when editing
    private var _person: IPerson? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_people)

        val myChildToolbar = findViewById(R.id.my_toolbar) as Toolbar
        setSupportActionBar(myChildToolbar)

        val ab = supportActionBar

        if (ab != null) {
            supportActionBar!!.title = "Manage people"
            ab.setDisplayHomeAsUpEnabled(true)
        }
    }

    val firstBirthDate: DateTime?
        get() {
            if (_person is Person) {
                return (_person as Person).birthDate
            } else if (_person is People) {
                return (_person as People).getAt(0).birthDate
            }

            return null
        }

    val secondBirthDate: DateTime?
        get() {
            if (_person is People) {
                return (_person as People).getAt(1).birthDate
            }

            return null
        }

    override fun finishWithResult(result: Intent) {
        setResult(Activity.RESULT_OK, result)
        finish()
    }

    override fun finishWithoutResult() {
        finish()
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)

        val _list = findViewById(R.id.peopleListView) as ListView

        _list.onItemClickListener = AdapterView.OnItemClickListener { adapterView, view, i, l ->
            val p = adapterView.adapter.getItem(i) as IPerson
            val result = Intent()
            result.putExtra(BusinessConstants.MANAGE_PEOPLE_SELECTED_PERSON, p)

            setResult(Activity.RESULT_OK, result)
            finish()
        }

        val inflater = this.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val footerView = inflater.inflate(R.layout.add_person_list_view_item, null, false)

        _list.addFooterView(footerView)

        loadPeople()

        if (_people == null) {
            _people = ArrayList<IPerson>()

            _people!!.add(Person("Denis", DateTime(1988, 9, 22, 0, 0)))
        }

        _adapter = PeopleListAdapter(this, _people!!, _list)
        _adapter!!.setCallback(this)

        if (_addPersonFragment != null) {
            _addPersonFragment!!.setAdapter(_adapter!!)
        }

        footerView.setOnClickListener {
            if (_addPersonFragment == null) {
                _addPersonFragment = AddPersonFragment()
                _addPersonFragment!!.setAdapter(_adapter!!)

                fragmentManager
                        .beginTransaction()
                        .add(R.id.fragmentContainer, _addPersonFragment, "addPersonF")
                        .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
                        .commit()
            } else if (!_addPersonFragment!!.isVisible) {
                fragmentManager
                        .beginTransaction()
                        .show(_addPersonFragment)
                        .commit()
            }

            _addPersonFragment!!.resetUi()
            _addPersonFragment!!.setPerson(null)
        }

        _list.adapter = _adapter
    }

    private fun loadPeople() {
        _people = ArrayList<IPerson>()

        val o = SettingsService.LoadObject(this, BusinessConstants.PREFS_PEOPLE_KEY)

        if (o != null) {
            _people = o as ArrayList<IPerson>?
        }
    }

    override fun onStop() {
        super.onStop()
    }

    fun showDatePickerDialog(v: View) {
        val dialog = DatePickerFragment()

        when (v.id) {
            R.id.firstDatePicker -> dialog.isMain = true
            R.id.secondDatePicker -> dialog.isMain = false
            else -> {
            }
        }

        dialog.show(supportFragmentManager, "datePickerM")
    }

    fun editPerson(p: IPerson) {
        _person = p

        if (_addPersonFragment == null) {
            _addPersonFragment = AddPersonFragment()

            _addPersonFragment!!.setPerson(p)
            _addPersonFragment!!.setAdapter(_adapter!!)

            fragmentManager
                    .beginTransaction()
                    .add(R.id.fragmentContainer, _addPersonFragment, "addPersonF")
                    .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
                    .commit()
        } else if (!_addPersonFragment!!.isVisible) {
            _addPersonFragment!!.setPerson(p)

            fragmentManager
                    .beginTransaction()
                    .show(_addPersonFragment)
                    .commit()
        }

        _addPersonFragment!!.keepUi()
    }

    override fun setFirstBirthDate(cal: Calendar) {
        if (_addPersonFragment != null) {
            _addPersonFragment!!.setFirstBirthDate(cal)
        }
    }

    override fun setSecondBirthDate(cal: Calendar) {
        if (_addPersonFragment != null) {
            _addPersonFragment!!.setSecondBirthDate(cal)
        }
    }

    fun dismissFragment() {
        fragmentManager
                .beginTransaction()
                .hide(_addPersonFragment)
                .commit()
    }

    public override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putSerializable("people", _people)
    }

    public override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)

        _people = savedInstanceState.getSerializable("people") as ArrayList<IPerson>
        _addPersonFragment = fragmentManager.findFragmentById(R.id.fragmentContainer) as AddPersonFragment
    }
}
