package com.example.navigationapplication

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import java.util.UUID
import kotlin.reflect.KClass

abstract class SceneFragment<VM : Any> : Fragment() {

    protected val activityViewModel: MainActivityViewModel by activityViewModels()

    val sceneViewModelId: UUID
        get() = UUID.fromString(
            requireArguments().getString(VIEW_MODEL_ID)
                ?: error("Missing $VIEW_MODEL_ID"),
        )

    val frameworkViewModel: FrameworkViewModel<VM> by viewModels {
        object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return FrameworkViewModel<VM>(
                    sceneViewModelId,
                    activityViewModel.serviceLocator,
                ) as T
            }
        }
    }

    protected val viewModel: VM
        get() = frameworkViewModel.viewModel

    companion object {
        const val VIEW_MODEL_ID = "view_model_id"

        fun sceneArguments(viewModelId: String): Bundle =
            Bundle().apply { putString(VIEW_MODEL_ID, viewModelId) }

        fun newInstance(
            type: KClass<out SceneFragment<*>>,
            viewModelId: String,
        ): SceneFragment<*> =
            type.java.getDeclaredConstructor().newInstance().apply {
                arguments = sceneArguments(viewModelId)
            }
    }
}
