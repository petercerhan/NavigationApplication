package com.example.navigationapplication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import java.util.UUID
import kotlinx.coroutines.launch

class RootContainerFragment : Fragment() {

    val activityViewModel: MainActivityViewModel by activityViewModels()

    private val serviceLocator: MutableMap<UUID, Any>
        get() = activityViewModel.serviceLocator

    private val rootContainerServiceLocator: MutableMap<UUID, Any>
        get() = activityViewModel.rootContainerServiceLocator

    val rootContainerSystemViewModel: RootContainerSystemViewModel by viewModels {
        object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val id = UUID.fromString(
                    requireArguments().getString(ARG_ROOT_CONTAINER_ID)
                        ?: error("Missing $ARG_ROOT_CONTAINER_ID")
                )
                return RootContainerSystemViewModel(
                    rootContainerId = id,
                    serviceLocator = activityViewModel.rootContainerServiceLocator,
                ) as T
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_container, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                rootContainerSystemViewModel.sceneFlow.collect { scene ->
                    showScene(scene)
                }
            }
        }
    }

    private fun showScene(scene: Scene) {
        if (incomingSceneIsAlreadyActive(scene)) {
            return
        }

        saveActiveSceneViewState()

        serviceLocator.clear()
        serviceLocator[scene.viewModel.id] = scene.viewModel

        val fragment = SceneFragment.newInstance(
            scene.fragmentType,
            scene.viewModel.id.toString(),
        ).apply {
            setInitialSavedState(scene.viewModel.fragmentSavedState)
        }
        childFragmentManager.beginTransaction()
            .setCustomAnimations(
                R.anim.fragment_slide_in_right,
                R.anim.fragment_slide_out_left
            )
            .setReorderingAllowed(true)
            .replace(R.id.child_fragment_container, fragment)
            .commit()
    }

    private fun incomingSceneIsAlreadyActive(scene: Scene): Boolean {
        val activeScene = childFragmentManager.findFragmentById(R.id.child_fragment_container)
        return (activeScene is SceneFragment<*> && activeScene.sceneViewModelId == scene.viewModel.id)
    }

    private fun saveActiveSceneViewState() {
        val activeScene = childFragmentManager.findFragmentById(R.id.child_fragment_container)
            as? SceneFragment<*> ?: return

        val viewModel = serviceLocator[activeScene.sceneViewModelId] as? PlainViewModel ?: return
        viewModel.fragmentSavedState =
            childFragmentManager.saveFragmentInstanceState(activeScene)
    }

    companion object {
        const val ARG_ROOT_CONTAINER_ID = "arg_root_container_id"

        fun newInstance(rootContainerId: UUID): RootContainerFragment {
            return RootContainerFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_ROOT_CONTAINER_ID, rootContainerId.toString())
                }
            }
        }
    }
}
