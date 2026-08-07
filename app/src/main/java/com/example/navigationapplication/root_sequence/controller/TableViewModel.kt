package com.example.navigationapplication.root_sequence.controller

import com.example.navigationapplication.controller_library.ApplicationViewModel
import com.example.navigationapplication.infrastructure_services.UUIDService

interface TableViewModelDelegate {
    fun next(tableViewModel: TableViewModel)
    fun back(tableViewModel: TableViewModel)
}

class TableViewModel(
    uuidService: UUIDService,
    val delegate: TableViewModelDelegate,
) : ApplicationViewModel(uuidService) {

    fun next() {
        delegate.next(this)
    }

    fun back() {
        delegate.back(this)
    }
}
