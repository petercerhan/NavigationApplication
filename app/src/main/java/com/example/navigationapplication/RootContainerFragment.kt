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
        //if no implied change - base scene is unchanged, modal scene is unchanged - short circuit and return
        //This happens on configuration change
        if (incomingBaseSceneMatchesActiveBaseScene(sceneState.scene) && incomingModalMatchesActiveModal(sceneState.modalScene)) {
            //In this path, we need to set the modal container to visible if there is a modal in the current scene state
            //
            if (sceneState.modalScene != null) {
                showModalContainer()
            } else {
                hideModalContainer()
            }
            return
        }

        //save active scene view state requires a locatable view model; so must be done before resetting the VM Locator
        saveActiveSceneViewState()
        saveActiveModalViewState()

        //update ViewModelLocator
        viewModelLocator.clear()
        viewModelLocator.cacheScene(sceneState.scene)
        if (sceneState.modalScene != null) {
            viewModelLocator.cacheScene(sceneState.modalScene)
        }

        val baseSceneChanged = sceneStateChangesBaseScene(sceneState)
        val initialStateContainsModal = initialStateContainsModal()
        val finalStateContainsModal = sceneStateContainsModal(sceneState)

        val sceneStateTransitionsBaseScene = (baseSceneChanged && !initialStateContainsModal && !finalStateContainsModal)
        val sceneStatePresentsModal = (!baseSceneChanged && !initialStateContainsModal && finalStateContainsModal)
        val sceneStateDismissesModal = (!baseSceneChanged && initialStateContainsModal && !finalStateContainsModal)

//        Log.d("PETERCERHAN", "Enter navigation cases $baseSceneChanged $initialStateContainsModal $finalStateContainsModal")
        if (sceneStateTransitionsBaseScene) {
            updateBaseSceneWithAnimation(sceneState)
        }
        else if (sceneStatePresentsModal && sceneState.modalScene != null) {
            presentModal(sceneState.modalScene)
        }
        else if (sceneStateDismissesModal) {
            dismissModal_new()
        }
        else {
            //make sure final state reflects Scene State?
            //implementation depends on how self-recovering this is - we can simply throw a fatal error here
        }
    }


    private fun incomingBaseSceneMatchesActiveBaseScene(scene: Scene): Boolean {
        val activeScene = childFragmentManager.findFragmentById(R.id.child_fragment_container)
        return (activeScene is SceneFragment<*> && activeScene.sceneViewModelId == scene.viewModel.id)
    }

    private fun incomingModalMatchesActiveModal(incomingModal: Scene?): Boolean {
        val activeModal = childFragmentManager.findFragmentById(R.id.modal_fragment_container)
        if (activeModal == null && incomingModal == null) {
            return true
        }
        if (incomingModal == null) {
            return false
        }
        return (activeModal is SceneFragment<*> && activeModal.sceneViewModelId == incomingModal.viewModel.id)
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


    //Show()

    private fun updateBaseSceneWithAnimation(sceneState: SceneState) {
        val scene = sceneState.scene

        //Replace this check with a different approach?
//        if (incomingSceneIsAlreadyActive(scene)) {
//            return
//        }

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


    //PresentModal()

    private fun presentModal(modalScene: Scene) {
        //Build fragment for new scene
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


    //DismissModal()

    private fun dismissModal_new() {
        val modalFragment = getCurrentModalFragment()
        val modalView = modalFragment?.view
        if (modalFragment == null || modalView == null) {
            return
        }

        //animate out modal, and remove container only after the animation is finished
        val exitAnimation = AnimationUtils.loadAnimation(requireContext(), R.anim.fragment_slide_out_bottom)
        exitAnimation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) = Unit
            override fun onAnimationRepeat(animation: Animation?) = Unit

            override fun onAnimationEnd(animation: Animation?) {
                if (!isAdded) return
                childFragmentManager.beginTransaction()
                    .remove(modalFragment)
                    .commit()
                hideModalContainer()
            }
        })
        modalView.startAnimation(exitAnimation)
    }

    private fun getCurrentModalFragment(): Fragment? {
        return childFragmentManager.findFragmentById(R.id.modal_fragment_container)
    }

    private fun hideModalContainer() {
        requireView().findViewById<View>(R.id.modal_fragment_container).visibility = View.GONE
    }



    //Where to factor these:

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

    private fun animationsFor(animation: SceneAnimation): Pair<Int, Int> =
        when (animation) {
            SceneAnimation.SlideFromRight -> R.anim.fragment_slide_in_right to R.anim.fragment_slide_out_left
            SceneAnimation.SlideFromLeft -> R.anim.fragment_slide_in_left to R.anim.fragment_slide_out_right
        }

    ////





    //Initialization

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

//    private fun dismissModal() {
//        saveActiveModalViewState()
//
//        val modalContainer = requireView().findViewById<View>(R.id.modal_fragment_container)
//        val modal = childFragmentManager.findFragmentById(R.id.modal_fragment_container) ?: run {
//            modalContainer.visibility = View.GONE
//            return
//        }
//
//        val modalView = modal.view
//        if (modalView == null) {
//            childFragmentManager.beginTransaction()
//                .remove(modal)
//                .commit()
//            modalContainer.visibility = View.GONE
//            return
//        }
//
//        val exitAnimation = AnimationUtils.loadAnimation(requireContext(), R.anim.fragment_slide_out_bottom)
//        exitAnimation.setAnimationListener(object : Animation.AnimationListener {
//            override fun onAnimationStart(animation: Animation?) = Unit
//
//            override fun onAnimationRepeat(animation: Animation?) = Unit
//
//            override fun onAnimationEnd(animation: Animation?) {
//                if (!isAdded) return
//                childFragmentManager.beginTransaction()
//                    .remove(modal)
//                    .commit()
//                modalContainer.visibility = View.GONE
//            }
//        })
//        modalView.startAnimation(exitAnimation)
//    }
    //Modal Mechanics Prior

//    private fun applyModalScene(modalScene: Scene?) {
//        if (modalScene == null) {
////            dismissModal()
//            return
//        }
//
//        if (incomingModalIsAlreadyActive(modalScene)) {
//            viewModelLocator.cacheScene(modalScene)
//            showModalContainer()
//            return
//        }
//
//        saveActiveModalViewState()
//        viewModelLocator.cacheScene(modalScene)
//
//        val fragment = SceneFragment.newInstance(
//            modalScene.fragmentType,
//            modalScene.viewModel.id.toString(),
//        ).apply {
//            setInitialSavedState(modalScene.viewModel.fragmentSavedState)
//        }
//
//        showModalContainer()
//        childFragmentManager.beginTransaction()
//            .setCustomAnimations(R.anim.fragment_slide_in_bottom, 0, 0, 0)
//            .setReorderingAllowed(true)
//            .replace(R.id.modal_fragment_container, fragment)
//            .commit()
//    }
}
