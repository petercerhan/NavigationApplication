package com.example.navigationapplication

import android.util.Log
import java.util.UUID

class HomeViewModel(
    val id: UUID,
) {

    lateinit var delegate: RootContainer

    fun next() {
        delegate.next()
    }

    fun ping() {
        Log.d("PeterCerhan", "Ping HomeViewModel $id")
    }

}