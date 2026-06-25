package com.example.navigationapplication

import com.example.navigationapplication.container.Container
import com.example.navigationapplication.home.HomeFragment
import com.example.navigationapplication.home.HomeViewModel
import com.example.navigationapplication.home.HomeViewModelDelegate
import com.example.navigationapplication.page_two.PageTwoFragment
import com.example.navigationapplication.page_two.PageTwoViewModel
import com.example.navigationapplication.page_two.PageTwoViewModelDelegate
import com.example.navigationapplication.controller_library.ModalDismissalAnimation
import com.example.navigationapplication.controller_library.ModalPresentationAnimation
import com.example.navigationapplication.controller_library.SceneTransitionAnimation
import com.example.navigationapplication.modal_sequence.ModalSequenceCoordinator
import com.example.navigationapplication.modal_sequence.ModalSequenceCoordinatorDelegate
import com.example.navigationapplication.container.ContainerFragment
import com.example.navigationapplication.container.ContainerViewModel
import com.example.navigationapplication.container.Scene
import com.example.navigationapplication.controller_library.Coordinator
import com.example.navigationapplication.modal_sequence.ModalSequenceCoordinatorFactory
import com.example.navigationapplication.simple_modal.SimpleModalViewModel
import com.example.navigationapplication.simple_modal.SimpleModalViewModelDelegate

class RootCoordinator(
    val container: Container,
    val composer: RootSequenceComposer,
): Coordinator, PageTwoViewModelDelegate, HomeViewModelDelegate, SimpleModalViewModelDelegate, ModalSequenceCoordinatorDelegate {

    private var childCoordinator: Coordinator? = null

    override val containerViewModel: ContainerViewModel
        get() = container.asContainerViewModel

    init {
        val scene = getHomeScene()
        container.showScene(scene, SceneTransitionAnimation.NoAnimation)
    }

    //Routing

    private fun getHomeScene(): Scene {
        return composer.composeHomeScene(this)
    }

    //HomeViewModelDelegate

    override fun next(homeViewModel: HomeViewModel) {
        val scene = composer.composePageTwoScene(this)
        container.showScene(scene, SceneTransitionAnimation.SlideFromRight)
    }

    override fun back(homeViewModel: HomeViewModel) {
        //do nothing
    }

    //PageTwoViewModelDelegate

    override fun next(pageTwoViewModel: PageTwoViewModel) {
        val coordinator = composer.composeModalSequenceCoordinator(this)
        childCoordinator = coordinator
        val scene = Scene(viewModel = coordinator.containerViewModel, fragmentType = ContainerFragment::class,)
        container.showModal(scene, ModalPresentationAnimation.CoverFromBottom)
    }

    override fun back(pageTwoViewModel: PageTwoViewModel) {
        val scene = getHomeScene()
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