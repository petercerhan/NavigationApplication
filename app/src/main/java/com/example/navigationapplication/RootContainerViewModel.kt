package com.example.navigationapplication

import java.util.UUID
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class RootContainerViewModel(
    val id: UUID,
) {
    private val _sceneFlow = MutableSharedFlow<Scene>(replay = 1)
    val sceneFlow: SharedFlow<Scene> = _sceneFlow.asSharedFlow()

    fun showScene(scene: Scene) {
        _sceneFlow.tryEmit(scene)
    }

}
