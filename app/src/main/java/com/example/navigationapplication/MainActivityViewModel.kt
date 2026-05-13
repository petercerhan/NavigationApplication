package com.example.navigationapplication

import android.util.Log
import androidx.lifecycle.ViewModel
import java.util.UUID

class MainActivityViewModel : ViewModel() {

    val serviceLocator: MutableMap<UUID, Any> = mutableMapOf()
    val coordinator: RootCoordinator

    init {
        val rootContainerId = UUID.randomUUID()
        val rootContainer = RootContainer(id=rootContainerId)

        val coordinatorId = UUID.randomUUID()
        coordinator = RootCoordinator(id=coordinatorId, rootContainer=rootContainer)

        serviceLocator[rootContainerId] = rootContainer
        serviceLocator[coordinatorId] = coordinator
    }

    fun ping() {
        Log.d("PeterCerhan", "Ping MainActivityViewModel")
    }
}
