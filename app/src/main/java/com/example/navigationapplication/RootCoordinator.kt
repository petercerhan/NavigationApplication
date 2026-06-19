package com.example.navigationapplication

import android.util.Log
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
import com.example.navigationapplication.root_container.RootContainerFragment
import com.example.navigationapplication.root_container.RootContainerViewModel
import com.example.navigationapplication.root_container.Scene
import com.example.navigationapplication.simple_modal.SimpleModalFragment
import com.example.navigationapplication.simple_modal.SimpleModalViewModel
import com.example.navigationapplication.simple_modal.SimpleModalViewModelDelegate
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class RootCoordinator(
    val rootContainerViewModel: RootContainerViewModel,
    val uuidService: UUIDService,
): PageTwoViewModelDelegate, HomeViewModelDelegate, SimpleModalViewModelDelegate, ModalSequenceCoordinatorDelegate {

    private var homeSceneCount = 0

    private var homeSceneCache: Scene? = null

    fun start() {
        val scene = getHomeScene()
        rootContainerViewModel.showScene(scene, SceneTransitionAnimation.NoAnimation)
    }

    //Routing

    private fun getHomeScene(): Scene {
        homeSceneCache?.let { return it }

        homeSceneCount++
        val viewModelId = uuidService.newUUID()
        val homeViewModel = HomeViewModel(viewModelId, this, homeSceneCount)
        val scene = Scene(viewModel = homeViewModel, fragmentType = HomeFragment::class,)
        homeSceneCache = scene
        return scene
    }

    //HomeViewModelDelegate

    override fun next(homeViewModel: HomeViewModel) {
        val viewModelId = uuidService.newUUID()
        val pageTwoViewModel = PageTwoViewModel(viewModelId, this)

        val scene = Scene(viewModel = pageTwoViewModel, fragmentType = PageTwoFragment::class,)
        rootContainerViewModel.showScene(scene, SceneTransitionAnimation.SlideFromRight)
    }

    override fun back(homeViewModel: HomeViewModel) {
        //do nothing
    }

    //PageTwoViewModelDelegate

    override fun next(pageTwoViewModel: PageTwoViewModel) {
        val rootContainerId = uuidService.newUUID()
        val newContainerViewModel = RootContainerViewModel(id = rootContainerId)
        val coordinator = ModalSequenceCoordinator(newContainerViewModel, uuidService, this)
        coordinator.start()
        val scene = Scene(viewModel = newContainerViewModel, fragmentType = RootContainerFragment::class,)
        rootContainerViewModel.showModal(scene, ModalPresentationAnimation.CoverFromBottom)
    }

    override fun back(pageTwoViewModel: PageTwoViewModel) {
        val scene = getHomeScene()
        rootContainerViewModel.showScene(scene, SceneTransitionAnimation.SlideFromLeft)
    }

    //SimpleModalViewModelDelegate

    override fun dismiss(simpleModalViewModel: SimpleModalViewModel) {
        rootContainerViewModel.dismissModal(ModalDismissalAnimation.UncoverDown)
    }

    //ModalSequenceCoordinatorDelegate

    override fun back(modalSequenceCoordinator: ModalSequenceCoordinator) {
        rootContainerViewModel.dismissModal(ModalDismissalAnimation.UncoverDown)
    }

}