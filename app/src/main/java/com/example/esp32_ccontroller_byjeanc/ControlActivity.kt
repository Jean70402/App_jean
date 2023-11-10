package com.example.esp32_ccontroller_byjeanc

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import java.io.IOException

class ControlActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_control)

        val idBtnEnviar: Button = findViewById(R.id.idBtnEnviar3)
        val idBtnLuz_1on: Button = findViewById(R.id.idBtnLuz_1on)
        val idBtnLuz_1off: Button = findViewById(R.id.idBtnLuz_1off)
        val idBtnLuz_2on: Button = findViewById(R.id.idBtnLuz_2on)
        val idBtnLuz_2off: Button = findViewById(R.id.idBtnLuz_2off)
        val idTextOut: EditText = findViewById(R.id.idTextOut3)
        val btnRegresar: Button = findViewById(R.id.btnRegresar)

        // Botones de control de luces y envío de mensajes
        idBtnLuz_1on.setOnClickListener { BluetoothManagerMine.sendCommand(MainActivity.m_bluetoothSocket, "A") }
        idBtnLuz_1off.setOnClickListener { BluetoothManagerMine.sendCommand(MainActivity.m_bluetoothSocket, "B") }
        idBtnLuz_2on.setOnClickListener { BluetoothManagerMine.sendCommand(MainActivity.m_bluetoothSocket, "C") }
        idBtnLuz_2off.setOnClickListener { BluetoothManagerMine.sendCommand(MainActivity.m_bluetoothSocket, "D") }


        idBtnEnviar.setOnClickListener {
            if (idTextOut.text.toString().isEmpty()) {
                Toast.makeText(this, "El mensaje no puede estar vacío", Toast.LENGTH_SHORT).show()
            } else {
                val mensaje_out: String = idTextOut.text.toString()
                sendCommand(mensaje_out)
            }
        }
        //cambiar pantalla
        btnRegresar.setOnClickListener {
            // Desconectar al regresar a la MainActivity
            BluetoothManagerMine.disconnect(MainActivity.m_bluetoothSocket)
            val intent = Intent(this, MainActivity::class.java)

            startActivity(intent)

        }
    }
    private fun sendCommand(input: String) {
        if (MainActivity.m_bluetoothSocket != null) {
            try {
                MainActivity.m_bluetoothSocket!!.outputStream.write(input.toByteArray())
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }


}