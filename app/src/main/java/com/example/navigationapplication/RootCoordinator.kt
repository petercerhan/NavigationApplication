package com.example.navigationapplication

import android.util.Log
import com.example.navigationapplication.controller_library.SceneAnimation
import com.example.navigationapplication.infrastructure_services.UUIDService
import java.util.UUID

class RootCoordinator(
    val rootContainerViewModel: RootContainerViewModel,
    val uuidService: UUIDService,
): PageTwoViewModelDelegate, HomeViewModelDelegate {

    private var homeSceneCount = 0

    private var homeSceneCache: Scene? = null

    fun start() {
        val scene = getHomeScene()
        rootContainerViewModel.showScene(scene, SceneAnimation.SlideFromRight)
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

    //PageTwoViewModelDelegate

    override fun next(pageTwoViewModel: PageTwoViewModel) {
        Log.d("PeterCerhan", "pageTwoViewModel-next on RootCoordinator")
    }

    override fun back(pageTwoViewModel: PageTwoViewModel) {
        val scene = getHomeScene()
        rootContainerViewModel.showScene(scene, SceneAnimation.SlideFromLeft)
    }

    //HomeViewModelDelegate

    override fun next(homeViewModel: HomeViewModel) {
        val viewModelId = uuidService.newUUID()
        val pageTwoViewModel = PageTwoViewModel(viewModelId, this)

        val scene = Scene(viewModel = pageTwoViewModel, fragmentType = PageTwoFragment::class,)
        rootContainerViewModel.showScene(scene, SceneAnimation.SlideFromRight)
    }

}