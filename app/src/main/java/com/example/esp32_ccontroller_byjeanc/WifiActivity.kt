package com.example.esp32_ccontroller_byjeanc

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request


class WifiActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        setTheme(android.R.style.Theme_NoTitleBar_Fullscreen)
        actionBar?.hide()
        supportActionBar?.hide()
        setContentView(R.layout.activity_wifi)

        val editTextCommand = findViewById<EditText>(R.id.editTextCommand)
        val buttonSend = findViewById<Button>(R.id.buttonSend)
        val textViewResponse = findViewById<TextView>(R.id.textViewResponse)

        buttonSend.setOnClickListener {
            val command = editTextCommand.text.toString()
            sendCommand(command, textViewResponse)
        }
    }

    private fun sendCommand(command: String, textViewResponse: TextView) {
        val client = OkHttpClient()
        val url = "http://192.168.18.42/command?value=$command"

        GlobalScope.launch(Dispatchers.IO) {
            val request = Request.Builder()
                .url(url)
                .build()

            try {
                val response = client.newCall(request).execute()
                val responseBody = response.body?.string()

                launch(Dispatchers.Main) {
                    textViewResponse.text = "Response: $responseBody"
                }
            } catch (e: Exception) {
                launch(Dispatchers.Main) {
                    textViewResponse.text = "Error: ${e.message}"
                }
            }
        }
    }
}