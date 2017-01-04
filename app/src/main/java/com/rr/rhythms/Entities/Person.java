package com.rr.rhythms.Entities;

import com.rr.rhythms.Interfaces.IPerson;

import org.joda.time.DateTime;

/**
 * Created by Huruk on 7/8/2016.
 */
public class Person implements IPerson {
    private String _name;
    private DateTime _birthDate;

    public Person(String name, DateTime bDay) {
        _name = name;
        _birthDate = bDay;
    }

    public String getName() {
        return _name;
    }

    public DateTime getBirthDate() {
        return _birthDate;
    }
}
