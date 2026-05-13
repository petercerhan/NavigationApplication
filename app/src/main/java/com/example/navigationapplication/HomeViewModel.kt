package com.example.navigationapplication

import android.util.Log
import androidx.lifecycle.ViewModel

class HomeViewModel: ViewModel() {

    //inner view model

    fun ping() {
        Log.d("PeterCerhan", "Ping HomeViewModel")
    }

}