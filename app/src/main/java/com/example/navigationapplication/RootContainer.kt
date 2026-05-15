package com.example.navigationapplication

import android.util.Log
import java.util.UUID

class RootContainer(
    val id: UUID,
    val homeViewModel: HomeViewModel
) {

    fun ping() {
        Log.d("PeterCerhan", "Ping RootContainer $id")
    }

}