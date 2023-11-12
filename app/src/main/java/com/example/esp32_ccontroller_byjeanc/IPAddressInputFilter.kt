package com.example.esp32_ccontroller_byjeanc

import android.text.InputFilter
import android.text.Spanned

class IPAddressInputFilter : InputFilter {
    override fun filter(
        source: CharSequence?,
        start: Int,
        end: Int,
        dest: Spanned?,
        dstart: Int,
        dend: Int
    ): CharSequence? {
        val builder = StringBuilder(dest)
        builder.insert(dstart, source, start, end)

        // Permitir la entrada de números y puntos
        if (!builder.toString().matches("[0-9.]*".toRegex())) {
            return ""
        }

        // Validar la dirección IP
        if (!isValidIPAddress(builder.toString())) {
            return ""
        }

        return null
    }

    private fun isValidIPAddress(ipAddress: String): Boolean {
        val parts = ipAddress.split("\\.".toRegex()).toTypedArray()

        for (part in parts) {
            try {
                val value = part.toInt()
                if (value < 0 || value > 255) {
                    return false
                }
            } catch (e: NumberFormatException) {
                return false
            }
        }

        return true
    }
}