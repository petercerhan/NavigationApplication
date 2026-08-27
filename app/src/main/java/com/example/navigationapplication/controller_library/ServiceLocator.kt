package com.example.navigationapplication.controller_library

import java.util.UUID

class ServiceLocator {

    private val viewModelMap: MutableMap<UUID, Any> = mutableMapOf()

    fun reset() {
        viewModelMap.clear()
    }

    fun registerViewModel(viewModel: ApplicationViewModel) {
        viewModelMap[viewModel.id] = viewModel
    }

    fun viewModelForId(id: UUID): ApplicationViewModel? {
        return viewModelMap[id] as? ApplicationViewModel
    }

}