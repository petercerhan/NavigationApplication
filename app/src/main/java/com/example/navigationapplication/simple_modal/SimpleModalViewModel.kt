package com.example.navigationapplication.simple_modal

import com.example.navigationapplication.controller_library.ApplicationViewModel
import java.util.UUID

interface SimpleModalViewModelDelegate {
    fun dismiss(simpleModalViewModel: SimpleModalViewModel)
}

class SimpleModalViewModel(
    id: UUID,
    val delegate: SimpleModalViewModelDelegate,
) : ApplicationViewModel(id) {

    fun dismiss() {
        delegate.dismiss(this)
    }
}
