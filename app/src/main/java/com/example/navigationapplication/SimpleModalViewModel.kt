package com.example.navigationapplication

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
