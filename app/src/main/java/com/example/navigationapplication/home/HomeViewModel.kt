package com.example.navigationapplication.home

import android.util.Log
import com.example.navigationapplication.controller_library.ApplicationViewModel
import java.util.UUID

interface HomeViewModelDelegate {
    fun next(homeViewModel: HomeViewModel)
    fun back(homeViewModel: HomeViewModel)
}

class HomeViewModel(
    id: UUID,
    val delegate: HomeViewModelDelegate,
) : ApplicationViewModel(id) {

    fun next() {
        delegate.next(this)
    }

    fun back() {
        delegate.back(this)
    }

}