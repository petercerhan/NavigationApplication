package com.example.navigationapplication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
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

    //Lifecycle

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
            //This is because the modal container scene has visible=GONE in the xml layout file
            if (sceneState.modalScene != null) {
                showModalContainer()
            } else {
                hideModalContainer()
            }
            return
        }

        //save active scene view state requires a locatable view model; so this must be done before resetting the VM Locator
        saveActiveSceneViewState()
        saveActiveModalViewState()

        updateViewModelLocatorForIncomingSceneState(sceneState)

        val baseSceneChanged = sceneStateChangesBaseScene(sceneState)
        val initialStateContainsModal = initialStateContainsModal()
        val finalStateContainsModal = sceneStateContainsModal(sceneState)

        val sceneStateTransitionsBaseScene = (baseSceneChanged && !initialStateContainsModal && !finalStateContainsModal)
        val sceneStatePresentsModal = (!baseSceneChanged && !initialStateContainsModal && finalStateContainsModal)
        val sceneStateDismissesModal = (!baseSceneChanged && initialStateContainsModal && !finalStateContainsModal)

        if (sceneStateTransitionsBaseScene) {
            updateBaseSceneWithAnimation(sceneState)
        }
        else if (sceneStatePresentsModal && sceneState.modalScene != null) {
            presentModal(sceneState.modalScene)
        }
        else if (sceneStateDismissesModal) {
            dismissModal()
        }
        else {
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

    private fun updateViewModelLocatorForIncomingSceneState(sceneState: SceneState) {
        viewModelLocator.clear()
        viewModelLocator.cacheScene(sceneState.scene)
        if (sceneState.modalScene != null) {
            viewModelLocator.cacheScene(sceneState.modalScene)
        }
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
        val fragment = createFragmentForScene(sceneState.scene)
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


    //PresentModal()

    private fun presentModal(modalScene: Scene) {
        val fragment = createFragmentForScene(modalScene)
        showModalContainer()
        childFragmentManager.beginTransaction()
            .setCustomAnimations(R.anim.fragment_slide_in_bottom, 0, 0, 0)
            .setReorderingAllowed(true)
            .replace(R.id.modal_fragment_container, fragment)
            .commit()
    }


    //DismissModal()

    private fun dismissModal() {
        val modalFragment = getCurrentModalFragment() ?: run {
            hideModalContainer()
            return
        }
        if (childFragmentManager.isStateSaved) {
            return
        }

        val hideModalContainerAfterModalViewIsDestroyed = object : FragmentManager.FragmentLifecycleCallbacks() {
            override fun onFragmentViewDestroyed(fragmentManager: FragmentManager, fragment: Fragment) {
                if (fragment !== modalFragment) {
                    return
                }
                fragmentManager.unregisterFragmentLifecycleCallbacks(this)
                hideModalContainer()
            }
        }

        childFragmentManager.registerFragmentLifecycleCallbacks(hideModalContainerAfterModalViewIsDestroyed, false)
        childFragmentManager.beginTransaction()
            .setCustomAnimations(0, R.anim.fragment_slide_out_bottom, 0, 0)
            .setReorderingAllowed(true)
            .remove(modalFragment)
            .commit()
    }

    private fun getCurrentModalFragment(): Fragment? {
        return childFragmentManager.findFragmentById(R.id.modal_fragment_container)
    }


    //Shared Subroutines

    private fun createFragmentForScene(scene: Scene): Fragment {
        val fragment = SceneFragment.newInstance(
            scene.fragmentType,
            scene.viewModel.id.toString(),
        ).apply {
            setInitialSavedState(scene.viewModel.fragmentSavedState)
        }
        return fragment
    }

    private fun showModalContainer() {
        requireView().findViewById<View>(R.id.modal_fragment_container).visibility = View.VISIBLE
    }

    private fun hideModalContainer() {
        requireView().findViewById<View>(R.id.modal_fragment_container).visibility = View.GONE
    }

}
