package com.example.navigationapplication

import androidx.fragment.app.Fragment
import java.util.UUID

abstract class ApplicationViewModel(
    open val id: UUID,
) {
    var fragmentSavedState: Fragment.SavedState? = null
}
