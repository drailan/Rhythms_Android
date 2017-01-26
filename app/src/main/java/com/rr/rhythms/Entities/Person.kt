package com.rr.rhythms.Entities

import com.rr.rhythms.Interfaces.IPerson

import org.joda.time.DateTime

/**
 * Created by Huruk on 7/8/2016.
 */
class Person(val name: String, val birthDate: DateTime) : IPerson
