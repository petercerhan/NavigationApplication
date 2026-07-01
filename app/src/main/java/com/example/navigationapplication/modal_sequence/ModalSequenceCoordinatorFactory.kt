package com.example.navigationapplication.modal_sequence

import com.example.navigationapplication.controller_library.container.ContainerViewModel
import com.example.navigationapplication.infrastructure_services.Logger
import com.example.navigationapplication.infrastructure_services.UUIDService

object ModalSequenceCoordinatorFactory {
    fun composeModalSequenceCoordinator(
        uuidService: UUIDService,
        delegate: ModalSequenceCoordinatorDelegate,
    ): ModalSequenceCoordinator {
        val logger = Logger(false)
        val containerViewModel = ContainerViewModel(uuidService, logger)
        val composer = ModalSequenceComposer(uuidService)
        return ModalSequenceCoordinator(
            containerViewModel,
            composer,
            delegate,
        )
    }
}

