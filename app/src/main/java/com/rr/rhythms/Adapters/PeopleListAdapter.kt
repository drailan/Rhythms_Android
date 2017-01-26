package com.rr.rhythms.Adapters

import android.content.Context
import android.content.Intent
import android.renderscript.ScriptGroup
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.BaseAdapter
import android.widget.ImageButton
import android.widget.ListView
import android.widget.TextView

import com.rr.rhythms.Activities.ManagePeopleActivity
import com.rr.rhythms.Business.BusinessConstants
import com.rr.rhythms.Entities.People
import com.rr.rhythms.Entities.Person
import com.rr.rhythms.Interfaces.IActivityDismissable
import com.rr.rhythms.Interfaces.IPerson
import com.rr.rhythms.R
import com.rr.rhythms.Services.SettingsService

import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter

import java.util.ArrayList

/**
 * Created by Huruk on 7/8/2016.
 */
class PeopleListAdapter(private val _context: Context, private val _people: ArrayList<IPerson>, private val _list: ListView) : BaseAdapter() {

    private var _callback: IActivityDismissable? = null

    fun setCallback(callback: IActivityDismissable) {
        _callback = callback
    }

    override fun getCount(): Int {
        return _people.size
    }

    override fun getItem(i: Int): IPerson {
        return _people[i]
    }

    override fun getItemId(i: Int): Long {
        return 0
    }

    fun addPerson(`object`: IPerson) {
        _people.add(`object`)
        notifyDataSetChanged()
    }

    fun editPerson(p: IPerson, newPerson: IPerson) {
        val index = _people.indexOf(p)

        if (index != -1) {
            _people[index] = newPerson
            notifyDataSetChanged()
        }
    }

    private fun editPerson(p: IPerson) {
        (_context as ManagePeopleActivity).editPerson(p)
    }

    fun deletePerson(person: IPerson) {
        _people.remove(person)
        notifyDataSetChanged()
    }

    fun deletePerson(position: Int) {
        _people.removeAt(position)
        notifyDataSetChanged()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val inflater = _context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        var view: View? = null

        val type = getItemViewType(position)

        val fmt = DateTimeFormat.forPattern(BusinessConstants.BDAY_FORMAT)

        if (type == 0) {
            if (convertView == null) {
                view = inflater.inflate(R.layout.person_list_view_item, parent, false)
            } else {
                view = convertView
            }

            val t1 = view!!.findViewById(R.id.firstLine) as TextView
            val t2 = view.findViewById(R.id.secondLine) as TextView

            t1.text = (_people[position] as Person).name
            t2.text = fmt.print((_people[position] as Person).birthDate)

            setButtonHandlers(view)

        } else if (type == 1) {
            if (convertView == null) {
                view = inflater.inflate(R.layout.people_list_view_item, parent, false)
            } else {
                view = convertView
            }

            val t1 = view!!.findViewById(R.id.firstLine) as TextView
            val t2 = view.findViewById(R.id.secondLine) as TextView

            val t3 = view.findViewById(R.id.firstLine2) as TextView
            val t4 = view.findViewById(R.id.secondLine2) as TextView

            val p = _people[position] as People

            t1.text = p.getAt(0).name
            t2.text = fmt.print(p.getAt(0).birthDate)

            t3.text = p.getAt(1).name
            t4.text = fmt.print(p.getAt(1).birthDate)

            setButtonHandlers(view)
        }

        return view!!
    }

    private fun setButtonHandlers(v: View) {

        val view = v.findViewById(R.id.view) as ImageButton
        val edit = v.findViewById(R.id.edit) as ImageButton
        val delete = v.findViewById(R.id.delete) as ImageButton

        setEventListeners(view, edit, delete)
    }

    private fun <T : ImageButton> setEventListeners(vararg elements: T) {
        for (element in elements) {
            element.setOnClickListener { view ->
                val position = _list.getPositionForView(view)

                val p: IPerson

                when (view.id) {
                    R.id.view -> {
                        p = getItem(position)
                        val result = Intent()
                        result.putExtra(BusinessConstants.MANAGE_PEOPLE_SELECTED_PERSON, p)

                        _callback!!.finishWithResult(result)
                    }
                    R.id.edit -> {
                        p = getItem(position)
                        editPerson(p)
                    }
                    R.id.delete -> deletePerson(position)
                    else -> {
                    }
                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        val item = getItem(position)

        if (item is Person) return 0
        if (item is People) return 1

        return 0
    }

    override fun getViewTypeCount(): Int {
        return 2
    }

    override fun notifyDataSetChanged() {
        super.notifyDataSetChanged()

        SettingsService.SaveObject(_context, BusinessConstants.PREFS_PEOPLE_KEY, _people)
    }
}
