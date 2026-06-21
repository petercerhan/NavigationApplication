package com.example.navigationapplication.simple_modal

import com.example.navigationapplication.controller_library.ApplicationViewModel
import com.example.navigationapplication.infrastructure_services.UUIDService
import java.util.UUID

interface SimpleModalViewModelDelegate {
    fun dismiss(simpleModalViewModel: SimpleModalViewModel)
}

class SimpleModalViewModel(
    uuidService: UUIDService,
    val delegate: SimpleModalViewModelDelegate,
) : ApplicationViewModel(uuidService) {

    fun dismiss() {
        delegate.dismiss(this)
    }
}
