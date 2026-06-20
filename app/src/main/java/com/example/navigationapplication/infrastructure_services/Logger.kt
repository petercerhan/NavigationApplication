package com.example.navigationapplication.infrastructure_services

import android.util.Log

class Logger(val enabled: Boolean) {

    fun log(message: String) {
        if (!enabled) {
            return
        }
        Log.d("Development Logger", message)
    }

}