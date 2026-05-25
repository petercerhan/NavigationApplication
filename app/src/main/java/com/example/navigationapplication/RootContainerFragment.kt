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
import com.example.navigationapplication.controller_library.ApplicationViewModelLocator
import com.example.navigationapplication.controller_library.SceneAnimation
import java.util.UUID
import kotlinx.coroutines.launch

class RootContainerFragment : Fragment() {

    val activityViewModel: MainActivityViewModel by activityViewModels()

    private val viewModelLocator: ApplicationViewModelLocator
        get() = activityViewModel.viewModelLocator

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
                rootContainerSystemViewModel.sceneFlow.collect { sceneState ->
                    showScene(sceneState)
                }
            }
        }
    }

    private fun showScene(sceneState: SceneState) {
        val scene = sceneState.scene
        if (incomingSceneIsAlreadyActive(scene)) {
            return
        }

        saveActiveSceneViewState()

        //this will be replaced with a single call eventually
        viewModelLocator.clear()
        viewModelLocator.cacheScene(scene)
        //

        val fragment = SceneFragment.newInstance(
            scene.fragmentType,
            scene.viewModel.id.toString(),
        ).apply {
            setInitialSavedState(scene.viewModel.fragmentSavedState)
        }

        val (newScreenEntryAnimation, priorScreenExitAnimation) = animationsFor(sceneState.animation)
        childFragmentManager.beginTransaction()
            .setCustomAnimations(newScreenEntryAnimation, priorScreenExitAnimation)
            .setReorderingAllowed(true)
            .replace(R.id.child_fragment_container, fragment)
            .commit()
    }

    private fun animationsFor(animation: SceneAnimation): Pair<Int, Int> =
        when (animation) {
            SceneAnimation.SlideFromRight -> R.anim.fragment_slide_in_right to R.anim.fragment_slide_out_left
            SceneAnimation.SlideFromLeft -> R.anim.fragment_slide_in_left to R.anim.fragment_slide_out_right
        }

    private fun incomingSceneIsAlreadyActive(scene: Scene): Boolean {
        val activeScene = childFragmentManager.findFragmentById(R.id.child_fragment_container)
        return (activeScene is SceneFragment<*> && activeScene.sceneViewModelId == scene.viewModel.id)
    }

    private fun saveActiveSceneViewState() {
        val activeScene = childFragmentManager.findFragmentById(R.id.child_fragment_container)
            as? SceneFragment<*> ?: return

        val viewModel = viewModelLocator.viewModelForId(activeScene.sceneViewModelId) as? ApplicationViewModel ?: return
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
