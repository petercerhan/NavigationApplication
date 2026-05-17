package com.example.navigationapplication

import android.util.Log
import androidx.lifecycle.ViewModel
import java.util.UUID

class MainActivityViewModel : ViewModel() {

    val serviceLocator: MutableMap<UUID, Any> = mutableMapOf()
    val coordinator: RootCoordinator

    init {
        val homeViewModelId = UUID.randomUUID()
        val homeViewModel = HomeViewModel(homeViewModelId)

        val rootContainerId = UUID.randomUUID()
        val rootContainerViewModel = RootContainerViewModel(id=rootContainerId, homeViewModel=homeViewModel)

        val coordinatorId = UUID.randomUUID()
        coordinator = RootCoordinator(id=coordinatorId, rootContainerViewModel=rootContainerViewModel, serviceLocator=serviceLocator)

        //Lateinit Delegate - remove when possible//
        homeViewModel.delegate = coordinator
        //

        serviceLocator[homeViewModelId] = homeViewModel
        serviceLocator[rootContainerId] = rootContainerViewModel
        serviceLocator[coordinatorId] = coordinator
    }

    fun ping() {
        Log.d("PeterCerhan", "Ping MainActivityViewModel")
    }
}
