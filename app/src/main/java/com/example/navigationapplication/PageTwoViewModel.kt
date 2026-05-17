package com.example.navigationapplication

import android.util.Log
import java.util.UUID

interface PageTwoViewModelDelegate {
    fun next(pageTwoViewModel: PageTwoViewModel)
}

class PageTwoViewModel(
    val id: UUID,
    val delegate: PageTwoViewModelDelegate,
)  {

    fun next() {
        delegate.next(this)
    }

    fun ping() {
        Log.d("PeterCerhan", "Ping PageTwoViewModel $id")
    }
}