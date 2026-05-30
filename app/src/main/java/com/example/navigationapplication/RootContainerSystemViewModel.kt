package com.example.navigationapplication

import android.util.Log
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.SharedFlow
import java.util.UUID

class RootContainerSystemViewModel(
    val rootContainerId: UUID,
    val serviceLocator: MutableMap<UUID, Any>,
) : ViewModel() {

    var transactionInProgress = false
    var activeSceneState: SceneState? = null

    val rootContainerViewModel: RootContainerViewModel
        get() = serviceLocator[rootContainerId] as RootContainerViewModel

    val sceneFlow: SharedFlow<SceneState>
        get() = rootContainerViewModel.sceneFlow
}