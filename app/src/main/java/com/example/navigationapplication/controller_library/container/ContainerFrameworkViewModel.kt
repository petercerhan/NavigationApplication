package com.example.navigationapplication.controller_library.container

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ContainerFrameworkViewModel: ViewModel() {
    var activeSceneState: SceneState? = null

    private val _transactionInProgress = MutableStateFlow(false)
    val transactionInProgress: StateFlow<Boolean> = _transactionInProgress.asStateFlow()

    fun setTransactionInProgress(durationMilliseconds: Long) {
        _transactionInProgress.value = true
        viewModelScope.launch {
            delay(durationMilliseconds)
            _transactionInProgress.value = false
        }
    }
}