package com.example.navigationapplication

import android.util.Log
import androidx.lifecycle.ViewModel
import java.util.UUID

class RootContainerViewModel(
    val rootContainerId: UUID,
    val serviceLocator: MutableMap<UUID, Any>,
) : ViewModel() {

    fun ping() {
        Log.d("PeterCerhan", "Ping RootContainerViewModel $rootContainerId")
    }
}