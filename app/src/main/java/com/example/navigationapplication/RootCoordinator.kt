package com.example.navigationapplication

import android.util.Log
import java.util.UUID

class RootCoordinator(
    val id: UUID,
    val rootContainerViewModel: RootContainerViewModel,
    val serviceLocator: MutableMap<UUID, Any>,
): PageTwoViewModelDelegate, HomeViewModelDelegate {

    fun start() {
        val homeViewModelId = UUID.randomUUID()
        val homeViewModel = HomeViewModel(homeViewModelId, this)
        serviceLocator[homeViewModelId] = homeViewModel

        val homeFragment = HomeFragment.newInstance(homeViewModelId.toString())
        rootContainerViewModel.showScene(homeFragment)
    }

    fun next() {
    }

    fun ping() {
        Log.d("PeterCerhan", "Ping RootCoordinator")
    }


    //PageTwoViewModelDelegate

    override fun next(pageTwoViewModel: PageTwoViewModel) {
        Log.d("PeterCerhan", "pageTwoViewModel-next on RootCoordinator")
    }

    override fun back(pageTwoViewModel: PageTwoViewModel) {
        val homeViewModelId = UUID.randomUUID()
        val homeViewModel = HomeViewModel(homeViewModelId, this)
        serviceLocator[homeViewModelId] = homeViewModel

        val homeFragment = HomeFragment.newInstance(homeViewModelId.toString())
        rootContainerViewModel.showScene(homeFragment)
    }

    //HomeViewModelDelegate

    override fun next(homeViewModel: HomeViewModel) {
        val pageTwoViewModelId = UUID.randomUUID()
        val pageTwoViewModel = PageTwoViewModel(pageTwoViewModelId, this)
        serviceLocator[pageTwoViewModelId] = pageTwoViewModel

        val pageTwoFragment = PageTwoFragment.newInstance(pageTwoViewModelId.toString())
        rootContainerViewModel.showScene(pageTwoFragment)
    }

}