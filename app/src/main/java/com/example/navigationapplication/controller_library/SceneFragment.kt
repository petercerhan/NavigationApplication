package com.example.navigationapplication.controller_library

import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import java.util.UUID
import kotlin.reflect.KClass

abstract class SceneFragment<VM : Any> : Fragment() {

    //System View Models

    protected val parentServiceLocatorViewModel: ServiceLocatorViewModel by viewModels(
        ownerProducer = { parentFragment ?: requireActivity() }
    )

    val sceneViewModelId: UUID
        get() = UUID.fromString(
            requireArguments().getString(VIEW_MODEL_ID)
                ?: error("Missing ${VIEW_MODEL_ID}"),
        )

    protected val viewModel: VM
        get() = parentServiceLocatorViewModel.serviceLocator.viewModelForId(sceneViewModelId) as VM

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


    //Lifecycle

    private var backPressedCallback: OnBackPressedCallback? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                backButtonAction()
            }
        }
        backPressedCallback = callback
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }

    abstract fun backButtonAction()

}