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
import java.util.ServiceLoader

class MainActivity : AppCompatActivity() {

    val serviceLocatorViewModel: ServiceLocatorViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        //set up coordinator etc.
        val uuidService: UUIDService = UUIDServiceImpl()
        val rootContainerId = uuidService.newUUID()
        val rootContainerViewModel = RootContainerViewModel(id=rootContainerId)
        val coordinator = RootCoordinator(rootContainerViewModel, uuidService)

        serviceLocatorViewModel.serviceLocator.cacheViewModel(rootContainerId, rootContainerViewModel)

        coordinator.start()
        //add to service locator on service locator view model
        //add coordinator to root activity


        val fragment = SceneFragment.newInstance(RootContainerFragment::class,coordinator.rootContainerViewModel.id.toString() )

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