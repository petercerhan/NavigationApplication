package com.example.navigationapplication

import android.util.Log
import java.util.UUID

interface HomeViewModelDelegate {
    fun next(homeViewModel: HomeViewModel)
}

class HomeViewModel(
    id: UUID,
    val delegate: HomeViewModelDelegate,
    val homeSceneNumber: Int,
) : PlainViewModel(id) {

    fun next() {
        delegate.next(this)
    }

    fun ping() {
        Log.d("PeterCerhan", "Ping HomeViewModel $id")
    }

}