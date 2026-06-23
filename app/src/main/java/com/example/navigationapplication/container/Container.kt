package com.example.navigationapplication.container

import com.example.navigationapplication.controller_library.ModalDismissalAnimation
import com.example.navigationapplication.controller_library.ModalPresentationAnimation
import com.example.navigationapplication.controller_library.SceneTransitionAnimation

interface Container {
    val asContainerViewModel: ContainerViewModel
    fun showScene(scene: Scene, animation: SceneTransitionAnimation)
    fun showModal(scene: Scene, animation: ModalPresentationAnimation)
    fun dismissModal(animation: ModalDismissalAnimation)
}