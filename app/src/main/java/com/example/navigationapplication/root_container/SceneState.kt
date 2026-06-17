package com.example.navigationapplication.root_container

import com.example.navigationapplication.controller_library.ModalDismissalAnimation
import com.example.navigationapplication.controller_library.ModalPresentationAnimation
import com.example.navigationapplication.controller_library.SceneTransitionAnimation

class SceneState(
    val scene: Scene,
    val sceneTransitionAnimation: SceneTransitionAnimation,
    val modalScene: Scene?,
    val modalPresentationAnimation: ModalPresentationAnimation?,
    val modalDismissalAnimation: ModalDismissalAnimation?,
)