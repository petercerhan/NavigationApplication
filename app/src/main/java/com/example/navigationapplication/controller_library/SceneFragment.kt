package com.example.navigationapplication.controller_library

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import java.util.UUID
import kotlin.reflect.KClass

abstract class SceneFragment<VM : Any> : Fragment() {

    //View Model Attachment

    protected val parentServiceLocatorViewModel: ServiceLocatorViewModel by viewModels(
        ownerProducer = { parentFragment ?: requireActivity() }
    )

    val viewModelId: UUID
        get() = UUID.fromString(
            requireArguments().getString(VIEW_MODEL_ID)
                ?: error("Missing ${VIEW_MODEL_ID}"),
        )

    protected val viewModel: VM
        get() = parentServiceLocatorViewModel.serviceLocator.viewModelForId(viewModelId) as VM

    //Initialization

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

    abstract fun backButtonAction()

}