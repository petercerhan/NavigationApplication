package com.example.navigationapplication.root_sequence.main

import com.example.navigationapplication.controller_library.container.ContainerViewModel
import com.example.navigationapplication.infrastructure_services.ElapsedRealtimeService
import com.example.navigationapplication.infrastructure_services.Logger
import com.example.navigationapplication.infrastructure_services.UUIDService
import com.example.navigationapplication.root_sequence.controller.RootCoordinator

object RootCoordinatorFactory {
    fun composeRootCoordinator(
        uuidService: UUIDService,
        elapsedRealtimeService: ElapsedRealtimeService,
    ): RootCoordinator {
        val logger = Logger(false)
        val containerViewModel = ContainerViewModel(uuidService, elapsedRealtimeService, logger)
        val composer = RootSequenceComposer(uuidService, elapsedRealtimeService)
        return RootCoordinator(containerViewModel, composer)
    }
}