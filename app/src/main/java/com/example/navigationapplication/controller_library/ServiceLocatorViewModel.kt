package com.example.navigationapplication.controller_library

import androidx.lifecycle.ViewModel

open class ServiceLocatorViewModel(
    val serviceLocator: ServiceLocator = ServiceLocator()
): ViewModel()