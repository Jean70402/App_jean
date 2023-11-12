package com.example.esp32_ccontroller_byjeanc

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request

class WifiViewModel(application: Application) : AndroidViewModel(application) {
    private val sharedPreferences =
        application.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

    var esp32IP: String
        get() = sharedPreferences.getString("esp32IP", "") ?: ""
        set(value) {
            sharedPreferences.edit().putString("esp32IP", value).apply()
        }

    val client = OkHttpClient()
}
