package com.rr.rhythms.Interfaces;

import android.content.Intent;

/**
 * Created by Huruk on 7/10/2016.
 */
public interface IActivityDismissable {
    void finishWithoutResult();
    void finishWithResult(Intent result);
}
