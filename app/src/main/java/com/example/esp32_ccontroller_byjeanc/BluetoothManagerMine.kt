package com.example.esp32_ccontroller_byjeanc

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothSocket
import java.io.IOException
import java.util.*

object BluetoothManagerMine {
    private val m_myUUID: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")

    fun connect(address: String): BluetoothSocket? {
        try {
            val device = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(address)
            //
            val socket = device.createRfcommSocketToServiceRecord(m_myUUID)
            socket.connect()
            return socket
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }

    fun disconnect(socket: BluetoothSocket?) {
        try {
            socket?.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun sendCommand(socket: BluetoothSocket?, command: String) {
        try {
            socket?.outputStream?.write(command.toByteArray())
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}
