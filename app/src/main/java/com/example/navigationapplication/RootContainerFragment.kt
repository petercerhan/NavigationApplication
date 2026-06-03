package com.example.navigationapplication

import android.os.Bundle
import android.util.Log
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
import com.example.navigationapplication.controller_library.LocatorViewModel
import com.example.navigationapplication.controller_library.ModalDismissalAnimation
import com.example.navigationapplication.controller_library.ModalPresentationAnimation
import com.example.navigationapplication.controller_library.SceneTransitionAnimation
import java.util.UUID
import kotlinx.coroutines.launch

class RootContainerFragment : Fragment() {

    val parentServiceLocatorViewModel: MainActivityViewModel by viewModels(
        ownerProducer = { parentFragment ?: requireActivity() }
    )

    val serviceLocatorViewModel: LocatorViewModel by viewModels()

    private val rootContainerServiceLocator: ApplicationViewModelLocator
        get() = parentServiceLocatorViewModel.viewModelLocator

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
                    serviceLocator = parentServiceLocatorViewModel.viewModelLocator,
                ) as T
            }
        }
    }

    private val viewModelLocator: ApplicationViewModelLocator
        get() = serviceLocatorViewModel.viewModelLocator

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
                launch {
                    rootContainerSystemViewModel.transactionInProgress.collect { inProgress ->
                        updateTransactionInputBlocker(inProgress)
                    }
                }
                launch {
                    rootContainerSystemViewModel.sceneFlow.collect { sceneState ->
                        processIncomingSceneState(sceneState)
                    }
                }
            }
        }
    }

    private fun updateTransactionInputBlocker(blockInput: Boolean) {
        requireView().findViewById<View>(R.id.transaction_input_blocker).visibility =
            if (blockInput) View.VISIBLE else View.GONE
    }

    private fun processIncomingSceneState(sceneState: SceneState) {
        Log.d("PETER CERHAN", "Evaluate Scene State")
        //Always set initial modal container visibility for currently active scene state
        //This ensures that re-evaluated sceneState due to a configuration change has modal container visibility set correctly
        //Because this property defaults to GONE as set in the xml resource file
        setInitialModalContainerSceneState()

        if (!shouldAcceptIncomingSceneState(sceneState)) {
            Log.d("PETER CERHAN", "Reject Scene State")
            return
        }

        //cache scene state on (system?) view model
        rootContainerSystemViewModel.activeSceneState = sceneState
        //call transition execution routine
        transitionToSceneState(sceneState)
    }

    private fun setInitialModalContainerSceneState() {
        val activeSceneState = rootContainerSystemViewModel.activeSceneState
        if (activeSceneState == null)  {
            hideModalContainer()
        } else if (activeSceneState.modalScene == null) {
            hideModalContainer()
        } else {
            showModalContainer()
        }
    }

    private fun shouldAcceptIncomingSceneState(incomingSceneState: SceneState): Boolean {
        if (childFragmentManager.isStateSaved) {
            Log.d("PETER CERHAN", "Reject Scene State: Fragment State Saved")
            return false
        } else if (transactionIsInProgress()) {
            Log.d("PETER CERHAN", "Reject Scene State: Transaction in Progress")
            return false
        } else if (incomingSceneStateMatchesActiveSceneState(incomingSceneState)) {
            Log.d("PETER CERHAN", "Reject Scene State: Incoming Matches Active")
            return false
        } else {
            return true
        }
    }

    private fun transactionIsInProgress(): Boolean {
        return rootContainerSystemViewModel.transactionInProgress.value
    }

    private fun incomingSceneStateMatchesActiveSceneState(incomingSceneState: SceneState): Boolean {
        val activeSceneState = rootContainerSystemViewModel.activeSceneState ?: return false
        return (incomingSceneState.scene.viewModel.id == activeSceneState.scene.viewModel.id &&
                incomingSceneState.modalScene?.viewModel?.id == activeSceneState.modalScene?.viewModel?.id)
    }

    private fun transitionToSceneState(sceneState: SceneState) {
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
            Log.d("PETER CERHAN", "Case A show()")
            updateBaseSceneWithAnimation(sceneState)
        }
        else if (sceneStatePresentsModal && sceneState.modalScene != null) {
            Log.d("PETER CERHAN", "Case B Present Modal")
            presentModal(sceneState)
        }
        else if (sceneStateDismissesModal) {
            Log.d("PETER CERHAN", "Case C Dismiss Modal")
            dismissModal(sceneState)
        }
        else {
            Log.d("PETER CERHAN", "Case D Reject")
            //implementation depends on how self-recovering this component is - we could simply throw a fatal error here
        }
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
        val outgoingFragment = childFragmentManager.findFragmentById(R.id.child_fragment_container)
        val incomingFragment = createFragmentForScene(sceneState.scene)
        val (newScreenEntryAnimation, priorScreenExitAnimation, animationDuration) = animationsParametersFor(sceneState.sceneTransitionAnimation)

        rootContainerSystemViewModel.setTransactionInProgress(animationDuration)
        hideModalContainer()

        //execute transaction with completion callback
        val transaction = childFragmentManager.beginTransaction()
            .setCustomAnimations(newScreenEntryAnimation, priorScreenExitAnimation)
            .setReorderingAllowed(true)
            .replace(R.id.child_fragment_container, incomingFragment)

        //In the case of the initial screen transaction where there is no outgoing fragment (so the outgoing animation complete callback does not execute)
        //We immediately set transactionInProgress to false
        transaction.commit()
    }

    private fun animationsParametersFor(animation: SceneTransitionAnimation): Triple<Int, Int, Long> =
        when (animation) {
            SceneTransitionAnimation.SlideFromRight -> Triple(R.anim.fragment_slide_in_right, R.anim.fragment_slide_out_left, 300L)
            SceneTransitionAnimation.SlideFromLeft -> Triple(R.anim.fragment_slide_in_left, R.anim.fragment_slide_out_right, 300L)
            SceneTransitionAnimation.NoAnimation -> Triple(0, 0, 0L)
        }


    //PresentModal()

    private fun presentModal(sceneState: SceneState) {
        val modalScene = sceneState.modalScene ?: return
        val (newScreenEntryAnimation, animationDuration) = modalPresentationAnimationsParametersFor(sceneState.modalPresentationAnimation)

        rootContainerSystemViewModel.setTransactionInProgress(animationDuration)
        showModalContainer()
        val fragment = createFragmentForScene(modalScene)
        val transaction = childFragmentManager.beginTransaction()
            .setCustomAnimations(newScreenEntryAnimation, 0, 0, 0)
            .setReorderingAllowed(true)
            .replace(R.id.modal_fragment_container, fragment)

        transaction.commit()
    }

    private fun modalPresentationAnimationsParametersFor(animation: ModalPresentationAnimation?): Pair<Int, Long> =
        when (animation) {
            ModalPresentationAnimation.CoverFromBottom -> Pair(R.anim.fragment_slide_in_bottom, 300L)
            null -> Pair(0, 0L)
        }


    //DismissModal()

    private fun dismissModal(sceneState: SceneState) {
        val modalFragment = getCurrentModalFragment() ?: run {
            hideModalContainer()
            return
        }

        val (priorScreenExitAnimation, animationDuration) = modalDismissalAnimationsParametersFor(sceneState.modalDismissalAnimation)
        rootContainerSystemViewModel.setTransactionInProgress(animationDuration)

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
            .setCustomAnimations(0, priorScreenExitAnimation, 0, 0)
            .setReorderingAllowed(true)
            .remove(modalFragment)
            .commit()
    }

    private fun modalDismissalAnimationsParametersFor(animation: ModalDismissalAnimation?): Pair<Int, Long> =
        when (animation) {
            ModalDismissalAnimation.UncoverDown -> Pair(R.anim.fragment_slide_out_bottom, 300L)
            null -> Pair(0, 0L)
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
