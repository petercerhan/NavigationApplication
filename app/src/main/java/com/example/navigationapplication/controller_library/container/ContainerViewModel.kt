package com.example.navigationapplication.controller_library.container

import com.example.navigationapplication.controller_library.ApplicationViewModel
import com.example.navigationapplication.controller_library.container.animations.ModalDismissalAnimation
import com.example.navigationapplication.controller_library.container.animations.ModalPresentationAnimation
import com.example.navigationapplication.controller_library.container.animations.BaseSceneTransitionAnimation
import com.example.navigationapplication.controller_library.container.animations.ReplaceModalAnimation
import com.example.navigationapplication.infrastructure_services.ElapsedRealtimeService
import com.example.navigationapplication.infrastructure_services.Logger
import com.example.navigationapplication.infrastructure_services.UUIDService
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

open class ContainerViewModel(
    uuidService: UUIDService,
    private val elapsedRealtimeService: ElapsedRealtimeService,
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
        if (previousState?.modalScene != null) return

        lockContainerForIncomingRequest(animation.duration)

        val sceneState = SceneState(
            uuidService.newUUID(),
            previousState,
            SceneStateTransitionType.TransitionBaseScene,
            scene,
            animation,
            null, null,
            null, null
        )
        _sceneFlow.tryEmit(sceneState)
    }

    override fun presentModal(scene: Scene, animation: ModalPresentationAnimation) {
        if (containerIsLockedForExistingRequest()) return
        val previousState = _sceneFlow.replayCache.firstOrNull() ?: return
        if (previousState.modalScene != null) return

        lockContainerForIncomingRequest(animation.duration)
        val sceneState =
            SceneState(
                uuidService.newUUID(),
                previousState,
                SceneStateTransitionType.PresentModal,
                previousState.baseScene,
                null,
                scene,
                animation,
                null,
                null
            )
        _sceneFlow.tryEmit(sceneState)
    }

    override fun dismissModal(animation: ModalDismissalAnimation) {
        if (containerIsLockedForExistingRequest()) return
        val previousState = _sceneFlow.replayCache.firstOrNull() ?: return
        if (previousState.modalScene == null) return

        lockContainerForIncomingRequest(animation.duration)
        val sceneState =
            SceneState(
                uuidService.newUUID(),
                previousState,
                SceneStateTransitionType.DismissModal,
                previousState.baseScene,
                null,
                null,
                null,
                animation,
                null
            )
        _sceneFlow.tryEmit(sceneState)
    }

    override fun replaceModal(scene: Scene, animation: ReplaceModalAnimation) {
        if (containerIsLockedForExistingRequest()) return
        val previousState = _sceneFlow.replayCache.firstOrNull() ?: return
        if (previousState.modalScene == null) return

        lockContainerForIncomingRequest(animation.duration)
        val sceneState =
            SceneState(
                uuidService.newUUID(),
                previousState,
                SceneStateTransitionType.ReplaceModal,
                previousState.baseScene,
                null,
                scene,
                null,
                null,
                animation
            )
        _sceneFlow.tryEmit(sceneState)
    }

    private fun containerIsLockedForExistingRequest(): Boolean {
        if (elapsedRealtimeService.elapsedRealtime() < acceptRequestsAfterElapsedRealtimeMs) {
            logger.log("Reject Container request: transition in progress")
            return true
        }
        return false
    }

    private fun lockContainerForIncomingRequest(durationMilliseconds: Long) {
        acceptRequestsAfterElapsedRealtimeMs =
            elapsedRealtimeService.elapsedRealtime() + durationMilliseconds + 100L
    }

}
