package com.example.navigationapplication.main

import androidx.lifecycle.ViewModel

class InitialCreateFlagCacheViewModel: ViewModel() {

    /** Survives configuration changes but not process death. */
    var hasCompletedInitialCreate = false

}