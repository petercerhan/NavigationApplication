package com.example.navigationapplication

import android.util.Log
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.SharedFlow
import java.util.UUID

class RootContainerViewModel(
    val rootContainerId: UUID,
    val serviceLocator: MutableMap<UUID, Any>,
) : ViewModel() {

    val rootContainer: RootContainer
        get() = serviceLocator[rootContainerId] as RootContainer

    val fragmentFlow: SharedFlow<Fragment>
        get() = rootContainer.fragmentFlow

    fun ping() {
        Log.d("PeterCerhan", "Ping RootContainerViewModel $rootContainerId")
    }
}