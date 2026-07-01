package com.example.navigationapplication

import com.example.navigationapplication.controller_library.container.Scene
import com.example.navigationapplication.controller_library.Coordinator
import com.example.navigationapplication.home.HomeFragment
import com.example.navigationapplication.home.HomeViewModel
import com.example.navigationapplication.home.HomeViewModelDelegate
import com.example.navigationapplication.infrastructure_services.UUIDService
import com.example.navigationapplication.modal_sequence.ModalSequenceCoordinatorDelegate
import com.example.navigationapplication.modal_sequence.ModalSequenceCoordinatorFactory
import com.example.navigationapplication.page_two.PageTwoFragment
import com.example.navigationapplication.page_two.PageTwoViewModel
import com.example.navigationapplication.page_two.PageTwoViewModelDelegate

class RootSequenceComposer(
    val uuidService: UUIDService,
) {
    private var homeSceneCache: Scene? = null


    fun composeHomeScene(delegate: HomeViewModelDelegate): Scene {
        homeSceneCache?.let { return it }

        val homeViewModel = HomeViewModel(uuidService, delegate)
        val homeScene = Scene(viewModel = homeViewModel, fragmentType = HomeFragment::class,)
        this.homeSceneCache = homeScene
        return homeScene
    }

    fun composePageTwoScene(delegate: PageTwoViewModelDelegate): Scene {
        val pageTwoViewModel = PageTwoViewModel(uuidService, delegate)
        return Scene(viewModel = pageTwoViewModel, fragmentType = PageTwoFragment::class,)
    }

    fun composeModalSequenceCoordinator(delegate: ModalSequenceCoordinatorDelegate): Coordinator {
        return ModalSequenceCoordinatorFactory.composeModalSequenceCoordinator(uuidService, delegate)
    }

}