package com.example.navigationapplication.controller_library.container

import com.example.navigationapplication.controller_library.container.animations.ModalDismissalAnimation
import com.example.navigationapplication.controller_library.container.animations.ModalPresentationAnimation
import com.example.navigationapplication.controller_library.container.animations.BaseSceneTransitionAnimation
import com.example.navigationapplication.controller_library.container.animations.ReplaceModalAnimation
import java.util.UUID

class SceneState(
    val id: UUID,
    val previousState: SceneState?,

    val transitionType: SceneStateTransitionType,
    val baseScene: Scene,
    val baseSceneTransitionAnimation: BaseSceneTransitionAnimation?,
    val modalScene: Scene?,
    val modalPresentationAnimation: ModalPresentationAnimation?,
    val modalDismissalAnimation: ModalDismissalAnimation?,
    val replaceModalAnimation: ReplaceModalAnimation?,
)
