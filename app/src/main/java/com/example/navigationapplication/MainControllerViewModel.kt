package com.example.navigationapplication

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import com.example.navigationapplication.controller_library.container.ContainerFragment
import com.example.navigationapplication.controller_library.Coordinator
import com.example.navigationapplication.controller_library.SceneFragment
import com.example.navigationapplication.controller_library.ServiceLocator
import com.example.navigationapplication.infrastructure_services.UUIDServiceImpl

class MainControllerViewModel: ViewModel() {

    val uuidService = UUIDServiceImpl()
    var rootCoordinator: Coordinator? = null

    fun composeRootCoordinatorReturningFragment(serviceLocator: ServiceLocator): Fragment {
        val rootCoordinator = RootCoordinatorFactory.composeRootCoordinator(uuidService)
        this.rootCoordinator = rootCoordinator
        serviceLocator.cacheViewModel(rootCoordinator.containerViewModel.id, rootCoordinator.containerViewModel)

        return SceneFragment.newInstance(ContainerFragment::class,rootCoordinator.containerViewModel.id)
    }

}