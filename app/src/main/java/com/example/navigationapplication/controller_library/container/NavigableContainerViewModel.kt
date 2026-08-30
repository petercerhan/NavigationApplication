package com.example.navigationapplication.controller_library.container

import android.util.Log
import com.example.navigationapplication.infrastructure_services.ElapsedRealtimeService
import com.example.navigationapplication.infrastructure_services.Logger
import com.example.navigationapplication.infrastructure_services.UUIDService

class NavigableContainerViewModel(
    uuidService: UUIDService,
    elapsedRealtimeService: ElapsedRealtimeService,
    logger: Logger,
) : ContainerViewModel(uuidService, elapsedRealtimeService, logger) {

    fun quit() {
        Log.d("Development Logger", "container subclass quit")
    }

}
