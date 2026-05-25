package com.example.navigationapplication

import com.example.navigationapplication.controller_library.SceneAnimation
import java.util.UUID
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class RootContainerViewModel(
    val id: UUID,
) {
    private val _sceneFlow = MutableSharedFlow<SceneState>(replay = 1)
    val sceneFlow: SharedFlow<SceneState> = _sceneFlow.asSharedFlow()

    fun showScene(scene: Scene, animation: SceneAnimation) {
        //Here we will do additional work to maintain correct SceneState
        val sceneState = SceneState(scene, animation, null)
        _sceneFlow.tryEmit(sceneState)
    }

    fun showModal(scene: Scene) {
        //block if there is already a modal
        val current = _sceneFlow.replayCache.firstOrNull() ?: return
        val sceneState = SceneState(current.scene, current.animation, scene)
        _sceneFlow.tryEmit(sceneState)
    }

    fun dismissModal() {
        //block if there is no modal
        val current = _sceneFlow.replayCache.firstOrNull() ?: return
        val sceneState = SceneState(current.scene, current.animation, null)
        _sceneFlow.tryEmit(sceneState)
    }

}
