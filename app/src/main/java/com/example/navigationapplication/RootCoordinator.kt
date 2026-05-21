package com.example.navigationapplication

import android.util.Log
import java.util.UUID

class RootCoordinator(
    val rootContainerViewModel: RootContainerViewModel,
    val serviceLocator: MutableMap<UUID, Any>,
): PageTwoViewModelDelegate, HomeViewModelDelegate {

    fun start() {
        val viewModelId = UUID.randomUUID()
        val homeViewModel = HomeViewModel(viewModelId, this)
        serviceLocator[viewModelId] = homeViewModel

        val scene = Scene(viewModelId = viewModelId.toString(), fragmentType = HomeFragment::class,)
        rootContainerViewModel.showScene(scene)
    }

    //PageTwoViewModelDelegate

    override fun next(pageTwoViewModel: PageTwoViewModel) {
        Log.d("PeterCerhan", "pageTwoViewModel-next on RootCoordinator")
    }

    override fun back(pageTwoViewModel: PageTwoViewModel) {
        val viewModelId = UUID.randomUUID()
        val homeViewModel = HomeViewModel(viewModelId, this)
        serviceLocator[viewModelId] = homeViewModel

        val scene = Scene(viewModelId = viewModelId.toString(), fragmentType = HomeFragment::class,)
        rootContainerViewModel.showScene(scene)
    }

    //HomeViewModelDelegate

    override fun next(homeViewModel: HomeViewModel) {
        val viewModelId = UUID.randomUUID()
        val pageTwoViewModel = PageTwoViewModel(viewModelId, this)
        serviceLocator[viewModelId] = pageTwoViewModel

        val scene = Scene(viewModelId = viewModelId.toString(), fragmentType = PageTwoFragment::class,)
        rootContainerViewModel.showScene(scene)
    }

}