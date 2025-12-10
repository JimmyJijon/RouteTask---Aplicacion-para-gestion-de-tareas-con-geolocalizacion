package com.example.proyectofinal.web

import android.webkit.JavascriptInterface

class JsBridge(
    private val listener: (lat: Double, lon: Double) -> Unit,
    private val onBack: (() -> Unit)? = null
) {

    @JavascriptInterface
    fun onLocationSelected(latStr: String, lonStr: String) {
        try {
            val lat = latStr.toDouble()
            val lon = lonStr.toDouble()
            listener(lat, lon)
            // Volver automáticamente después de guardar
            onBack?.invoke()
        } catch (e: Exception) {
            // ignorar
        }
    }
}

