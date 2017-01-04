package com.rr.rhythms.Adapters;

import android.content.Context;
import android.content.Intent;
import android.renderscript.ScriptGroup;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.rr.rhythms.Activities.ManagePeopleActivity;
import com.rr.rhythms.Business.BusinessConstants;
import com.rr.rhythms.Entities.People;
import com.rr.rhythms.Entities.Person;
import com.rr.rhythms.Interfaces.IActivityDismissable;
import com.rr.rhythms.Interfaces.IPerson;
import com.rr.rhythms.R;
import com.rr.rhythms.Services.SettingsService;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;

/**
 * Created by Huruk on 7/8/2016.
 */
public class PeopleListAdapter extends BaseAdapter {

    private final Context _context;
    private final ArrayList<IPerson> _people;
    private final ListView _list;

    private IActivityDismissable _callback;

    public PeopleListAdapter(Context context, ArrayList<IPerson> people, ListView container) {
        _context = context;
        _people = people;
        _list = container;
    }

    public void setCallback(IActivityDismissable callback) {
        _callback = callback;
    }

    @Override
    public int getCount() {
        return _people.size();
    }

    @Override
    public IPerson getItem(int i) {
        return _people.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    public void addPerson(IPerson object) {
        _people.add(object);
        notifyDataSetChanged();
    }

    public void editPerson(IPerson p, IPerson newPerson) {
        int index = _people.indexOf(p);

        if (index != -1) {
            _people.set(index, newPerson);
            notifyDataSetChanged();
        }
    }

    private void editPerson(IPerson p) {
        ((ManagePeopleActivity)_context).editPerson(p);
    }

    public void deletePerson(IPerson person) {
        _people.remove(person);
        notifyDataSetChanged();
    }

    public void deletePerson(int position) {
        _people.remove(position);
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) _context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = null;

        int type = getItemViewType(position);

        DateTimeFormatter fmt = DateTimeFormat.forPattern(BusinessConstants.BDAY_FORMAT);

        if (type == 0) {
            if (convertView == null) {
                view = inflater.inflate(R.layout.person_list_view_item, parent, false);
            } else {
                view = convertView;
            }

            TextView t1 = (TextView) view.findViewById(R.id.firstLine);
            TextView t2 = (TextView) view.findViewById(R.id.secondLine);

            t1.setText(((Person) _people.get(position)).getName());
            t2.setText(fmt.print(((Person) _people.get(position)).getBirthDate()));

            setButtonHandlers(view);

        } else if (type == 1) {
            if (convertView == null) {
                view = inflater.inflate(R.layout.people_list_view_item, parent, false);
            } else {
                view = convertView;
            }

            TextView t1 = (TextView) view.findViewById(R.id.firstLine);
            TextView t2 = (TextView) view.findViewById(R.id.secondLine);

            TextView t3 = (TextView) view.findViewById(R.id.firstLine2);
            TextView t4 = (TextView) view.findViewById(R.id.secondLine2);

            People p = (People) _people.get(position);

            t1.setText(p.getAt(0).getName());
            t2.setText(fmt.print(p.getAt(0).getBirthDate()));

            t3.setText(p.getAt(1).getName());
            t4.setText(fmt.print(p.getAt(1).getBirthDate()));

            setButtonHandlers(view);
        }


        return view;
    }

    private void setButtonHandlers(View v) {

        ImageButton view = (ImageButton) v.findViewById(R.id.view);
        ImageButton edit = (ImageButton) v.findViewById(R.id.edit);
        ImageButton delete = (ImageButton) v.findViewById(R.id.delete);

        setEventListeners(view, edit, delete);
    }

    private <T extends ImageButton> void setEventListeners(T... elements) {
        for (T element : elements) {
            element.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    int position = _list.getPositionForView(view);

                    IPerson p;

                    switch (view.getId()) {
                        case R.id.view:
                            p = getItem(position);
                            Intent result = new Intent();
                            result.putExtra(BusinessConstants.MANAGE_PEOPLE_SELECTED_PERSON, p);

                            _callback.finishWithResult(result);
                            break;
                        case R.id.edit:
                            p = getItem(position);
                            editPerson(p);
                            break;
                        case R.id.delete:
                            deletePerson(position);
                            break;
                        default:
                            break;
                    }
                }
            });
        }
    }

    @Override
    public int getItemViewType(int position) {
        IPerson item = getItem(position);

        if (item instanceof Person) return 0;
        if (item instanceof People) return 1;

        return 0;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();

        SettingsService.SaveObject(_context, BusinessConstants.PREFS_PEOPLE_KEY, _people);
    }
}
