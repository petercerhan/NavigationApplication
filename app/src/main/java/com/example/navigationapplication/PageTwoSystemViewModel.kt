package com.example.navigationapplication

import android.util.Log
import androidx.lifecycle.ViewModel
import java.util.UUID

class PageTwoSystemViewModel(
    val viewModelId: UUID,
    val serviceLocator: MutableMap<UUID, Any>,
) : ViewModel() {

    val viewModel: PageTwoViewModel
        get() = serviceLocator[viewModelId] as PageTwoViewModel

    fun ping() {
        Log.d("PeterCerhan", "Ping PageTwoSystemViewModel $viewModelId")
    }
}
