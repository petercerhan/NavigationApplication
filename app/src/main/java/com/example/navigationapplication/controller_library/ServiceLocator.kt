package com.example.navigationapplication.controller_library

import android.util.Log
import com.example.navigationapplication.root_container.Scene
import java.util.UUID

class ServiceLocator {

    init {
        ServiceLocator.counter++
        Log.d("Peter Cerhan", "ApplicationViewModelLocator Initialized ${ServiceLocator.counter}")
    }

    companion object {
        var counter: Int = 0
    }

    private val viewModelMap: MutableMap<UUID, Any> = mutableMapOf()

    fun clear() {
        viewModelMap.clear()
    }

    fun cacheScene(scene: Scene) {
        viewModelMap[scene.viewModel.id] = scene.viewModel
    }

    fun cacheViewModel(id: UUID, viewModel: Any) {
        Log.d("Peter Cerhan", "On ${ServiceLocator.counter} Cache view model $id")
        viewModelMap[id] = viewModel
    }

    fun viewModelForId(id: UUID): Any? {
        Log.d("Peter Cerhan", "On ${ServiceLocator.counter} View Model for id $id")
        return viewModelMap[id]
    }

}