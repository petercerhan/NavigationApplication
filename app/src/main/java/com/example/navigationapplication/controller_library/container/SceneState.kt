package com.example.navigationapplication.controller_library.container

import com.example.navigationapplication.controller_library.container.animations.ModalDismissalAnimation
import com.example.navigationapplication.controller_library.container.animations.ModalPresentationAnimation
import com.example.navigationapplication.controller_library.container.animations.BaseSceneTransitionAnimation

class SceneState(
    val baseScene: Scene,
    val baseSceneTransitionAnimation: BaseSceneTransitionAnimation,
    val modalScene: Scene?,
    val modalPresentationAnimation: ModalPresentationAnimation?,
    val modalDismissalAnimation: ModalDismissalAnimation?,
) {
    fun hasIdenticalScenesAs(other: SceneState): Boolean {
        return (baseScene.viewModel.id == other.baseScene.viewModel.id) &&
                (modalScene?.viewModel?.id == other.modalScene?.viewModel?.id)
    }
}