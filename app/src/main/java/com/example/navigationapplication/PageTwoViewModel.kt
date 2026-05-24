package com.example.navigationapplication

import android.util.Log
import java.util.UUID

interface PageTwoViewModelDelegate {
    fun next(pageTwoViewModel: PageTwoViewModel)
    fun back(pageTwoViewModel: PageTwoViewModel)
}

class PageTwoViewModel(
    id: UUID,
    val delegate: PageTwoViewModelDelegate,
) : PlainViewModel(id) {

    fun next() {
        delegate.next(this)
    }

    fun back() {
        delegate.back(this)
    }

    fun ping() {
        Log.d("PeterCerhan", "Ping PageTwoViewModel $id")
    }
}