package com.example.esp32_ccontroller_byjeanc

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.AsyncTask
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.airbnb.lottie.LottieAnimationView
import kotlinx.coroutines.CoroutineScope
import java.io.IOException
import java.util.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext





class MainActivity : AppCompatActivity() {
    private lateinit var mBtAdapter: BluetoothAdapter
    private lateinit var mAddressDevices: ArrayAdapter<String>
    private lateinit var mNameDevices: ArrayAdapter<String>

    private lateinit var lottieAnimationView: LottieAnimationView
    private var isConnected: Boolean = false
    companion object {
        private val m_myUUID: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
        private var m_bluetoothSocket: BluetoothSocket? = null
        private var m_isConnected: Boolean = false
        private lateinit var m_address: String
        const val REQUEST_ENABLE_BT = 1
    }
    private val someActivityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == REQUEST_ENABLE_BT) {
            Log.i("MainActivity", "ACTIVIDAD REGISTRADA")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mAddressDevices = ArrayAdapter(this, android.R.layout.simple_list_item_1)
        mNameDevices = ArrayAdapter(this, android.R.layout.simple_list_item_1)

        val idBtnOnBT: Button = findViewById(R.id.idBtnOnBT)
        val idBtnConect: Button = findViewById(R.id.idBtnConect)
        val idBtnEnviar: Button = findViewById(R.id.idBtnEnviar)
        val idBtnLuz_1on: Button = findViewById(R.id.idBtnLuz_1on)
        val idBtnLuz_1off: Button = findViewById(R.id.idBtnLuz_1off)
        val idBtnLuz_2on: Button = findViewById(R.id.idBtnLuz_2on)
        val idBtnLuz_2off: Button = findViewById(R.id.idBtnLuz_2off)
        val idBtnDispBT: Button = findViewById(R.id.idBtnDispBT)
        val idSpinDisp: Spinner = findViewById(R.id.idSpinDisp)
        val idBtnDisconnect: Button = findViewById(R.id.idBtnDisonect)
        val adapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, mNameDevices?.count?.let { count ->
            (0 until count).mapNotNull { mNameDevices?.getItem(it) }
        } ?: emptyList())

        idSpinDisp.adapter = adapter
        val idTextOut: EditText = findViewById(R.id.idTextOut)

        // Inicialización del BluetoothAdapter
        mBtAdapter = (getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager).adapter

        // Solicitar permisos en tiempo de ejecución
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.BLUETOOTH_CONNECT
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.BLUETOOTH_CONNECT),
                REQUEST_ENABLE_BT
            )
        }
        lottieAnimationView = findViewById(R.id.lottieAnimationView)
        // Por defecto, carga la animación "off.json"
        loadAnimation("off")

        // Configura tu botón de desconexión
        // Botón Encender Bluetooth
        idBtnOnBT.setOnClickListener {
            if (mBtAdapter.isEnabled) {
                Toast.makeText(this, "Bluetooth ya se encuentra activado", Toast.LENGTH_LONG).show()
            } else {
                val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                someActivityResultLauncher.launch(enableBtIntent)
            }
        }
        idSpinDisp.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                m_address = mAddressDevices.getItem(position) ?: ""
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Puedes manejar esto si es necesario
            }
        }
        // Botón Dispositivos Emparejados
        idBtnDispBT.setOnClickListener {
            if (!mBtAdapter.isEnabled) {
                // Intenta encender Bluetooth si no está habilitado
                val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                someActivityResultLauncher.launch(enableBtIntent)
                return@setOnClickListener
            }

            // Verificar permiso BLUETOOTH_SCAN
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.BLUETOOTH_SCAN
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // Si no se tiene el permiso, solicitarlo
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.BLUETOOTH_SCAN),
                    REQUEST_ENABLE_BT
                )
            } else {
                // Si ya se tiene el permiso, realizar la operación que lo necesita
                mBtAdapter.cancelDiscovery()

                // Continúa obteniendo los dispositivos emparejados...
                val pairedDevices: Set<BluetoothDevice>? = mBtAdapter.bondedDevices
                mAddressDevices?.clear()
                mNameDevices?.clear()

                pairedDevices?.forEach { device ->
                    val deviceName = device.name
                    val deviceHardwareAddress = device.address // MAC address
                    mAddressDevices?.add(deviceHardwareAddress)
                    mNameDevices?.add(deviceName)
                }

                // Notificar al adaptador que los datos han cambiado
                adapter.clear()
                for (i in 0 until mNameDevices?.count!!) {
                    adapter.add(mNameDevices?.getItem(i))
                }
                adapter.notifyDataSetChanged()
            }
        }

        // Botón Conectar
        // Botón Conectar
        idBtnConect.setOnClickListener {
            if (m_bluetoothSocket == null || !m_isConnected) {
                // Obtener la dirección MAC del dispositivo seleccionado en el Spinner
                val selectedDeviceName: String = idSpinDisp.selectedItem?.toString() ?: ""

                if (mAddressDevices.isEmpty) {
                    Toast.makeText(this, "Primero selecciona dispositivos Bluetooth", Toast.LENGTH_LONG).show()
                    return@setOnClickListener
                }

                // Obtener la dirección MAC desde el ArrayAdapter
                val selectedDeviceAddress: String? =
                    mAddressDevices.getItem(mNameDevices.getPosition(selectedDeviceName))

                if (selectedDeviceAddress != null) {
                    m_address = selectedDeviceAddress

                    // Iniciar la tarea de conexión Bluetooth
                    ConnectBluetoothTask().connectBluetooth()
                } else {
                    Toast.makeText(
                        this,
                        "Error: No se encontró la dirección MAC del dispositivo",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }


        // Botones de control de luces y envío de mensajes
        idBtnLuz_1on.setOnClickListener { sendCommand("A") }
        idBtnLuz_1off.setOnClickListener { sendCommand("B") }
        idBtnLuz_2on.setOnClickListener { sendCommand("C") }
        idBtnLuz_2off.setOnClickListener { sendCommand("D") }

        idBtnEnviar.setOnClickListener {
            if (idTextOut.text.toString().isEmpty()) {
                Toast.makeText(this, "El mensaje no puede estar vacío", Toast.LENGTH_SHORT).show()
            } else {
                val mensaje_out: String = idTextOut.text.toString()
                sendCommand(mensaje_out)
            }
        }
        idBtnDisconnect.setOnClickListener {
            if (m_isConnected) {
                // Si está conectado, cerrar la conexión
                disconnectBluetooth()
            } else {
                Toast.makeText(this, "No estás conectado actualmente", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun sendCommand(input: String) {
        if (m_bluetoothSocket != null) {
            try {
                m_bluetoothSocket!!.outputStream.write(input.toByteArray())
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private inner class ConnectBluetoothTask : CoroutineScope by CoroutineScope(Dispatchers.Default) {
        fun connectBluetooth() {
            launch {
                val result = withContext(Dispatchers.IO) {
                    try {
                        mBtAdapter.cancelDiscovery()
                        val device: BluetoothDevice = mBtAdapter.getRemoteDevice(m_address)
                        m_bluetoothSocket = device.createRfcommSocketToServiceRecord(m_myUUID)
                        m_bluetoothSocket!!.connect()
                        m_isConnected = true
                        true
                    } catch (e: IOException) {
                        e.printStackTrace()
                        false
                    }
                }

                withContext(Dispatchers.Main) {
                    if (result) {
                        Toast.makeText(this@MainActivity, "CONEXIÓN EXITOSA", Toast.LENGTH_LONG).show()
                        Log.i("MainActivity", "CONEXIÓN EXITOSA")
                        loadAnimation("on")
                    } else {
                        Toast.makeText(this@MainActivity, "ERROR DE CONEXIÓN", Toast.LENGTH_LONG).show()
                        Log.i("MainActivity", "ERROR DE CONEXIÓN")
                    }
                }
            }
        }
    }
    private fun disconnectBluetooth() {
        try {
            m_bluetoothSocket?.close()
            m_isConnected = false
            Toast.makeText(this, "Desconexión exitosa", Toast.LENGTH_SHORT).show()
            loadAnimation("off")
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(this, "Error al desconectar", Toast.LENGTH_SHORT).show()
        }
    }
    private fun loadAnimation(animationFileName: String) {
        // Carga la animación desde el archivo JSON en res/raw
        val animationResId = resources.getIdentifier(animationFileName, "raw", packageName)
        lottieAnimationView.setAnimation(animationResId)
        // Inicia automáticamente la animación
        lottieAnimationView.playAnimation()
    }
}
