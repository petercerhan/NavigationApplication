package com.example.navigationapplication.modal_sequence.main

import com.example.navigationapplication.controller_library.container.Scene
import com.example.navigationapplication.root_sequence.view.HomeFragment
import com.example.navigationapplication.root_sequence.controller.HomeViewModel
import com.example.navigationapplication.root_sequence.controller.HomeViewModelDelegate
import com.example.navigationapplication.infrastructure_services.UUIDService
import com.example.navigationapplication.root_sequence.view.PageTwoFragment
import com.example.navigationapplication.root_sequence.controller.PageTwoViewModel
import com.example.navigationapplication.root_sequence.controller.PageTwoViewModelDelegate
import com.example.navigationapplication.modal_sequence.view.SimpleModalFragment
import com.example.navigationapplication.modal_sequence.controller.SimpleModalViewModel
import com.example.navigationapplication.modal_sequence.controller.SimpleModalViewModelDelegate

class ModalSequenceComposer(
    val uuidService: UUIDService,
) {

    fun composeHomeScene(delegate: HomeViewModelDelegate): Scene {
        val homeViewModel = HomeViewModel(uuidService, delegate)
        return Scene(viewModel = homeViewModel, fragmentType = HomeFragment::class,)
    }

    fun composePageTwoScene(delegate: PageTwoViewModelDelegate): Scene {
        val pageTwoViewModel = PageTwoViewModel(uuidService, delegate)
        return Scene(viewModel = pageTwoViewModel, fragmentType = PageTwoFragment::class,)
    }

    fun composeSimpleModalScene(delegate: SimpleModalViewModelDelegate): Scene {
        val simpleModalViewModel = SimpleModalViewModel(uuidService, delegate)
        return Scene(viewModel = simpleModalViewModel, fragmentType = SimpleModalFragment::class,)
    }

}