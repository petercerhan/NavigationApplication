package com.example.navigationapplication

import android.util.Log
import java.util.UUID

class HomeViewModel(
    val id: UUID,
) {

    fun ping() {
        Log.d("PeterCerhan", "Ping HomeViewModel $id")
    }

}