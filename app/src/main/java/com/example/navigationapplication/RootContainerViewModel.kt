package com.example.navigationapplication

import android.util.Log
import com.example.navigationapplication.controller_library.ModalDismissalAnimation
import com.example.navigationapplication.controller_library.ModalPresentationAnimation
import com.example.navigationapplication.controller_library.SceneTransitionAnimation
import java.util.UUID
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class RootContainerViewModel(
    val id: UUID,
) {
    private val _sceneFlow = MutableSharedFlow<SceneState>(replay = 1)
    val sceneFlow: SharedFlow<SceneState> = _sceneFlow.asSharedFlow()

    fun showScene(scene: Scene, animation: SceneTransitionAnimation) {
        Log.d("PETER CERHAN", "VM showScene")
        //Here we will do additional work to maintain correct SceneState
        val sceneState = SceneState(scene, animation, null, null, null)
        _sceneFlow.tryEmit(sceneState)
    }

    fun showModal(scene: Scene, animation: ModalPresentationAnimation) {
        Log.d("PETER CERHAN", "VM showModal")
        //block if there is already a modal
        val current = _sceneFlow.replayCache.firstOrNull() ?: return
        val sceneState = SceneState(current.scene, current.sceneTransitionAnimation, scene, animation, null)
        _sceneFlow.tryEmit(sceneState)
    }

    fun dismissModal(animation: ModalDismissalAnimation) {
        Log.d("PETER CERHAN", "VM dismissModal")
        //block if there is no modal
        val current = _sceneFlow.replayCache.firstOrNull() ?: return
        val sceneState = SceneState(current.scene, current.sceneTransitionAnimation, null, null, animation)
        _sceneFlow.tryEmit(sceneState)
    }

}
