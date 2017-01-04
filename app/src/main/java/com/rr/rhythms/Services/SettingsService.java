package com.rr.rhythms.Services;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Created by Huruk on 7/10/2016.
 */
public class SettingsService {
    public static void SaveJson(Context context, String key, String json) {
        SharedPreferences prefs = context.getSharedPreferences(key, Context.MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = prefs.edit();

        prefsEditor
                .putString(key, json)
                .apply();
    }

    public static String ReadJson(Context context, String key) {
        SharedPreferences prefs = context.getSharedPreferences(key, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        return prefs.getString(key, "");
    }

    public static void SaveObject(Context context, String fileName, Object object) {
        try {
            FileOutputStream fos = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            ObjectOutputStream os;
            os = new ObjectOutputStream(fos);
            os.writeObject(object);
            os.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Object LoadObject(Context context, String fileName) {
        try {
            FileInputStream fis = context.openFileInput(fileName);
            ObjectInputStream is = new ObjectInputStream(fis);
            Object o = is.readObject();
            is.close();
            fis.close();

            return o;
        } catch (Exception e) {

        }

        return null;
    }
}
