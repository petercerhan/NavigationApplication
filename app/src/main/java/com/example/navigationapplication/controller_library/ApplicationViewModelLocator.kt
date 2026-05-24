package com.example.navigationapplication.controller_library

import com.example.navigationapplication.Scene
import java.util.UUID

class ApplicationViewModelLocator {

    private val viewModelMap: MutableMap<UUID, Any> = mutableMapOf()

    fun clear() {
        viewModelMap.clear()
    }

    fun cacheScene(scene: Scene) {
        viewModelMap[scene.viewModel.id] = scene.viewModel
    }

    fun viewModelForId(id: UUID): Any? {
        return viewModelMap[id]
    }

}