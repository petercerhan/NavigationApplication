package com.example.navigationapplication.modal_sequence

import com.example.navigationapplication.container.ContainerViewModel
import com.example.navigationapplication.infrastructure_services.Logger
import com.example.navigationapplication.infrastructure_services.UUIDService

fun composeModalSequenceCoordinator(
    uuidService: UUIDService,
    delegate: ModalSequenceCoordinatorDelegate,
): ModalSequenceCoordinator {
    val logger = Logger(false)
    val containerViewModel = ContainerViewModel(uuidService, logger)
    return ModalSequenceCoordinator(
        containerViewModel,
        uuidService,
        delegate,
    )
}