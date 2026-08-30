package com.example.navigationapplication.modal_sequence.controller

import com.example.navigationapplication.controller_library.ApplicationViewModel
import com.example.navigationapplication.infrastructure_services.UUIDService

interface ModalPageTwoViewModelDelegate {
    fun next(modalPageTwoViewModel: ModalPageTwoViewModel)
    fun back(modalPageTwoViewModel: ModalPageTwoViewModel)
}

class ModalPageTwoViewModel(
    uuidService: UUIDService,
    val delegate: ModalPageTwoViewModelDelegate,
) : ApplicationViewModel(uuidService) {

    fun next() {
        delegate.next(this)
    }

    fun back() {
        delegate.back(this)
    }
}
