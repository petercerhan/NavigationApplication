package com.example.navigationapplication

import androidx.fragment.app.Fragment
import java.util.UUID
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class RootContainerViewModel(
    val id: UUID,
) {
    private val _fragmentFlow = MutableSharedFlow<Fragment>(replay = 1)
    val fragmentFlow: SharedFlow<Fragment> = _fragmentFlow.asSharedFlow()

    fun showScene(fragment: Fragment) {
        _fragmentFlow.tryEmit(fragment)
    }

}
