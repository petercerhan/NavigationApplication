package com.example.navigationapplication

import android.os.Bundle
import android.util.Log
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
        //update ViewModelLocator
        //save active scene view state requires a locatable view model; so must be done before resetting the VM Locator
        saveActiveSceneViewState()
        //need to also save modal scene view state
        viewModelLocator.clear()
        viewModelLocator.cacheScene(sceneState.scene)

        //booleans
        val baseSceneChanged = sceneStateChangesBaseScene(sceneState)
        val initialStateContainsModal = initialStateContainsModal()
        val finalStateContainsModal = sceneStateContainsModal(sceneState)

//        Log.d("PETERCERHAN", "Made it a $baseSceneChanged $initialStateContainsModal $finalStateContainsModal")
        //no initial modal, no final modal, base change -> update base scene
        if (baseSceneChanged && !initialStateContainsModal && !finalStateContainsModal) {
            //standard navigation here
            updateBaseSceneWithAnimation(sceneState)
        }
        //no initial modal, final modal, stable base -> present modal
        else if (!baseSceneChanged && !initialStateContainsModal && finalStateContainsModal) {
            //present modal
        }
        //initial modal, no final modal, stable base -> dismiss modal
        else if (!baseSceneChanged && initialStateContainsModal && !finalStateContainsModal) {
            //dismiss Modal
        }
        //else, reset:set base and modal with no animation (ViewModalLocator already set
        else {
            //make sure final state reflects Scene State
        }

//        applyMainScene(sceneState)
//        applyModalScene(sceneState.modalScene)
    }

    private fun sceneStateChangesBaseScene(sceneState: SceneState): Boolean {
        val activeScene = childFragmentManager.findFragmentById(R.id.child_fragment_container)
        if (activeScene == null) {
            return true
        }
        return (activeScene is SceneFragment<*> && activeScene.sceneViewModelId != sceneState.scene.viewModel.id)
    }

    private fun initialStateContainsModal(): Boolean {
        val activeModal = childFragmentManager.findFragmentById(R.id.modal_fragment_container)
        return (activeModal != null)
    }

    private fun sceneStateContainsModal(sceneState: SceneState): Boolean {
        return (sceneState.modalScene != null)
    }


    private fun updateBaseSceneWithAnimation(sceneState: SceneState) {
        val scene = sceneState.scene
        if (incomingSceneIsAlreadyActive(scene)) {
            return
        }

        //Build Fragment for new Scene
        val fragment = SceneFragment.newInstance(
            scene.fragmentType,
            scene.viewModel.id.toString(),
        ).apply {
            setInitialSavedState(scene.viewModel.fragmentSavedState)
        }

        //Animate in new Scene
        val (newScreenEntryAnimation, priorScreenExitAnimation) = animationsFor(sceneState.animation)
        childFragmentManager.beginTransaction()
            .setCustomAnimations(newScreenEntryAnimation, priorScreenExitAnimation)
            .setReorderingAllowed(true)
            .replace(R.id.child_fragment_container, fragment)
            .commit()
    }


//    private fun applyMainScene(sceneState: SceneState) {
//        val scene = sceneState.scene
//        if (incomingSceneIsAlreadyActive(scene)) {
//            viewModelLocator.cacheScene(scene)
//            return
//        }
//
//        saveActiveSceneViewState()
//
//        //this will be replaced with a single call eventually
//        viewModelLocator.clear()
//        viewModelLocator.cacheScene(scene)
//        //
//
//        val fragment = SceneFragment.newInstance(
//            scene.fragmentType,
//            scene.viewModel.id.toString(),
//        ).apply {
//            setInitialSavedState(scene.viewModel.fragmentSavedState)
//        }
//
//        val (newScreenEntryAnimation, priorScreenExitAnimation) = animationsFor(sceneState.animation)
//        childFragmentManager.beginTransaction()
//            .setCustomAnimations(newScreenEntryAnimation, priorScreenExitAnimation)
//            .setReorderingAllowed(true)
//            .replace(R.id.child_fragment_container, fragment)
//            .commit()
//    }

    //will be replaced by other logic above
    private fun incomingSceneIsAlreadyActive(scene: Scene): Boolean {
        val activeScene = childFragmentManager.findFragmentById(R.id.child_fragment_container)
        return (activeScene is SceneFragment<*> && activeScene.sceneViewModelId == scene.viewModel.id)
    }
    /////

    private fun saveActiveSceneViewState() {
        val activeScene = childFragmentManager.findFragmentById(R.id.child_fragment_container)
                as? SceneFragment<*> ?: return

        val viewModel = viewModelLocator.viewModelForId(activeScene.sceneViewModelId) as? ApplicationViewModel ?: return
        viewModel.fragmentSavedState =
            childFragmentManager.saveFragmentInstanceState(activeScene)
    }

    private fun animationsFor(animation: SceneAnimation): Pair<Int, Int> =
        when (animation) {
            SceneAnimation.SlideFromRight -> R.anim.fragment_slide_in_right to R.anim.fragment_slide_out_left
            SceneAnimation.SlideFromLeft -> R.anim.fragment_slide_in_left to R.anim.fragment_slide_out_right
        }


    //Modal Mechanics

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


    private fun incomingModalIsAlreadyActive(modalScene: Scene): Boolean {
        val activeModal = childFragmentManager.findFragmentById(R.id.modal_fragment_container)
        return (activeModal is SceneFragment<*> && activeModal.sceneViewModelId == modalScene.viewModel.id)
    }

    private fun saveActiveModalViewState() {
        val activeModal = childFragmentManager.findFragmentById(R.id.modal_fragment_container)
            as? SceneFragment<*> ?: return

        val viewModel = viewModelLocator.viewModelForId(activeModal.sceneViewModelId) as? ApplicationViewModel ?: return
        viewModel.fragmentSavedState =
            childFragmentManager.saveFragmentInstanceState(activeModal)
    }


    //Initialization Helper

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
