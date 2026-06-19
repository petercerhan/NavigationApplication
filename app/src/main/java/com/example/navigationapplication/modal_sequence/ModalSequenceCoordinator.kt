package com.example.navigationapplication.modal_sequence

import android.util.Log
import com.example.navigationapplication.controller_library.ModalDismissalAnimation
import com.example.navigationapplication.controller_library.ModalPresentationAnimation
import com.example.navigationapplication.controller_library.SceneTransitionAnimation
import com.example.navigationapplication.home.HomeFragment
import com.example.navigationapplication.home.HomeViewModel
import com.example.navigationapplication.home.HomeViewModelDelegate
import com.example.navigationapplication.infrastructure_services.UUIDService
import com.example.navigationapplication.page_two.PageTwoFragment
import com.example.navigationapplication.page_two.PageTwoViewModel
import com.example.navigationapplication.page_two.PageTwoViewModelDelegate
import com.example.navigationapplication.root_container.RootContainerViewModel
import com.example.navigationapplication.root_container.Scene
import com.example.navigationapplication.simple_modal.SimpleModalFragment
import com.example.navigationapplication.simple_modal.SimpleModalViewModel
import com.example.navigationapplication.simple_modal.SimpleModalViewModelDelegate


interface ModalSequenceCoordinatorDelegate {
    fun back(modalSequenceCoordinator: ModalSequenceCoordinator)
}

class ModalSequenceCoordinator(
    val rootContainerViewModel: RootContainerViewModel,
    val uuidService: UUIDService,
    val delegate: ModalSequenceCoordinatorDelegate,
): HomeViewModelDelegate, PageTwoViewModelDelegate, SimpleModalViewModelDelegate {

    fun start() {
        val viewModelId = uuidService.newUUID()
        val homeViewModel = HomeViewModel(viewModelId, this, 0)
        val scene = Scene(viewModel = homeViewModel, fragmentType = HomeFragment::class,)
        rootContainerViewModel.showScene(scene, SceneTransitionAnimation.NoAnimation)
    }

    //HomeViewModelDelegate

    override fun next(homeViewModel: HomeViewModel) {
        val viewModelId = uuidService.newUUID()
        val pageTwoViewModel = PageTwoViewModel(viewModelId, this)

        val scene = Scene(viewModel = pageTwoViewModel, fragmentType = PageTwoFragment::class,)
        rootContainerViewModel.showScene(scene, SceneTransitionAnimation.SlideFromRight)
    }

    override fun back(homeViewModel: HomeViewModel) {
        delegate.back(this)
    }

    //PageTwoViewModelDelegate

    override fun next(pageTwoViewModel: PageTwoViewModel) {
        val viewModelId = uuidService.newUUID()
        val simpleModalViewModel = SimpleModalViewModel(viewModelId, this)

        val scene = Scene(viewModel = simpleModalViewModel, fragmentType = SimpleModalFragment::class,)
        rootContainerViewModel.showModal(scene, ModalPresentationAnimation.CoverFromBottom)
    }

    override fun back(pageTwoViewModel: PageTwoViewModel) {
        val viewModelId = uuidService.newUUID()
        val homeViewModel = HomeViewModel(viewModelId, this, 0)
        val scene = Scene(viewModel = homeViewModel, fragmentType = HomeFragment::class,)
        rootContainerViewModel.showScene(scene, SceneTransitionAnimation.SlideFromLeft)
    }

    //SimpleModalViewModelDelegate

    override fun dismiss(simpleModalViewModel: SimpleModalViewModel) {
        rootContainerViewModel.dismissModal(ModalDismissalAnimation.UncoverDown)
    }

}