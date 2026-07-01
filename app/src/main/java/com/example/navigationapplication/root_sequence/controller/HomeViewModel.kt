package com.example.navigationapplication.root_sequence.controller

import com.example.navigationapplication.controller_library.ApplicationViewModel
import com.example.navigationapplication.infrastructure_services.UUIDService

interface HomeViewModelDelegate {
    fun next(homeViewModel: HomeViewModel)
    fun back(homeViewModel: HomeViewModel)
}

class HomeViewModel(
    uuidService: UUIDService,
    val delegate: HomeViewModelDelegate,
) : ApplicationViewModel(uuidService) {

    fun next() {
        delegate.next(this)
    }

    fun back() {
        delegate.back(this)
    }

}