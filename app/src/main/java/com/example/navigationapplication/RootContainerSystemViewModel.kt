package com.example.navigationapplication

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.navigationapplication.controller_library.ApplicationViewModelLocator
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

class RootContainerSystemViewModel(
    val serviceLocator: ApplicationViewModelLocator,
) : ViewModel() {

    private val _transactionInProgress = MutableStateFlow(false)
    val transactionInProgress: StateFlow<Boolean> = _transactionInProgress.asStateFlow()
    var activeSceneState: SceneState? = null

    fun setTransactionInProgress(durationMilliseconds: Long) {
        _transactionInProgress.value = true
        viewModelScope.launch {
            delay(durationMilliseconds)
            _transactionInProgress.value = false
        }
    }
}