package com.example.navigationapplication

import android.util.Log
import java.util.UUID

class RootCoordinator(
    val rootContainerViewModel: RootContainerViewModel,
): PageTwoViewModelDelegate, HomeViewModelDelegate {

    fun start() {
        val viewModelId = UUID.randomUUID()
        val homeViewModel = HomeViewModel(viewModelId, this)

        val scene = Scene(viewModel = homeViewModel, fragmentType = HomeFragment::class,)
        rootContainerViewModel.showScene(scene)
    }

    //PageTwoViewModelDelegate

    override fun next(pageTwoViewModel: PageTwoViewModel) {
        Log.d("PeterCerhan", "pageTwoViewModel-next on RootCoordinator")
    }

    override fun back(pageTwoViewModel: PageTwoViewModel) {
        val viewModelId = UUID.randomUUID()
        val homeViewModel = HomeViewModel(viewModelId, this)

        val scene = Scene(viewModel = homeViewModel, fragmentType = HomeFragment::class,)
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