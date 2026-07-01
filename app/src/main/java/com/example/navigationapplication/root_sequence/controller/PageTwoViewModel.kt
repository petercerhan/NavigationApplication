package com.example.navigationapplication.root_sequence.controller

import com.example.navigationapplication.controller_library.ApplicationViewModel
import com.example.navigationapplication.infrastructure_services.UUIDService

interface PageTwoViewModelDelegate {
    fun next(pageTwoViewModel: PageTwoViewModel)
    fun back(pageTwoViewModel: PageTwoViewModel)
}

class PageTwoViewModel(
    uuidService: UUIDService,
    val delegate: PageTwoViewModelDelegate,
) : ApplicationViewModel(uuidService) {

    fun next() {
        delegate.next(this)
    }

    fun back() {
        delegate.back(this)
    }
}