package com.example.navigationapplication.modal_sequence

import android.util.Log
import com.example.navigationapplication.controller_library.SceneTransitionAnimation
import com.example.navigationapplication.home.HomeFragment
import com.example.navigationapplication.home.HomeViewModel
import com.example.navigationapplication.home.HomeViewModelDelegate
import com.example.navigationapplication.infrastructure_services.UUIDService
import com.example.navigationapplication.root_container.RootContainerViewModel
import com.example.navigationapplication.root_container.Scene

class ModalSequenceCoordinator(
    val rootContainerViewModel: RootContainerViewModel,
    val uuidService: UUIDService,
): HomeViewModelDelegate {

    fun start() {
        val viewModelId = uuidService.newUUID()
        val homeViewModel = HomeViewModel(viewModelId, this, 0)
        val scene = Scene(viewModel = homeViewModel, fragmentType = HomeFragment::class,)
        rootContainerViewModel.showScene(scene, SceneTransitionAnimation.NoAnimation)
    }

    //HomeViewModelDelegate

    override fun next(homeViewModel: HomeViewModel) {
        Log.d("Peter Cerhan", "HomeViewModelDelegate Next")
    }

}