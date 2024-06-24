package com.ek.firebasesmssample

import android.content.Context
import android.content.SharedPreferences

class SharedPrefManager(context: Context) {

    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("MySharedPref", Context.MODE_PRIVATE)

    fun saveUID(uid: String) {
        val editor = sharedPreferences.edit()
        editor.putString("uid", uid)
        editor.apply()
    }

    fun getUID(): String? {
        return sharedPreferences.getString("uid", null)
    }
}