package com.example.navigationapplication.modal_sequence.controller

import com.example.navigationapplication.controller_library.ApplicationViewModel
import com.example.navigationapplication.infrastructure_services.UUIDService

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
