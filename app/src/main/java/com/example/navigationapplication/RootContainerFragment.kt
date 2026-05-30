package com.example.navigationapplication

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
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
                    processIncomingSceneState(sceneState)
                }
            }
        }
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
        //set transaction in progreess flag on (system?) view model
        rootContainerSystemViewModel.setTransactionInProgress()
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
        if (transactionIsInProgress()) {
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
            presentModal(sceneState.modalScene)
        }
        else if (sceneStateDismissesModal) {
            Log.d("PETER CERHAN", "Case C Dismiss Modal")
            dismissModal()
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
        hideModalContainer()
        val outgoingFragment = childFragmentManager.findFragmentById(R.id.child_fragment_container)
        val incomingFragment = createFragmentForScene(sceneState.scene)
        val (newScreenEntryAnimation, priorScreenExitAnimation) = animationsFor(sceneState.animation)

        //callback to finalize transaction once animations finish
        if (outgoingFragment != null) {
            val clearTransactionInProgressAfterAnimation = object : FragmentManager.FragmentLifecycleCallbacks() {
                override fun onFragmentViewDestroyed(fragmentManager: FragmentManager, fragment: Fragment) {
                    if (fragment !== outgoingFragment) {
                        return
                    }
                    fragmentManager.unregisterFragmentLifecycleCallbacks(this)
//                    rootContainerSystemViewModel.transactionInProgress.tryEmit(false)
                }
            }
            childFragmentManager.registerFragmentLifecycleCallbacks(clearTransactionInProgressAfterAnimation, false)
        }

        //execute transaction with completion callback
        val transaction = childFragmentManager.beginTransaction()
            .setCustomAnimations(newScreenEntryAnimation, priorScreenExitAnimation)
            .setReorderingAllowed(true)
            .replace(R.id.child_fragment_container, incomingFragment)

        //In the case of the initial screen transaction where there is no outgoing fragment (so the outgoing animation complete callback does not execute)
        //We immediately set transactionInProgress to false
        if (outgoingFragment == null) {
            transaction.runOnCommit {
//                rootContainerSystemViewModel.transactionInProgress.tryEmit(false)
            }
        }

        transaction.commit()
    }

    private fun animationsFor(animation: SceneAnimation): Pair<Int, Int> =
        when (animation) {
            SceneAnimation.SlideFromRight -> R.anim.fragment_slide_in_right to R.anim.fragment_slide_out_left
            SceneAnimation.SlideFromLeft -> R.anim.fragment_slide_in_left to R.anim.fragment_slide_out_right
        }


    //PresentModal()

    private fun presentModal(modalScene: Scene) {
        showModalContainer()
        val fragment = createFragmentForScene(modalScene)
        val transaction = childFragmentManager.beginTransaction()
            .setCustomAnimations(R.anim.fragment_slide_in_bottom, 0, 0, 0)
            .setReorderingAllowed(true)
            .replace(R.id.modal_fragment_container, fragment)

        transaction.runOnCommit {
            blockModalInteractionUntilEnterAnimationCompletes(fragment.view) {
//                rootContainerSystemViewModel.transactionInProgress.tryEmit(false)
            }
        }

        transaction.commit()
    }

    /**
     * Fragment custom animations only affect drawing, not layout bounds. The modal view is
     * interactive at its final position immediately, so block touches until the enter animation
     * finishes.
     */
    private fun blockModalInteractionUntilEnterAnimationCompletes(
        view: View?,
        onAnimationComplete: () -> Unit,
    ) {
        if (view == null) {
            onAnimationComplete()
            return
        }

        view.isClickable = true
        view.setOnTouchListener { _, _ -> true }

        view.post {
            val animation = view.animation
            if (animation == null || animation.hasEnded()) {
                view.setOnTouchListener(null)
                onAnimationComplete()
                return@post
            }

            animation.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation?) {}

                override fun onAnimationEnd(animation: Animation?) {
                    view.setOnTouchListener(null)
                    onAnimationComplete()
                }

                override fun onAnimationRepeat(animation: Animation?) {}
            })
        }
    }


    //DismissModal()

    private fun dismissModal() {
        val modalFragment = getCurrentModalFragment() ?: run {
            hideModalContainer()
            return
        }

        //TODO: What is this code for?
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

                //Confirm this is the right place to execute this
//                rootContainerSystemViewModel.transactionInProgress.tryEmit(false)
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
