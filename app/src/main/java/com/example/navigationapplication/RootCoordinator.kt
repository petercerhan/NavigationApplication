package com.example.navigationapplication

import com.example.navigationapplication.controller_library.ModalDismissalAnimation
import com.example.navigationapplication.controller_library.ModalPresentationAnimation
import com.example.navigationapplication.controller_library.SceneTransitionAnimation
import com.example.navigationapplication.infrastructure_services.UUIDService

class RootCoordinator(
    val rootContainerViewModel: RootContainerViewModel,
    val uuidService: UUIDService,
): PageTwoViewModelDelegate, HomeViewModelDelegate, SimpleModalViewModelDelegate {

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

    //PageTwoViewModelDelegate

    override fun next(pageTwoViewModel: PageTwoViewModel) {
        val viewModelId = uuidService.newUUID()
        val simpleModalViewModel = SimpleModalViewModel(viewModelId, this)

        val scene = Scene(viewModel = simpleModalViewModel, fragmentType = SimpleModalFragment::class,)
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

}