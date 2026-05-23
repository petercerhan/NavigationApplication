package com.example.navigationapplication

import android.util.Log
import java.util.UUID

class RootCoordinator(
    val rootContainerViewModel: RootContainerViewModel,
): PageTwoViewModelDelegate, HomeViewModelDelegate {

    private var homeSceneCount = 0

    private var homeSceneCache: Scene? = null

    fun start() {
        val scene = getHomeScene()
        rootContainerViewModel.showScene(scene)
    }

    //Routing

    private fun getHomeScene(): Scene {
        homeSceneCache?.let { return it }

        homeSceneCount++
        val viewModelId = UUID.randomUUID()
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
        rootContainerViewModel.showScene(scene)
    }

    //HomeViewModelDelegate

    override fun next(homeViewModel: HomeViewModel) {
        val viewModelId = UUID.randomUUID()
        val pageTwoViewModel = PageTwoViewModel(viewModelId, this)

        val scene = Scene(viewModel = pageTwoViewModel, fragmentType = PageTwoFragment::class,)
        rootContainerViewModel.showScene(scene)
    }

}