package com.example.navigationapplication.modal_sequence.main

import com.example.navigationapplication.controller_library.container.Scene
import com.example.navigationapplication.root_sequence.view.HomeFragment
import com.example.navigationapplication.root_sequence.controller.HomeViewModel
import com.example.navigationapplication.root_sequence.controller.HomeViewModelDelegate
import com.example.navigationapplication.infrastructure_services.UUIDService
import com.example.navigationapplication.modal_sequence.view.ModalPageTwoFragment
import com.example.navigationapplication.modal_sequence.controller.ModalPageTwoViewModel
import com.example.navigationapplication.modal_sequence.controller.ModalPageTwoViewModelDelegate
import com.example.navigationapplication.modal_sequence.view.SimpleModalFragment
import com.example.navigationapplication.modal_sequence.controller.SimpleModalViewModel
import com.example.navigationapplication.modal_sequence.controller.SimpleModalViewModelDelegate
import com.example.navigationapplication.modal_sequence.view.ReplacementModalFragment
import com.example.navigationapplication.modal_sequence.controller.ReplacementModalViewModel
import com.example.navigationapplication.modal_sequence.controller.ReplacementModalViewModelDelegate

class ModalSequenceComposer(
    val uuidService: UUIDService,
) {

    fun composeHomeScene(delegate: HomeViewModelDelegate): Scene {
        val homeViewModel = HomeViewModel(uuidService, delegate)
        return Scene(viewModel = homeViewModel, fragmentType = HomeFragment::class,)
    }

    fun composeModalPageTwoScene(delegate: ModalPageTwoViewModelDelegate): Scene {
        val modalPageTwoViewModel = ModalPageTwoViewModel(uuidService, delegate)
        return Scene(viewModel = modalPageTwoViewModel, fragmentType = ModalPageTwoFragment::class,)
    }

    fun composeSimpleModalScene(delegate: SimpleModalViewModelDelegate): Scene {
        val simpleModalViewModel = SimpleModalViewModel(uuidService, delegate)
        return Scene(viewModel = simpleModalViewModel, fragmentType = SimpleModalFragment::class,)
    }

    fun composeReplacementModalScene(delegate: ReplacementModalViewModelDelegate): Scene {
        val replacementModalViewModel = ReplacementModalViewModel(uuidService, delegate)
        return Scene(viewModel = replacementModalViewModel, fragmentType = ReplacementModalFragment::class,)
    }

}