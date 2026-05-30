package com.example.navigationapplication

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

class RootContainerSystemViewModel(
    val rootContainerId: UUID,
    val serviceLocator: MutableMap<UUID, Any>,
) : ViewModel() {

    private val _transactionInProgress = MutableStateFlow(false)
    val transactionInProgress: StateFlow<Boolean> = _transactionInProgress.asStateFlow()
    var activeSceneState: SceneState? = null

    val rootContainerViewModel: RootContainerViewModel
        get() = serviceLocator[rootContainerId] as RootContainerViewModel

    val sceneFlow: SharedFlow<SceneState>
        get() = rootContainerViewModel.sceneFlow

    fun setTransactionInProgress() {
        _transactionInProgress.value = true
        viewModelScope.launch {
            delay(2000)
            _transactionInProgress.value = false
        }
    }
}