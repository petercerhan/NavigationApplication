package com.example.navigationapplication.root_sequence.main

import com.example.navigationapplication.controller_library.container.Scene
import com.example.navigationapplication.controller_library.Coordinator
import com.example.navigationapplication.root_sequence.view.HomeFragment
import com.example.navigationapplication.root_sequence.controller.HomeViewModel
import com.example.navigationapplication.root_sequence.controller.HomeViewModelDelegate
import com.example.navigationapplication.infrastructure_services.UUIDService
import com.example.navigationapplication.modal_sequence.controller.ModalSequenceCoordinatorDelegate
import com.example.navigationapplication.modal_sequence.main.ModalSequenceCoordinatorFactory
import com.example.navigationapplication.root_sequence.view.PageTwoFragment
import com.example.navigationapplication.root_sequence.view.TableFragment
import com.example.navigationapplication.root_sequence.controller.PageTwoViewModel
import com.example.navigationapplication.root_sequence.controller.PageTwoViewModelDelegate
import com.example.navigationapplication.root_sequence.controller.TableViewModel
import com.example.navigationapplication.root_sequence.controller.TableViewModelDelegate

class RootSequenceComposer(
    val uuidService: UUIDService,
) {
    private var homeSceneCache: Scene? = null
    private var tableSceneCache: Scene? = null

    fun composeHomeScene(delegate: HomeViewModelDelegate): Scene {
        homeSceneCache?.let { return it }

        val homeViewModel = HomeViewModel(uuidService, delegate)
        val homeScene = Scene(viewModel = homeViewModel, fragmentType = HomeFragment::class,)
        this.homeSceneCache = homeScene
        return homeScene
    }

    fun composeTableScene(delegate: TableViewModelDelegate): Scene {
        tableSceneCache?.let { return it }

        val tableViewModel = TableViewModel(uuidService, delegate)
        val tableScene = Scene(viewModel = tableViewModel, fragmentType = TableFragment::class,)
        this.tableSceneCache = tableScene
        return tableScene
    }

    fun composePageTwoScene(delegate: PageTwoViewModelDelegate): Scene {
        val pageTwoViewModel = PageTwoViewModel(uuidService, delegate)
        return Scene(viewModel = pageTwoViewModel, fragmentType = PageTwoFragment::class,)
    }

    fun composeModalSequenceCoordinator(delegate: ModalSequenceCoordinatorDelegate): Coordinator {
        return ModalSequenceCoordinatorFactory.composeModalSequenceCoordinator(uuidService, delegate)
    }

}