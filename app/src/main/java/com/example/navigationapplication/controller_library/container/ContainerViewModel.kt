package com.example.navigationapplication.controller_library.container

import android.os.SystemClock
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

    private var acceptRequestsAfterElapsedRealtimeMs = 0L

    //Container Interface

    override val asContainerViewModel: ContainerViewModel
        get() = this

    override fun showScene(scene: Scene, animation: BaseSceneTransitionAnimation) {
        if (containerIsLockedForExistingRequest()) return
        val previousState = _sceneFlow.replayCache.firstOrNull()

        lockContainerForIncomingRequest(animation.duration)

        //Here we will do additional work to maintain correct SceneState
        val sceneState = SceneState(
            uuidService.newUUID(),
            previousState?.id,
            SceneStateTransitionType.TransitionBaseScene,
            scene,
            animation,
            null, null,
            null
        )
        _sceneFlow.tryEmit(sceneState)
    }

    override fun showModal(scene: Scene, animation: ModalPresentationAnimation) {
        if (containerIsLockedForExistingRequest()) return
        val previousState = _sceneFlow.replayCache.firstOrNull() ?: return

        //block if there is already a modal

        lockContainerForIncomingRequest(animation.duration)
        val sceneState =
            SceneState(
                uuidService.newUUID(),
                previousState?.id,
                SceneStateTransitionType.PresentModal,
                previousState.baseScene,
                null,
                scene,
                animation,
                null
            )
        _sceneFlow.tryEmit(sceneState)
    }

    override fun dismissModal(animation: ModalDismissalAnimation) {
        if (containerIsLockedForExistingRequest()) return
        val previousState = _sceneFlow.replayCache.firstOrNull() ?: return

        //block if there is no modal

        lockContainerForIncomingRequest(animation.duration)
        val sceneState =
            SceneState(
                uuidService.newUUID(),
                previousState.id,
                SceneStateTransitionType.DismissModal,
                previousState.baseScene,
                null,
                null,
                null,
                animation
            )
        _sceneFlow.tryEmit(sceneState)
    }

    private fun containerIsLockedForExistingRequest(): Boolean {
        if (SystemClock.elapsedRealtime() < acceptRequestsAfterElapsedRealtimeMs) {
            logger.log("Reject Container request: transition in progress")
            return true
        }
        return false
    }

    private fun lockContainerForIncomingRequest(durationMilliseconds: Long) {
        acceptRequestsAfterElapsedRealtimeMs =
            SystemClock.elapsedRealtime() + durationMilliseconds + 100L
    }

}
