package com.example.esp32_ccontroller_byjeanc

import WifiManager

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.text.method.DigitsKeyListener
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request


class WifiActivity : AppCompatActivity() {
    private lateinit var wifiViewModel: WifiViewModel
    @SuppressLint("SetTextI18n")
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

        val buttonConnect = findViewById<Button>(R.id.buttonConnect)
        val editTextCommand = findViewById<EditText>(R.id.editTextCommand)
        val buttonSend = findViewById<Button>(R.id.buttonSend)
        val textViewResponse = findViewById<TextView>(R.id.textViewResponse)
        val btnRegresar: Button = findViewById(R.id.btnRegresar)
        val textResponse2: TextView=findViewById(R.id.textID)


        btnRegresar.setOnClickListener {
            // Desconectar al regresar a la Selector Activity
            val intent = Intent(this, Selector::class.java)
            startActivity(intent)
        }

        wifiViewModel = ViewModelProvider(this).get(WifiViewModel::class.java)
        textResponse2.text= "Ip actual =${wifiViewModel.esp32IP}"
        val editTextIP = findViewById<EditText>(R.id.editTextIP)

        buttonConnect.setOnClickListener {
            wifiViewModel.esp32IP = editTextIP.text.toString()
            textViewResponse.text = "Connected to ${wifiViewModel.esp32IP}"
        }

        buttonSend.setOnClickListener {
            if (wifiViewModel.esp32IP.isNotEmpty()) {
                editTextIP.setText(wifiViewModel.esp32IP)
            }

            val command = editTextCommand.text.toString()
            val esp32IP = editTextIP.text.toString()
            sendCommand(esp32IP, command, textViewResponse)
        }

        val btnIrAControl: Button = findViewById(R.id.irControl)
        btnIrAControl.setOnClickListener {

            val intent = Intent(this, ControlWifi::class.java)
            startActivity(intent)
        }
    }

    private fun sendCommand(esp32IP: String, command: String, textViewResponse: TextView) {
        val client = OkHttpClient()
        val url = "http://$esp32IP/command?value=$command"

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