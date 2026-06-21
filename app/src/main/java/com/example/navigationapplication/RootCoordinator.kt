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
import com.example.navigationapplication.infrastructure_services.UUIDService
import com.example.navigationapplication.modal_sequence.ModalSequenceCoordinator
import com.example.navigationapplication.modal_sequence.ModalSequenceCoordinatorDelegate
import com.example.navigationapplication.container.ContainerFragment
import com.example.navigationapplication.container.ContainerViewModel
import com.example.navigationapplication.container.Scene
import com.example.navigationapplication.infrastructure_services.Logger
import com.example.navigationapplication.simple_modal.SimpleModalViewModel
import com.example.navigationapplication.simple_modal.SimpleModalViewModelDelegate

class RootCoordinator(
    val container: Container,
    val uuidService: UUIDService,
): PageTwoViewModelDelegate, HomeViewModelDelegate, SimpleModalViewModelDelegate, ModalSequenceCoordinatorDelegate {

    private var homeSceneCache: Scene? = null

    fun start() {
        val scene = getHomeScene()
        container.showScene(scene, SceneTransitionAnimation.NoAnimation)
    }

    //Routing

    private fun getHomeScene(): Scene {
        homeSceneCache?.let { return it }

        val homeViewModel = HomeViewModel(uuidService, this)
        val scene = Scene(viewModel = homeViewModel, fragmentType = HomeFragment::class,)
        homeSceneCache = scene
        return scene
    }

    //HomeViewModelDelegate

    override fun next(homeViewModel: HomeViewModel) {
        val pageTwoViewModel = PageTwoViewModel(uuidService, this)

        val scene = Scene(viewModel = pageTwoViewModel, fragmentType = PageTwoFragment::class,)
        container.showScene(scene, SceneTransitionAnimation.SlideFromRight)
    }

    override fun back(homeViewModel: HomeViewModel) {
        //do nothing
    }

    //PageTwoViewModelDelegate

    override fun next(pageTwoViewModel: PageTwoViewModel) {
        val logger = Logger(false)

        val newContainerViewModel = ContainerViewModel(uuidService, logger)
        val coordinator = ModalSequenceCoordinator(newContainerViewModel, uuidService, this)
        coordinator.start()
        val scene = Scene(viewModel = newContainerViewModel, fragmentType = ContainerFragment::class,)
        container.showModal(scene, ModalPresentationAnimation.CoverFromBottom)
    }

    override fun back(pageTwoViewModel: PageTwoViewModel) {
        val scene = getHomeScene()
        container.showScene(scene, SceneTransitionAnimation.SlideFromLeft)
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