package com.nurdev.notener

import android.content.Context
import android.content.SharedPreferences

class SharedPreferencesManager(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

    // get data
    fun getDataFromSharedPreferences(): String? {
        return prefs.getString("cardNumber", "7777")
    }

    // save data
    fun saveDataToSharedPreferences(cardNumber: String) {
        prefs.edit().putString("cardNumber", cardNumber).apply()
    }

    // clear all data
    fun clearDataFromSharedPreferences() {
        prefs.edit().clear().apply()
    }

    // remove specific data
    fun removeDataFromSharedPreferences(key: String) {
        prefs.edit().remove(key).apply()
    }

}