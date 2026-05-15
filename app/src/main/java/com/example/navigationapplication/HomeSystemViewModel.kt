package com.example.navigationapplication

import android.util.Log
import androidx.lifecycle.ViewModel
import java.util.UUID

class HomeSystemViewModel(
    val viewModelId: UUID,
    val serviceLocator: MutableMap<UUID, Any>,
): ViewModel() {

    //inner view model
    val viewModel: HomeViewModel
        get() = serviceLocator[viewModelId] as HomeViewModel

    fun ping() {
        Log.d("PeterCerhan", "Ping HomeSystemViewModel $viewModelId")
    }

}