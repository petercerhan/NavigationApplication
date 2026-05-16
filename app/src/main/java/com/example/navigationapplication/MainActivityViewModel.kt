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
        val rootContainer = RootContainer(id=rootContainerId, homeViewModel=homeViewModel)

        val coordinatorId = UUID.randomUUID()
        coordinator = RootCoordinator(id=coordinatorId, rootContainer=rootContainer, serviceLocator=serviceLocator)

        //HERE//
        rootContainer.delegate = coordinator
        homeViewModel.delegate = rootContainer
        //

        serviceLocator[homeViewModelId] = homeViewModel
        serviceLocator[rootContainerId] = rootContainer
        serviceLocator[coordinatorId] = coordinator
    }

    fun ping() {
        Log.d("PeterCerhan", "Ping MainActivityViewModel")
    }
}
