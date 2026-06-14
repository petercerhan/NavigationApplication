package com.example.navigationapplication

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.navigationapplication.controller_library.ApplicationViewModelLocator
import com.example.navigationapplication.controller_library.ServiceLocatorViewModel
import com.example.navigationapplication.infrastructure_services.UUIDService
import com.example.navigationapplication.infrastructure_services.UUIDServiceImpl

class MainActivityViewModel : ViewModel() {
    var coordinator: RootCoordinator
    val uuidService: UUIDService = UUIDServiceImpl()

    init {
        Log.d("Peter Cerhan", "MainActivityViewModel Initialized")
        val rootContainerId = uuidService.newUUID()
        val rootContainerViewModel = RootContainerViewModel(id=rootContainerId)

        coordinator = RootCoordinator(rootContainerViewModel, uuidService)

//        serviceLocator.cacheViewModel(rootContainerId, rootContainerViewModel)
//
//        coordinator.start()
    }

    fun ping() {
        Log.d("PeterCerhan", "Ping MainActivityViewModel")
    }
}
