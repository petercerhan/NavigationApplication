package com.example.navigationapplication

import android.util.Log
import androidx.lifecycle.ViewModel
import java.util.UUID

class MainActivityDelegatingViewModel: ViewModel() {

    val id = uuid()

    private fun uuid() = UUID.randomUUID().toString()

    fun ping() {
        Log.d("PeterCerhan", "Ping MainActivityDelegatingViewModel id=$id")
    }

}
