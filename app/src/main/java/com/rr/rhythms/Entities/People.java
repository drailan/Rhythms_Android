package com.rr.rhythms.Entities;

import com.rr.rhythms.Interfaces.IPerson;

/**
 * Created by Huruk on 7/9/2016.
 */
public class People implements IPerson {
    private Person[] _people;

    public People(Person[] person) {
        _people = person;
    }

    public Person getAt(int i) {
        return _people[i];
    }

    public Person[] getPeople() {
        return _people;
    }
}
