package com.example.navigationapplication

import android.util.Log
import java.util.UUID

class RootCoordinator(
    val id: UUID,
    val rootContainer: RootContainer
) {

    fun next() {
        Log.d("PeterCerhan", "RootCoordinator Next")
        //create PageTwoFragment
        val pageTwoFragment = PageTwoFragment()
        //rootcontainer.showFragment(pageTwoFragment)
        rootContainer.showScene(pageTwoFragment)
    }

    fun ping() {
        Log.d("PeterCerhan", "Ping RootCoordinator")
    }

}