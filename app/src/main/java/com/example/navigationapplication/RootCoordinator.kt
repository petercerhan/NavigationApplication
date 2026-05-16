package com.example.navigationapplication

import android.util.Log
import java.util.UUID

class RootCoordinator(
    val id: UUID,
    val rootContainer: RootContainer,
    val serviceLocator: MutableMap<UUID, Any>,
) {

    fun next() {
        Log.d("PeterCerhan", "RootCoordinator Next")

        val pageTwoViewModelId = UUID.randomUUID()
        val pageTwoViewModel = PageTwoViewModel(id=pageTwoViewModelId)
        serviceLocator[pageTwoViewModelId] = pageTwoViewModel


        //pageTwoViewModelId needs to be sent to PageTwoFragment on creation
        val pageTwoFragment = PageTwoFragment.newInstance(pageTwoViewModelId.toString())
        rootContainer.showScene(pageTwoFragment)
    }

    fun ping() {
        Log.d("PeterCerhan", "Ping RootCoordinator")
    }

}