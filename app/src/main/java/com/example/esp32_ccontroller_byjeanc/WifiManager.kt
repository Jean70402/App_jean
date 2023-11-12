import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException

class WifiManager {
    private var client: OkHttpClient? = null

    fun connect(esp32IP: String): Boolean {
        client = OkHttpClient.Builder().build()
        // Lógica de conexión
        return true  // o false si falla la conexión
    }

    fun sendCommand(esp32IP: String, command: String): String {
        val client = OkHttpClient()
        val url = "http://$esp32IP/command?value=$command"

        return try {
            val request = Request.Builder()
                .url(url)
                .build()

            val response = client.newCall(request).execute()
            response.body?.string() ?: "No response body"
        } catch (e: Exception) {
            "Error: ${e.message}"
        }
    }

    fun disconnect() {
        // Lógica de desconexión
        client?.dispatcher?.executorService?.shutdown()
        client = null
    }
}
