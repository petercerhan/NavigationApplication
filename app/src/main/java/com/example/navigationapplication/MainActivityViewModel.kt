package com.example.navigationapplication

import android.util.Log
import androidx.lifecycle.ViewModel
import java.util.UUID
import com.example.navigationapplication.controller_library.ApplicationViewModelLocator
import com.example.navigationapplication.infrastructure_services.UUIDService
import com.example.navigationapplication.infrastructure_services.UUIDServiceImpl

class MainActivityViewModel : ViewModel() {
//    val rootContainerServiceLocator: MutableMap<UUID, Any> = mutableMapOf()
    val viewModelLocator: ApplicationViewModelLocator = ApplicationViewModelLocator()
    val coordinator: RootCoordinator
    val uuidService: UUIDService = UUIDServiceImpl()

    init {
        val rootContainerId = uuidService.newUUID()
        val rootContainerViewModel = RootContainerViewModel(id=rootContainerId)

        coordinator = RootCoordinator(rootContainerViewModel, uuidService)

//        viewModelLocator[rootContainerId] = rootContainerViewModel
        viewModelLocator.cacheViewModel(rootContainerId, rootContainerViewModel)

        coordinator.start()
    }

    fun ping() {
        Log.d("PeterCerhan", "Ping MainActivityViewModel")
    }
}
