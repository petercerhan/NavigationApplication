package com.example.navigationapplication

import android.util.Log
import androidx.lifecycle.ViewModel
import java.util.UUID

class MainActivityViewModel : ViewModel() {

    val serviceLocator: MutableMap<UUID, Any> = mutableMapOf()
    val coordinator: RootCoordinator

    init {
        val coordinatorId = UUID.randomUUID()
        coordinator = RootCoordinator(coordinatorId)
        serviceLocator[coordinatorId] = coordinator
    }

    fun ping() {
        Log.d("PeterCerhan", "Ping MainActivityViewModel")
    }
}
