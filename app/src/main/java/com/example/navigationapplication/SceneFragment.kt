package com.example.navigationapplication

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import java.util.UUID

abstract class SceneFragment<VM : Any> : Fragment() {

    protected val activityViewModel: MainActivityViewModel by activityViewModels()

    protected val sceneViewModelId: UUID
        get() = UUID.fromString(
            requireArguments().getString(VIEW_MODEL_ID)
                ?: error("Missing $VIEW_MODEL_ID"),
        )

    val systemViewModel: SystemViewModel<VM> by viewModels {
        object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return SystemViewModel<VM>(
                    sceneViewModelId,
                    activityViewModel.serviceLocator,
                ) as T
            }
        }
    }

    protected val viewModel: VM
        get() = systemViewModel.viewModel

    companion object {
        const val VIEW_MODEL_ID = "view_model_id"

        fun sceneArguments(viewModelId: String): Bundle =
            Bundle().apply { putString(VIEW_MODEL_ID, viewModelId) }

        inline fun <reified F : SceneFragment<*>> newInstance(viewModelId: String): F =
            F::class.java.getDeclaredConstructor().newInstance().apply {
                arguments = sceneArguments(viewModelId)
            }
    }
}
