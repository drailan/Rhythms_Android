package com.rr.rhythms.Services

import android.content.Context
import android.content.SharedPreferences

import com.google.gson.Gson

import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.io.ObjectInputStream
import java.io.ObjectOutputStream

/**
 * Created by Huruk on 7/10/2016.
 */
object SettingsService {
    fun SaveJson(context: Context, key: String, json: String) {
        val prefs = context.getSharedPreferences(key, Context.MODE_PRIVATE)
        val prefsEditor = prefs.edit()

        prefsEditor
                .putString(key, json)
                .apply()
    }

    fun ReadJson(context: Context, key: String): String {
        val prefs = context.getSharedPreferences(key, Context.MODE_PRIVATE)
        val gson = Gson()
        return prefs.getString(key, "")
    }

    fun SaveObject(context: Context, fileName: String, `object`: Any) {
        try {
            val fos = context.openFileOutput(fileName, Context.MODE_PRIVATE)
            val os: ObjectOutputStream
            os = ObjectOutputStream(fos)
            os.writeObject(`object`)
            os.close()
            fos.close()
        } catch (e: IOException) {
            e.message;
        }
    }

    fun LoadObject(context: Context, fileName: String): Any? {
        try {
            val fis = context.openFileInput(fileName)
            val `is` = ObjectInputStream(fis)
            val o = `is`.readObject()
            `is`.close()
            fis.close()

            return o
        } catch (e: Exception) {

        }

        return null
    }
}
