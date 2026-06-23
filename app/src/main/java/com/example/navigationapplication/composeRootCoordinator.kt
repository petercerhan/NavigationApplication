package com.example.navigationapplication

import com.example.navigationapplication.container.ContainerViewModel
import com.example.navigationapplication.infrastructure_services.Logger
import com.example.navigationapplication.infrastructure_services.UUIDService

fun composeRootCoordinator(
    uuidService: UUIDService
): RootCoordinator {
    val logger = Logger(false)
    val containerViewModel = ContainerViewModel(uuidService, logger)
    return RootCoordinator(containerViewModel, uuidService)
}