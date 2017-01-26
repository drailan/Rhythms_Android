package com.rr.rhythms.Entities

import com.rr.rhythms.Interfaces.IPerson

/**
 * Created by Huruk on 7/9/2016.
 */
class People(val people: Array<Person>) : IPerson {
    fun getAt(i: Int): Person {
        return people[i]
    }
}
