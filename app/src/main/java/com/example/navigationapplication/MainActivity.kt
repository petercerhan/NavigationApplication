package com.example.navigationapplication

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.navigationapplication.controller_library.SceneFragment
import com.example.navigationapplication.controller_library.ServiceLocatorViewModel
import com.example.navigationapplication.infrastructure_services.UUIDService
import com.example.navigationapplication.infrastructure_services.UUIDServiceImpl
import com.example.navigationapplication.container.ContainerFragment
import com.example.navigationapplication.container.ContainerViewModel
import com.example.navigationapplication.infrastructure_services.Logger

class MainActivity : AppCompatActivity() {

    val serviceLocatorViewModel: ServiceLocatorViewModel by viewModels()
    val mainControllerViewModel: MainControllerViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        val fragment = mainControllerViewModel.composeRootCoordinatorReturningFragment(serviceLocatorViewModel.serviceLocator)

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