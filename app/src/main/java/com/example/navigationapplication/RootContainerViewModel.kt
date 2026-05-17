package com.example.navigationapplication

import android.util.Log
import androidx.fragment.app.Fragment
import java.util.UUID
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class RootContainerViewModel(
    val id: UUID,
    val homeViewModel: HomeViewModel,
) {
    private val _fragmentFlow = MutableSharedFlow<Fragment>(extraBufferCapacity = 64)
    val fragmentFlow: SharedFlow<Fragment> = _fragmentFlow.asSharedFlow()

    fun showScene(fragment: Fragment) {
        _fragmentFlow.tryEmit(fragment)
    }

    fun ping() {
        Log.d("PeterCerhan", "Ping RootContainer $id")
    }

}
