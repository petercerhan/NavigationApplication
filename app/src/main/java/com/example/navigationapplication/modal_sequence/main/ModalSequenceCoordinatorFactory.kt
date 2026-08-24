package com.example.navigationapplication.modal_sequence.main

import com.example.navigationapplication.controller_library.container.ContainerViewModel
import com.example.navigationapplication.infrastructure_services.ElapsedRealtimeService
import com.example.navigationapplication.infrastructure_services.Logger
import com.example.navigationapplication.infrastructure_services.UUIDService
import com.example.navigationapplication.modal_sequence.controller.ModalSequenceCoordinator
import com.example.navigationapplication.modal_sequence.controller.ModalSequenceCoordinatorDelegate

object ModalSequenceCoordinatorFactory {
    fun composeModalSequenceCoordinator(
        uuidService: UUIDService,
        elapsedRealtimeService: ElapsedRealtimeService,
        delegate: ModalSequenceCoordinatorDelegate,
    ): ModalSequenceCoordinator {
        val logger = Logger(false)
        val containerViewModel = ContainerViewModel(uuidService, elapsedRealtimeService, logger)
        val composer = ModalSequenceComposer(uuidService)
        return ModalSequenceCoordinator(
            containerViewModel,
            composer,
            delegate,
        )
    }
}

