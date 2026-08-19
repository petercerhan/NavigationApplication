package com.example.navigationapplication.controller_library.container

import com.example.navigationapplication.controller_library.container.animations.ModalDismissalAnimation
import com.example.navigationapplication.controller_library.container.animations.ModalPresentationAnimation
import com.example.navigationapplication.controller_library.container.animations.BaseSceneTransitionAnimation

interface Container {
    val asContainerViewModel: ContainerViewModel
    fun showScene(scene: Scene, animation: BaseSceneTransitionAnimation)
    fun showModal(scene: Scene, animation: ModalPresentationAnimation)
    fun dismissModal(animation: ModalDismissalAnimation)
}