package com.example.navigationapplication

import android.util.Log
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.SharedFlow
import java.util.UUID

class RootContainerSystemViewModel(
    val rootContainerId: UUID,
    val serviceLocator: MutableMap<UUID, Any>,
) : ViewModel() {

    val rootContainerViewModel: RootContainerViewModel
        get() = serviceLocator[rootContainerId] as RootContainerViewModel

    val sceneFlow: SharedFlow<Scene>
        get() = rootContainerViewModel.sceneFlow
}