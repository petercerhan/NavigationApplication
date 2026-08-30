package com.example.navigationapplication.controller_library.NavigableContainer

import com.example.navigationapplication.controller_library.container.ContainerViewModel
import com.example.navigationapplication.infrastructure_services.ElapsedRealtimeService
import com.example.navigationapplication.infrastructure_services.Logger
import com.example.navigationapplication.infrastructure_services.UUIDService

interface NavigableContainerViewModelDelegate {
    fun quit(navigableContainerViewModel: NavigableContainerViewModel)
}

class NavigableContainerViewModel(
    uuidService: UUIDService,
    elapsedRealtimeService: ElapsedRealtimeService,
    logger: Logger,
) : ContainerViewModel(uuidService, elapsedRealtimeService, logger) {

    private var delegate: NavigableContainerViewModelDelegate? = null

    fun setDelegate(delegate: NavigableContainerViewModelDelegate) {
        this.delegate = delegate
    }

    fun quit() {
        delegate?.quit(this)
    }

}
