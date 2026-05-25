package com.example.navigationapplication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
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
                    applySceneState(sceneState)
                }
            }
        }
    }

    private fun applySceneState(sceneState: SceneState) {
        applyMainScene(sceneState)
        applyModalScene(sceneState.modalScene)
    }

    private fun applyMainScene(sceneState: SceneState) {
        val scene = sceneState.scene
        if (incomingSceneIsAlreadyActive(scene)) {
            viewModelLocator.cacheScene(scene)
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

    private fun applyModalScene(modalScene: Scene?) {
        if (modalScene == null) {
            dismissModal()
            return
        }

        if (incomingModalIsAlreadyActive(modalScene)) {
            viewModelLocator.cacheScene(modalScene)
            showModalContainer()
            return
        }

        saveActiveModalViewState()
        viewModelLocator.cacheScene(modalScene)

        val fragment = SceneFragment.newInstance(
            modalScene.fragmentType,
            modalScene.viewModel.id.toString(),
        ).apply {
            setInitialSavedState(modalScene.viewModel.fragmentSavedState)
        }

        showModalContainer()
        childFragmentManager.beginTransaction()
            .setCustomAnimations(R.anim.fragment_slide_in_bottom, 0, 0, 0)
            .setReorderingAllowed(true)
            .replace(R.id.modal_fragment_container, fragment)
            .commit()
    }

    private fun showModalContainer() {
        requireView().findViewById<View>(R.id.modal_fragment_container).visibility = View.VISIBLE
    }

    private fun dismissModal() {
        saveActiveModalViewState()

        val modalContainer = requireView().findViewById<View>(R.id.modal_fragment_container)
        val modal = childFragmentManager.findFragmentById(R.id.modal_fragment_container) ?: run {
            modalContainer.visibility = View.GONE
            return
        }

        val modalView = modal.view
        if (modalView == null) {
            childFragmentManager.beginTransaction()
                .remove(modal)
                .commit()
            modalContainer.visibility = View.GONE
            return
        }

        val exitAnimation = AnimationUtils.loadAnimation(requireContext(), R.anim.fragment_slide_out_bottom)
        exitAnimation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) = Unit

            override fun onAnimationRepeat(animation: Animation?) = Unit

            override fun onAnimationEnd(animation: Animation?) {
                if (!isAdded) return
                childFragmentManager.beginTransaction()
                    .remove(modal)
                    .commit()
                modalContainer.visibility = View.GONE
            }
        })
        modalView.startAnimation(exitAnimation)
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

    private fun incomingModalIsAlreadyActive(modalScene: Scene): Boolean {
        val activeModal = childFragmentManager.findFragmentById(R.id.modal_fragment_container)
        return (activeModal is SceneFragment<*> && activeModal.sceneViewModelId == modalScene.viewModel.id)
    }

    private fun saveActiveSceneViewState() {
        val activeScene = childFragmentManager.findFragmentById(R.id.child_fragment_container)
            as? SceneFragment<*> ?: return

        val viewModel = viewModelLocator.viewModelForId(activeScene.sceneViewModelId) as? ApplicationViewModel ?: return
        viewModel.fragmentSavedState =
            childFragmentManager.saveFragmentInstanceState(activeScene)
    }

    private fun saveActiveModalViewState() {
        val activeModal = childFragmentManager.findFragmentById(R.id.modal_fragment_container)
            as? SceneFragment<*> ?: return

        val viewModel = viewModelLocator.viewModelForId(activeModal.sceneViewModelId) as? ApplicationViewModel ?: return
        viewModel.fragmentSavedState =
            childFragmentManager.saveFragmentInstanceState(activeModal)
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
