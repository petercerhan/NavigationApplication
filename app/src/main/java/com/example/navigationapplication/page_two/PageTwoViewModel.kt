package com.example.navigationapplication.page_two

import android.util.Log
import com.example.navigationapplication.controller_library.ApplicationViewModel
import com.example.navigationapplication.infrastructure_services.UUIDService
import java.util.UUID

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