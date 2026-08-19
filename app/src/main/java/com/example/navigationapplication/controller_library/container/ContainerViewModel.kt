package com.example.navigationapplication.controller_library.container

import com.example.navigationapplication.controller_library.ApplicationViewModel
import com.example.navigationapplication.controller_library.container.animations.ModalDismissalAnimation
import com.example.navigationapplication.controller_library.container.animations.ModalPresentationAnimation
import com.example.navigationapplication.controller_library.container.animations.BaseSceneTransitionAnimation
import com.example.navigationapplication.infrastructure_services.Logger
import com.example.navigationapplication.infrastructure_services.UUIDService
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class ContainerViewModel(
    uuidService: UUIDService,
    val logger: Logger
): ApplicationViewModel(uuidService), Container {

    //Internal Mechanics - Interface for Container Fragment

    private val _sceneFlow = MutableSharedFlow<SceneState>(replay = 1)
    val sceneStateFlow: SharedFlow<SceneState> = _sceneFlow.asSharedFlow()

    //Container Interface

    override val asContainerViewModel: ContainerViewModel
        get() = this

    override fun showScene(scene: Scene, animation: BaseSceneTransitionAnimation) {
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
            SceneState(current.baseScene, current.baseSceneTransitionAnimation, scene, animation, null)
        _sceneFlow.tryEmit(sceneState)
    }

    override fun dismissModal(animation: ModalDismissalAnimation) {
        logger.log("VM dismissModal")
        //block if there is no modal
        val current = _sceneFlow.replayCache.firstOrNull() ?: return
        val sceneState =
            SceneState(current.baseScene, current.baseSceneTransitionAnimation, null, null, animation)
        _sceneFlow.tryEmit(sceneState)
    }

}
