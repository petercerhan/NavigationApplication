package com.example.navigationapplication.modal_sequence

import com.example.navigationapplication.controller_library.container.Scene
import com.example.navigationapplication.home.HomeFragment
import com.example.navigationapplication.home.HomeViewModel
import com.example.navigationapplication.home.HomeViewModelDelegate
import com.example.navigationapplication.infrastructure_services.UUIDService
import com.example.navigationapplication.page_two.PageTwoFragment
import com.example.navigationapplication.page_two.PageTwoViewModel
import com.example.navigationapplication.page_two.PageTwoViewModelDelegate
import com.example.navigationapplication.simple_modal.SimpleModalFragment
import com.example.navigationapplication.simple_modal.SimpleModalViewModel
import com.example.navigationapplication.simple_modal.SimpleModalViewModelDelegate

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