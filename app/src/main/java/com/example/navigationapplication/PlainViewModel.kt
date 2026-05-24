package com.example.navigationapplication

import androidx.fragment.app.Fragment
import java.util.UUID

abstract class PlainViewModel(
    open val id: UUID,
) {
    var fragmentSavedState: Fragment.SavedState? = null
}
