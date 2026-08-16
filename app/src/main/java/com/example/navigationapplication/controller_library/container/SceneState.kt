package com.example.navigationapplication.controller_library.container

import com.example.navigationapplication.controller_library.container.animations.ModalDismissalAnimation
import com.example.navigationapplication.controller_library.container.animations.ModalPresentationAnimation
import com.example.navigationapplication.controller_library.container.animations.SceneTransitionAnimation

class SceneState(
    val scene: Scene,
    val sceneTransitionAnimation: SceneTransitionAnimation,
    val modalScene: Scene?,
    val modalPresentationAnimation: ModalPresentationAnimation?,
    val modalDismissalAnimation: ModalDismissalAnimation?,
) {
    fun hasIdenticalScenesAs(other: SceneState): Boolean {
        return (scene.viewModel.id == other.scene.viewModel.id) &&
                (modalScene?.viewModel?.id == other.modalScene?.viewModel?.id)
    }
}