package com.example.navigationapplication

import android.util.Log
import java.util.UUID

interface HomeViewModelDelegate {
    fun next(homeViewModel: HomeViewModel)
}

class HomeViewModel(
    val id: UUID,
) {

    lateinit var delegate: HomeViewModelDelegate

    fun next() {
        delegate.next(this)
    }

    fun ping() {
        Log.d("PeterCerhan", "Ping HomeViewModel $id")
    }

}