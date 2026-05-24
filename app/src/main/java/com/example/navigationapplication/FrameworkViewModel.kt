package com.example.navigationapplication

import androidx.lifecycle.ViewModel
import com.example.navigationapplication.controller_library.ApplicationViewModelLocator
import java.util.UUID

class FrameworkViewModel<T>(
    val viewModelId: UUID,
    val serviceLocator: ApplicationViewModelLocator,
) : ViewModel()  {

    val viewModel: T
        get() = serviceLocator.viewModelForId(viewModelId) as T

}