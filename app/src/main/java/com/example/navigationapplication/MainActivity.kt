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
    val initialCreateFlagCacheViewModel: InitialCreateFlagCacheViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        val viewModelScopedInitializedFlagSet = initialCreateFlagCacheViewModel.hasCompletedInitialCreate
        val savedInstanceStateSet = (savedInstanceState != null)


        if (!savedInstanceStateSet && !viewModelScopedInitializedFlagSet) { //cold start launch
            super.onCreate(null)
            executePostOnCreateRequiredSetup()
            initializeRootCoordinatorAndAddRootFragment()
        }
        else if (savedInstanceStateSet && !viewModelScopedInitializedFlagSet) { //process death recreation launch
            super.onCreate(null) //saved instance state is non-null, and discarded. This is the crux of forcing relaunch on process death recreation
            executePostOnCreateRequiredSetup()
            initializeRootCoordinatorAndAddRootFragment()
        }
        else if (savedInstanceStateSet && viewModelScopedInitializedFlagSet) { //configuration change
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