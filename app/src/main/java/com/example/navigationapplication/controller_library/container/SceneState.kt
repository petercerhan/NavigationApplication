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
)