package com.example.esp32_ccontroller_byjeanc

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import com.example.joystickjhr.JoystickJhr
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request

class ControlWifi : AppCompatActivity() {
    private lateinit var wifiViewModel: WifiViewModel
    var dir_anterior = 0
    @SuppressLint("ClickableViewAccessibility", "SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_control_wifi)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        setTheme(android.R.style.Theme_NoTitleBar_Fullscreen)
        actionBar?.hide()
        supportActionBar?.hide()

        val idBtnLuz_1on: Button = findViewById(R.id.idBtnLuz_1on)
        val idBtnLuz_1off: Button = findViewById(R.id.idBtnLuz_1off)
        val idBtnLuz_2on: Button = findViewById(R.id.idBtnLuz_2on)
        val idBtnLuz_2off: Button = findViewById(R.id.idBtnLuz_2off)
        val btnRegresar: Button = findViewById(R.id.btnRegresar)
        wifiViewModel = ViewModelProvider(this).get(WifiViewModel::class.java)
        Log.d("WifiActivity", "ESP32 IP set to: ${wifiViewModel.esp32IP}")
        val esp32IP = wifiViewModel.esp32IP

        val distanciaX: TextView = findViewById(R.id.distanciaX)

        val joystickJhr: JoystickJhr = findViewById(R.id.joystickJhr)

        val distanciaY: TextView = findViewById(R.id.distanciaY)
        val angle: TextView = findViewById(R.id.angle)
        val distancia: TextView = findViewById(R.id.distancia)

        val textViewResponse2: TextView = findViewById(R.id.textID)

        textViewResponse2.text= "Ip actual =$esp32IP"

        joystickJhr.setOnTouchListener { view, motionEvent ->
            joystickJhr.move(motionEvent)
            distanciaX.text = "X= " + joystickJhr.joyX().toString()
            distanciaY.text = "Y= " + joystickJhr.joyX().toString()
            angle.text = "Angulo= " + joystickJhr.angle().toString()
            distancia.text = "Distancia= " + joystickJhr.distancia().toString()
            var dir = joystickJhr.direccion
            Log.d("Joystick", "Dirección actual: $dir")

            if (dir_anterior != dir) {
                dir_anterior = dir
                runOnUiThread {
                    when (dir) {
                        joystickJhr.stick_up() -> sendCommand(esp32IP, "V")
                        joystickJhr.stick_upRight() -> sendCommand(esp32IP, "X")
                        joystickJhr.stick_right() -> sendCommand(esp32IP, "Y")
                        joystickJhr.stick_downRight() -> sendCommand(esp32IP, "Z")
                        joystickJhr.stick_down() -> sendCommand(esp32IP, "E")
                        joystickJhr.stick_downLeft() -> sendCommand(esp32IP, "F")
                        joystickJhr.stick_left() -> sendCommand(esp32IP, "G")
                        joystickJhr.stick_upLeft() -> sendCommand(esp32IP, "H")
                        joystickJhr.stick_none() -> sendCommand(esp32IP, "N")
                    }
                }
            }
            true
        }


        // Botones de control de luces y envío de mensajes
        idBtnLuz_1on.setOnClickListener {
            sendCommand(esp32IP, "A")
        }
        idBtnLuz_1off.setOnClickListener {
            sendCommand(esp32IP, "B")
        }
        idBtnLuz_2on.setOnClickListener {
            sendCommand(esp32IP, "C")
        }
        idBtnLuz_2off.setOnClickListener {
            sendCommand(esp32IP, "D")
        }
        btnRegresar.setOnClickListener {
            // Desconectar al regresar a la Selector Activity
            val intent = Intent(this, WifiActivity::class.java)
            startActivity(intent)
        }
    }

    fun sendCommand(esp32IP: String, command: String) {
        val client = OkHttpClient()
        val url = "http://$esp32IP/command?value=$command"

        GlobalScope.launch(Dispatchers.IO) {
            val request = Request.Builder()
                .url(url)
                .build()

            try {
                val response = client.newCall(request).execute()
                val responseBody = response.body?.string()

                // Aquí eliminé la segunda llamada
            } catch (e: Exception) {
                // Manejar el error si es necesario
            }
        }
    }
}