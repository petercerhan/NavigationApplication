package com.example.navigationapplication

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import com.example.navigationapplication.controller_library.ServiceLocatorViewModel

class MainActivity : AppCompatActivity() {

    val serviceLocatorViewModel: ServiceLocatorViewModel by viewModels()
    val mainControllerViewModel: MainControllerViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        val isProcessDeathRecreation =
            savedInstanceState != null && !mainControllerViewModel.hasCompletedInitialCreate

        if (isProcessDeathRecreation) {
            super.onCreate(null)
        } else {
            super.onCreate(savedInstanceState)
        }

        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        if (isProcessDeathRecreation || savedInstanceState == null) {
            val fragment = mainControllerViewModel.composeRootCoordinatorReturningFragment(
                serviceLocatorViewModel.serviceLocator,
            )
            supportFragmentManager.commit {
                replace(R.id.main, fragment)
            }
        }

        mainControllerViewModel.hasCompletedInitialCreate = true
    }
}