package com.example.navigationapplication

import androidx.lifecycle.ViewModel
import java.util.UUID

class FrameworkViewModel<T>(
    val viewModelId: UUID,
    val serviceLocator: MutableMap<UUID, Any>,
) : ViewModel()  {

    val viewModel: T
        get() = serviceLocator[viewModelId] as T

}