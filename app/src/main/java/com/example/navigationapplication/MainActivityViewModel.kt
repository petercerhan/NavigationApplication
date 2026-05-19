package com.example.navigationapplication

import android.util.Log
import androidx.lifecycle.ViewModel
import java.util.UUID

class MainActivityViewModel : ViewModel() {
    val serviceLocator: MutableMap<UUID, Any> = mutableMapOf()
    val coordinator: RootCoordinator

    init {
        val rootContainerId = UUID.randomUUID()
        val rootContainerViewModel = RootContainerViewModel(id=rootContainerId)

        val coordinatorId = UUID.randomUUID()
        coordinator = RootCoordinator(id=coordinatorId, rootContainerViewModel=rootContainerViewModel, serviceLocator=serviceLocator)

        serviceLocator[rootContainerId] = rootContainerViewModel
        serviceLocator[coordinatorId] = coordinator

        coordinator.start()
    }

    fun ping() {
        Log.d("PeterCerhan", "Ping MainActivityViewModel")
    }
}
