package com.example.esp32_ccontroller_byjeanc

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import android.widget.Button

class Selector : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        setTheme(android.R.style.Theme_NoTitleBar_Fullscreen)
        actionBar?.hide()
        supportActionBar?.hide()

        setContentView(R.layout.activity_selector)

        val btnWifi: Button = findViewById(R.id.button_wifi)
        val btnBlue: Button = findViewById(R.id.button_blue)

        btnWifi.setOnClickListener {
            // Desconectar al regresar a la MainActivity
            val intent = Intent(this, WifiActivity::class.java)
            startActivity(intent)
        }
        btnBlue.setOnClickListener {
            // Desconectar al regresar a la MainActivity
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

}