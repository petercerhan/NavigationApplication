package com.example.navigationapplication.controller_library

import androidx.fragment.app.Fragment
import com.example.navigationapplication.infrastructure_services.UUIDService
import java.util.UUID

abstract class ApplicationViewModel(
    val uuidService: UUIDService,
) {
    val id: UUID = uuidService.newUUID()
    var fragmentSavedState: Fragment.SavedState? = null
}