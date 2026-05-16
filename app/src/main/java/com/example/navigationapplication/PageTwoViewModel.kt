package com.example.navigationapplication

import android.util.Log
import java.util.UUID

class PageTwoViewModel(
    val id: UUID,
)  {

    lateinit var delegate: RootCoordinator

    fun ping() {
        Log.d("PeterCerhan", "Ping PageTwoViewModel $id")
    }
}