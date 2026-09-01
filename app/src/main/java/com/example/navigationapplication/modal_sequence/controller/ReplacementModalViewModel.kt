package com.example.navigationapplication.modal_sequence.controller

import com.example.navigationapplication.controller_library.ApplicationViewModel
import com.example.navigationapplication.infrastructure_services.UUIDService

interface ReplacementModalViewModelDelegate {
    fun dismiss(replacementModalViewModel: ReplacementModalViewModel)
}

class ReplacementModalViewModel(
    uuidService: UUIDService,
    val delegate: ReplacementModalViewModelDelegate,
) : ApplicationViewModel(uuidService) {

    fun dismiss() {
        delegate.dismiss(this)
    }
}
