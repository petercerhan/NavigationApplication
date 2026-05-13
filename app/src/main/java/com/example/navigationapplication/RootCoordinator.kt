package com.example.navigationapplication

import android.util.Log
import java.util.UUID

class RootCoordinator(
    val id: UUID
) {

    fun ping() {
        Log.d("PeterCerhan", "Ping RootCoordinator")
    }

}