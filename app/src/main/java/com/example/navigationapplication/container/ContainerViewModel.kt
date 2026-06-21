package com.example.navigationapplication.container

import com.example.navigationapplication.controller_library.ApplicationViewModel
import com.example.navigationapplication.controller_library.ModalDismissalAnimation
import com.example.navigationapplication.controller_library.ModalPresentationAnimation
import com.example.navigationapplication.controller_library.SceneTransitionAnimation
import com.example.navigationapplication.infrastructure_services.Logger
import java.util.UUID
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class ContainerViewModel(
    id: UUID,
    val logger: Logger
): ApplicationViewModel(id), Container {

    //Internal Mechanics - Interface for Container Fragment

    private val _sceneFlow = MutableSharedFlow<SceneState>(replay = 1)
    val sceneFlow: SharedFlow<SceneState> = _sceneFlow.asSharedFlow()

    //Container Interface

    override fun showScene(scene: Scene, animation: SceneTransitionAnimation) {
        logger.log("VM showScene")
        //Here we will do additional work to maintain correct SceneState
        val sceneState = SceneState(scene, animation, null, null, null)
        _sceneFlow.tryEmit(sceneState)
    }

    override fun showModal(scene: Scene, animation: ModalPresentationAnimation) {
        logger.log("VM showModal")
        //block if there is already a modal
        val current = _sceneFlow.replayCache.firstOrNull() ?: return
        val sceneState =
            SceneState(current.scene, current.sceneTransitionAnimation, scene, animation, null)
        _sceneFlow.tryEmit(sceneState)
    }

    override fun dismissModal(animation: ModalDismissalAnimation) {
        logger.log("VM dismissModal")
        //block if there is no modal
        val current = _sceneFlow.replayCache.firstOrNull() ?: return
        val sceneState =
            SceneState(current.scene, current.sceneTransitionAnimation, null, null, animation)
        _sceneFlow.tryEmit(sceneState)
    }

}
