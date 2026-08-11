package com.example.navigationapplication.modal_sequence.controller

import com.example.navigationapplication.controller_library.container.Container
import com.example.navigationapplication.controller_library.container.animations.ModalDismissalAnimation
import com.example.navigationapplication.controller_library.container.animations.ModalPresentationAnimation
import com.example.navigationapplication.controller_library.container.animations.SceneTransitionAnimation
import com.example.navigationapplication.root_sequence.controller.HomeViewModel
import com.example.navigationapplication.root_sequence.controller.HomeViewModelDelegate
import com.example.navigationapplication.root_sequence.controller.PageTwoViewModel
import com.example.navigationapplication.root_sequence.controller.PageTwoViewModelDelegate
import com.example.navigationapplication.controller_library.container.ContainerViewModel
import com.example.navigationapplication.controller_library.Coordinator
import com.example.navigationapplication.controller_library.container.ContainerFragment
import com.example.navigationapplication.controller_library.container.Scene
import com.example.navigationapplication.modal_sequence.main.ModalSequenceComposer


interface ModalSequenceCoordinatorDelegate {
    fun back(modalSequenceCoordinator: ModalSequenceCoordinator)
}

class ModalSequenceCoordinator(
    val container: Container,
    val composer: ModalSequenceComposer,
    val delegate: ModalSequenceCoordinatorDelegate,
): Coordinator, HomeViewModelDelegate, PageTwoViewModelDelegate, SimpleModalViewModelDelegate {

    override val containerViewModel: ContainerViewModel
        get() = container.asContainerViewModel

    override val containerScene: Scene
        get() = Scene(
            viewModel = containerViewModel,
            fragmentType = ContainerFragment::class,
        )

    init {
        val scene = composer.composeHomeScene(this)
        container.showScene(scene, SceneTransitionAnimation.NoAnimation)
    }

    //HomeViewModelDelegate

    override fun next(homeViewModel: HomeViewModel) {
        val scene = composer.composePageTwoScene(this)
        container.showScene(scene, SceneTransitionAnimation.SlideFromRight)
    }

    override fun back(homeViewModel: HomeViewModel) {
        delegate.back(this)
    }

    //PageTwoViewModelDelegate

    override fun next(pageTwoViewModel: PageTwoViewModel) {
        val scene = composer.composeSimpleModalScene(this)
        container.showModal(scene, ModalPresentationAnimation.CoverFromBottom)
    }

    override fun back(pageTwoViewModel: PageTwoViewModel) {
        val scene = composer.composeHomeScene(this)
        container.showScene(scene, SceneTransitionAnimation.SlideFromLeft)
    }

    //SimpleModalViewModelDelegate

    override fun dismiss(simpleModalViewModel: SimpleModalViewModel) {
        container.dismissModal(ModalDismissalAnimation.UncoverDown)
    }

}