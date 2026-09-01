package com.example.navigationapplication.modal_sequence.controller

import com.example.navigationapplication.controller_library.container.Container
import com.example.navigationapplication.controller_library.container.animations.ModalDismissalAnimation
import com.example.navigationapplication.controller_library.container.animations.ModalPresentationAnimation
import com.example.navigationapplication.controller_library.container.animations.BaseSceneTransitionAnimation
import com.example.navigationapplication.controller_library.container.animations.ReplaceModalAnimation
import com.example.navigationapplication.root_sequence.controller.HomeViewModel
import com.example.navigationapplication.root_sequence.controller.HomeViewModelDelegate
import com.example.navigationapplication.controller_library.container.ContainerViewModel
import com.example.navigationapplication.controller_library.Coordinator
import com.example.navigationapplication.controller_library.NavigableContainer.NavigableContainerFragment
import com.example.navigationapplication.controller_library.NavigableContainer.NavigableContainerViewModel
import com.example.navigationapplication.controller_library.NavigableContainer.NavigableContainerViewModelDelegate
import com.example.navigationapplication.controller_library.container.Scene
import com.example.navigationapplication.modal_sequence.main.ModalSequenceComposer


interface ModalSequenceCoordinatorDelegate {
    fun back(modalSequenceCoordinator: ModalSequenceCoordinator)
}

class ModalSequenceCoordinator(
    val container: Container,
    val composer: ModalSequenceComposer,
    val delegate: ModalSequenceCoordinatorDelegate,
): Coordinator, HomeViewModelDelegate, ModalPageTwoViewModelDelegate, SimpleModalViewModelDelegate,
    ReplacementModalViewModelDelegate, NavigableContainerViewModelDelegate {

    override val containerViewModel: ContainerViewModel
        get() = container.asContainerViewModel

    override val containerScene: Scene
        get() = Scene(
            viewModel = containerViewModel,
            fragmentType = NavigableContainerFragment::class,
        )

    init {
        (container as? NavigableContainerViewModel)?.setDelegate(this)
        val scene = composer.composeHomeScene(this)
        container.showScene(scene, BaseSceneTransitionAnimation.NoAnimation)
    }

    //HomeViewModelDelegate

    override fun next(homeViewModel: HomeViewModel) {
        val scene = composer.composeModalPageTwoScene(this)
        container.showScene(scene, BaseSceneTransitionAnimation.SlideFromRight)
    }

    override fun back(homeViewModel: HomeViewModel) {
        delegate.back(this)
    }

    //ModalPageTwoViewModelDelegate

    override fun next(modalPageTwoViewModel: ModalPageTwoViewModel) {
        val scene = composer.composeSimpleModalScene(this)
        container.presentModal(scene, ModalPresentationAnimation.CoverFromBottom)
    }

    override fun back(modalPageTwoViewModel: ModalPageTwoViewModel) {
        val scene = composer.composeHomeScene(this)
        container.showScene(scene, BaseSceneTransitionAnimation.SlideFromLeft)
    }

    //SimpleModalViewModelDelegate

    override fun dismiss(simpleModalViewModel: SimpleModalViewModel) {
        container.dismissModal(ModalDismissalAnimation.UncoverDown)
    }

    override fun replaceModal(simpleModalViewModel: SimpleModalViewModel) {
        val scene = composer.composeReplacementModalScene(this)
        container.replaceModal(scene, ReplaceModalAnimation.SlideFromRight)
    }

    //ReplacementModalViewModelDelegate

    override fun dismiss(replacementModalViewModel: ReplacementModalViewModel) {
        container.dismissModal(ModalDismissalAnimation.UncoverDown)
    }

    //NavigableContainerViewModelDelegate

    override fun quit(navigableContainerViewModel: NavigableContainerViewModel) {
        delegate.back(this)
    }

}