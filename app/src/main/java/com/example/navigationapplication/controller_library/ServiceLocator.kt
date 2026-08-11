package com.example.navigationapplication.controller_library

import com.example.navigationapplication.controller_library.container.Scene
import java.util.UUID

class ServiceLocator {

    private val viewModelMap: MutableMap<UUID, Any> = mutableMapOf()

    fun clear() {
        viewModelMap.clear()
    }

    fun cacheScene(scene: Scene) {
        viewModelMap[scene.viewModel.id] = scene.viewModel
    }

    fun cacheViewModel(id: UUID, viewModel: Any) {
        viewModelMap[id] = viewModel
    }

    fun viewModelForId(id: UUID): Any? {
        return viewModelMap[id]
    }

}