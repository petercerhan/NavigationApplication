package com.example.navigationapplication

import android.util.Log
import androidx.lifecycle.ViewModel
import java.util.UUID
import com.example.navigationapplication.controller_library.ApplicationViewModelLocator

class MainActivityViewModel : ViewModel() {
    val viewModelLocator: ApplicationViewModelLocator = ApplicationViewModelLocator()
    val rootContainerServiceLocator: MutableMap<UUID, Any> = mutableMapOf()
    val coordinator: RootCoordinator

    init {
        val rootContainerId = UUID.randomUUID()
        val rootContainerViewModel = RootContainerViewModel(id=rootContainerId)

        coordinator = RootCoordinator(rootContainerViewModel)

        rootContainerServiceLocator[rootContainerId] = rootContainerViewModel

        coordinator.start()
    }

    fun ping() {
        Log.d("PeterCerhan", "Ping MainActivityViewModel")
    }
}
