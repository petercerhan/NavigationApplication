package com.example.navigationapplication.main

import android.content.Intent
import android.os.Bundle
import android.os.Process
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import com.example.navigationapplication.R
import com.example.navigationapplication.controller_library.AppRelauncher
import com.example.navigationapplication.controller_library.ServiceLocatorViewModel

class MainActivity : AppCompatActivity(), AppRelauncher {

    val serviceLocatorViewModel: ServiceLocatorViewModel by viewModels()
    val mainControllerViewModel: MainControllerViewModel by viewModels()
    val initialCreateFlagCacheViewModel: InitialCreateFlagCacheViewModel by viewModels()

    private var relaunchInProgress = false

    override fun relaunch() {
        if (!relaunchInProgress) {
            relaunchInProgress = true
            startActivity(Intent.makeRestartActivityTask(componentName))
        }
        Process.killProcess(Process.myPid())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        val firstOnCreateAlreadyRun = initialCreateFlagCacheViewModel.hasCompletedInitialCreate
        val savedInstanceStateSet = (savedInstanceState != null)


        if (!savedInstanceStateSet && !firstOnCreateAlreadyRun) { //cold start launch
            super.onCreate(null)
            executePostOnCreateRequiredSetup()
            initializeRootCoordinatorAndAddRootFragment()
        }
        else if (savedInstanceStateSet && !firstOnCreateAlreadyRun) { //process death recreation launch
            super.onCreate(null) //saved instance state is non-null, and discarded. This is the crux of "forcing relaunch" on process death recreation
            executePostOnCreateRequiredSetup()
            initializeRootCoordinatorAndAddRootFragment()
        }
        else if (savedInstanceStateSet && firstOnCreateAlreadyRun) { //configuration change
            super.onCreate(savedInstanceState)
            executePostOnCreateRequiredSetup()
        }
        else { //unexpected state
            //execute cold start
            super.onCreate(null)
            executePostOnCreateRequiredSetup()
            initializeRootCoordinatorAndAddRootFragment()
        }

        initialCreateFlagCacheViewModel.hasCompletedInitialCreate = true
    }

    private fun executePostOnCreateRequiredSetup() {
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
    }

    private fun initializeRootCoordinatorAndAddRootFragment() {
        val fragment = mainControllerViewModel.composeRootCoordinatorReturningFragment(
            serviceLocatorViewModel.serviceLocator,
        )
        supportFragmentManager.commit {
            replace(R.id.main, fragment)
        }
    }
}