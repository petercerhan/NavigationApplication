package com.example.navigationapplication

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
import com.example.navigationapplication.simple_modal.SimpleModalViewModel
import com.example.navigationapplication.simple_modal.SimpleModalViewModelDelegate

class RootCoordinator(
    val containerViewModel: ContainerViewModel,
    val uuidService: UUIDService,
): PageTwoViewModelDelegate, HomeViewModelDelegate, SimpleModalViewModelDelegate, ModalSequenceCoordinatorDelegate {

    private var homeSceneCache: Scene? = null

    fun start() {
        val scene = getHomeScene()
        containerViewModel.showScene(scene, SceneTransitionAnimation.NoAnimation)
    }

    //Routing

    private fun getHomeScene(): Scene {
        homeSceneCache?.let { return it }

        val viewModelId = uuidService.newUUID()
        val homeViewModel = HomeViewModel(viewModelId, this)
        val scene = Scene(viewModel = homeViewModel, fragmentType = HomeFragment::class,)
        homeSceneCache = scene
        return scene
    }

    //HomeViewModelDelegate

    override fun next(homeViewModel: HomeViewModel) {
        val viewModelId = uuidService.newUUID()
        val pageTwoViewModel = PageTwoViewModel(viewModelId, this)

        val scene = Scene(viewModel = pageTwoViewModel, fragmentType = PageTwoFragment::class,)
        containerViewModel.showScene(scene, SceneTransitionAnimation.SlideFromRight)
    }

    override fun back(homeViewModel: HomeViewModel) {
        //do nothing
    }

    //PageTwoViewModelDelegate

    override fun next(pageTwoViewModel: PageTwoViewModel) {
        val containerId = uuidService.newUUID()
        val newContainerViewModel = ContainerViewModel(id = containerId)
        val coordinator = ModalSequenceCoordinator(newContainerViewModel, uuidService, this)
        coordinator.start()
        val scene = Scene(viewModel = newContainerViewModel, fragmentType = ContainerFragment::class,)
        containerViewModel.showModal(scene, ModalPresentationAnimation.CoverFromBottom)
    }

    override fun back(pageTwoViewModel: PageTwoViewModel) {
        val scene = getHomeScene()
        containerViewModel.showScene(scene, SceneTransitionAnimation.SlideFromLeft)
    }

    //SimpleModalViewModelDelegate

    override fun dismiss(simpleModalViewModel: SimpleModalViewModel) {
        containerViewModel.dismissModal(ModalDismissalAnimation.UncoverDown)
    }

    //ModalSequenceCoordinatorDelegate

    override fun back(modalSequenceCoordinator: ModalSequenceCoordinator) {
        containerViewModel.dismissModal(ModalDismissalAnimation.UncoverDown)
    }

}