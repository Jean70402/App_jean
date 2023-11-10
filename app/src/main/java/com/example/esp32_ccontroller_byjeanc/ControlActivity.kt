package com.example.esp32_ccontroller_byjeanc

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.joystickjhr.JoystickJhr
import ingenieria.jhr.bluetoothjhr.BluetoothJhr
import java.io.IOException

class ControlActivity : AppCompatActivity() {
    //lateinit var blue:BluetoothJhr
    var dir_anterior=0

    @SuppressLint("ClickableViewAccessibility", "SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_control)

        val idBtnLuz_1on: Button = findViewById(R.id.idBtnLuz_1on)
        val idBtnLuz_1off: Button = findViewById(R.id.idBtnLuz_1off)
        val idBtnLuz_2on: Button = findViewById(R.id.idBtnLuz_2on)
        val idBtnLuz_2off: Button = findViewById(R.id.idBtnLuz_2off)
        val btnRegresar: Button = findViewById(R.id.btnRegresar)

        val distanciaX: TextView = findViewById(R.id.distanciaX)

        val joystickJhr: JoystickJhr = findViewById(R.id.joystickJhr)

        val distanciaY: TextView = findViewById(R.id.distanciaY)
        val angle: TextView = findViewById(R.id.angle)
        val distancia: TextView = findViewById(R.id.distancia)

        joystickJhr.setOnTouchListener { view, motionEvent->
            joystickJhr.move(motionEvent)
            distanciaX.text= "X= "+joystickJhr.joyX().toString()
            distanciaY.text= "Y= "+joystickJhr.joyX().toString()
            angle.text= "Angulo= "+joystickJhr.angle().toString()
            distancia.text= "Distancia= "+joystickJhr.distancia().toString()
            var dir =joystickJhr.direccion
            Log.d("Joystick", "Dirección actual: $dir")
            if(dir_anterior!=dir){
                dir_anterior=dir
                if(dir==joystickJhr.stick_up()){
                    BluetoothManagerMine.sendCommand(MainActivity.m_bluetoothSocket, "V")
                }
                else if(dir==joystickJhr.stick_upRight()){
                    BluetoothManagerMine.sendCommand(MainActivity.m_bluetoothSocket, "X")
                }
                else if(dir==joystickJhr.stick_right()){
                    BluetoothManagerMine.sendCommand(MainActivity.m_bluetoothSocket, "Y")
                }
                else if(dir==joystickJhr.stick_downRight()){
                    BluetoothManagerMine.sendCommand(MainActivity.m_bluetoothSocket, "Z")
                }
                else if(dir==joystickJhr.stick_down()){
                    BluetoothManagerMine.sendCommand(MainActivity.m_bluetoothSocket, "E")
                }
                else if(dir==joystickJhr.stick_downLeft()){
                    BluetoothManagerMine.sendCommand(MainActivity.m_bluetoothSocket, "F")
                }
                else if(dir==joystickJhr.stick_left()){
                    BluetoothManagerMine.sendCommand(MainActivity.m_bluetoothSocket, "G")
                }
                else if(dir==joystickJhr.stick_upLeft()){
                    BluetoothManagerMine.sendCommand(MainActivity.m_bluetoothSocket, "H")
                }
                else if(dir==joystickJhr.stick_none()){
                    BluetoothManagerMine.sendCommand(MainActivity.m_bluetoothSocket, "N")
                }
            }
            true
        }



        // Botones de control de luces y envío de mensajes
        idBtnLuz_1on.setOnClickListener { BluetoothManagerMine.sendCommand(MainActivity.m_bluetoothSocket, "A") }
        idBtnLuz_1off.setOnClickListener { BluetoothManagerMine.sendCommand(MainActivity.m_bluetoothSocket, "B") }
        idBtnLuz_2on.setOnClickListener { BluetoothManagerMine.sendCommand(MainActivity.m_bluetoothSocket, "C") }
        idBtnLuz_2off.setOnClickListener { BluetoothManagerMine.sendCommand(MainActivity.m_bluetoothSocket, "D") }


//        idBtnEnviar.setOnClickListener {
//            if (idTextOut.text.toString().isEmpty()) {
//                Toast.makeText(this, "El mensaje no puede estar vacío", Toast.LENGTH_SHORT).show()
//            } else {
//                val mensaje_out: String = idTextOut.text.toString()
//                sendCommand(mensaje_out)
//            }
//        }
        //cambiar pantalla
        btnRegresar.setOnClickListener {
            // Desconectar al regresar a la MainActivity
            BluetoothManagerMine.disconnect(MainActivity.m_bluetoothSocket)
            val intent = Intent(this, MainActivity::class.java)

            startActivity(intent)

        }
    }
//    private fun sendCommand(input: String) {
//        if (MainActivity.m_bluetoothSocket != null) {
//            try {
//                MainActivity.m_bluetoothSocket!!.outputStream.write(input.toByteArray())
//            } catch (e: IOException) {
//                e.printStackTrace()
//            }
//        }
//    }


}