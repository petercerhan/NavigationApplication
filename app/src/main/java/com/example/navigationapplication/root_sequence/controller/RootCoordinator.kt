package com.example.navigationapplication.root_sequence.controller

import com.example.navigationapplication.controller_library.Coordinator
import com.example.navigationapplication.controller_library.container.Container
import com.example.navigationapplication.controller_library.container.ContainerFragment
import com.example.navigationapplication.controller_library.container.ContainerViewModel
import com.example.navigationapplication.controller_library.container.Scene
import com.example.navigationapplication.controller_library.container.animations.ModalDismissalAnimation
import com.example.navigationapplication.controller_library.container.animations.ModalPresentationAnimation
import com.example.navigationapplication.controller_library.container.animations.SceneTransitionAnimation
import com.example.navigationapplication.modal_sequence.controller.ModalSequenceCoordinator
import com.example.navigationapplication.modal_sequence.controller.ModalSequenceCoordinatorDelegate
import com.example.navigationapplication.root_sequence.main.RootSequenceComposer
import com.example.navigationapplication.modal_sequence.controller.SimpleModalViewModel
import com.example.navigationapplication.modal_sequence.controller.SimpleModalViewModelDelegate

class RootCoordinator(
    val container: Container,
    val composer: RootSequenceComposer,
): Coordinator, PageTwoViewModelDelegate, TableViewModelDelegate, HomeViewModelDelegate,
    SimpleModalViewModelDelegate, ModalSequenceCoordinatorDelegate {

    private var childCoordinator: Coordinator? = null

    override val containerViewModel: ContainerViewModel
        get() = container.asContainerViewModel

    init {
        val scene = composer.composeHomeScene(this)
        container.showScene(scene, SceneTransitionAnimation.NoAnimation)
    }

    //HomeViewModelDelegate

    override fun next(homeViewModel: HomeViewModel) {
        val scene = composer.composeTableScene(this)
        container.showScene(scene, SceneTransitionAnimation.SlideFromRight)
    }

    override fun back(homeViewModel: HomeViewModel) {
        //do nothing
    }

    //TableViewModelDelegate

    override fun next(tableViewModel: TableViewModel) {
        val scene = composer.composePageTwoScene(this)
        container.showScene(scene, SceneTransitionAnimation.SlideFromRight)
    }

    override fun back(tableViewModel: TableViewModel) {
        val scene = composer.composeHomeScene(this)
        container.showScene(scene, SceneTransitionAnimation.SlideFromLeft)
    }

    //PageTwoViewModelDelegate

    override fun next(pageTwoViewModel: PageTwoViewModel) {
        val coordinator = composer.composeModalSequenceCoordinator(this)
        childCoordinator = coordinator
        val scene = Scene(
            viewModel = coordinator.containerViewModel,
            fragmentType = ContainerFragment::class,
        )
        container.showModal(scene, ModalPresentationAnimation.CoverFromBottom)
    }

    override fun back(pageTwoViewModel: PageTwoViewModel) {
        val scene = composer.composeTableScene(this)
        container.showScene(scene, SceneTransitionAnimation.SlideFromLeft)
        childCoordinator = null
    }

    //SimpleModalViewModelDelegate

    override fun dismiss(simpleModalViewModel: SimpleModalViewModel) {
        container.dismissModal(ModalDismissalAnimation.UncoverDown)
    }

    //ModalSequenceCoordinatorDelegate

    override fun back(modalSequenceCoordinator: ModalSequenceCoordinator) {
        container.dismissModal(ModalDismissalAnimation.UncoverDown)
    }

}