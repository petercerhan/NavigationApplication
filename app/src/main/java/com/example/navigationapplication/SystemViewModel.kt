package com.example.navigationapplication

import androidx.lifecycle.ViewModel
import java.util.UUID

class SystemViewModel<T>(
    val viewModelId: UUID,
    val serviceLocator: MutableMap<UUID, Any>,
) : ViewModel()  {

    val viewModel: T
        get() = serviceLocator[viewModelId] as T

}