package com.rr.rhythms.Interfaces

import android.content.Intent

/**
 * Created by Huruk on 7/10/2016.
 */
interface IActivityDismissable {
    fun finishWithoutResult()
    fun finishWithResult(result: Intent)
}
