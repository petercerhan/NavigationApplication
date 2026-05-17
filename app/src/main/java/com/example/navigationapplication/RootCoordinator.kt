package com.example.navigationapplication

import android.util.Log
import java.util.UUID

class RootCoordinator(
    val id: UUID,
    val rootContainer: RootContainer,
    val serviceLocator: MutableMap<UUID, Any>,
): PageTwoViewModelDelegate {

    fun next() {
        val pageTwoViewModelId = UUID.randomUUID()
        val pageTwoViewModel = PageTwoViewModel(id=pageTwoViewModelId, delegate=this)
        serviceLocator[pageTwoViewModelId] = pageTwoViewModel


        //pageTwoViewModelId needs to be sent to PageTwoFragment on creation
        val pageTwoFragment = PageTwoFragment.newInstance(pageTwoViewModelId.toString())
        rootContainer.showScene(pageTwoFragment)
    }

    fun ping() {
        Log.d("PeterCerhan", "Ping RootCoordinator")
    }

    //PageTwoViewModelDelegate

    override fun next(pageTwoViewModel: PageTwoViewModel) {
        Log.d("PeterCerhan", "pageTwoViewModel-next on RootCoordinator")
    }

}