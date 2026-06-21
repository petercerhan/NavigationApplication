package com.example.navigationapplication

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import com.example.navigationapplication.controller_library.SceneFragment
import com.example.navigationapplication.controller_library.ServiceLocatorViewModel
import com.example.navigationapplication.infrastructure_services.UUIDService
import com.example.navigationapplication.infrastructure_services.UUIDServiceImpl
import com.example.navigationapplication.container.ContainerFragment
import com.example.navigationapplication.container.ContainerViewModel
import com.example.navigationapplication.infrastructure_services.Logger

class MainActivity : AppCompatActivity() {

    val serviceLocatorViewModel: ServiceLocatorViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        //set up coordinator etc.
        val logger = Logger(false)

        val uuidService: UUIDService = UUIDServiceImpl()
        val containerId = uuidService.newUUID()
        val containerViewModel = ContainerViewModel(id = containerId, logger)
        val coordinator = RootCoordinator(containerViewModel, uuidService)

        serviceLocatorViewModel.serviceLocator.cacheViewModel(containerId, containerViewModel)

        coordinator.start()
        //add to service locator on service locator view model
        //add coordinator to root activity


        val fragment = SceneFragment.newInstance(ContainerFragment::class,containerId.toString() )

        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                replace(
                    R.id.main,
                    fragment
                )
            }
        }
    }
}