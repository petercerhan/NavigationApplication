package com.example.navigationapplication.controller_library.container

import com.example.navigationapplication.controller_library.container.animations.ModalDismissalAnimation
import com.example.navigationapplication.controller_library.container.animations.ModalPresentationAnimation
import com.example.navigationapplication.controller_library.container.animations.BaseSceneTransitionAnimation
import com.example.navigationapplication.controller_library.container.animations.ReplaceModalAnimation

interface Container {
    val asContainerViewModel: ContainerViewModel
    fun showScene(scene: Scene, animation: BaseSceneTransitionAnimation)
    fun presentModal(scene: Scene, animation: ModalPresentationAnimation)
    fun dismissModal(animation: ModalDismissalAnimation)
    fun replaceModal(scene: Scene, animation: ReplaceModalAnimation)
}