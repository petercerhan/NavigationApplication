package com.example.navigationapplication.controller_library.container

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.navigationapplication.R
import com.example.navigationapplication.controller_library.ApplicationViewModel
import com.example.navigationapplication.controller_library.ServiceLocator
import com.example.navigationapplication.controller_library.ServiceLocatorViewModel
import com.example.navigationapplication.controller_library.container.animations.ModalDismissalAnimation
import com.example.navigationapplication.controller_library.container.animations.ModalPresentationAnimation
import com.example.navigationapplication.controller_library.SceneFragment
import com.example.navigationapplication.controller_library.container.animations.SceneTransitionAnimation
import kotlinx.coroutines.launch

class ContainerFragment : SceneFragment<ContainerViewModel>() {

    override fun backButtonAction() {
        //unreachable
    }

    val serviceLocatorViewModel: ServiceLocatorViewModel by viewModels()

    val containerFrameworkViewModel: ContainerFrameworkViewModel by viewModels()

    private val serviceLocator: ServiceLocator
        get() = serviceLocatorViewModel.serviceLocator

    private lateinit var transactionInputBlocker: View

    //Lifecycle

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val root = inflater.inflate(R.layout.fragment_container, container, false) as FrameLayout
        transactionInputBlocker = View(requireContext()).apply {
            id = View.generateViewId()
            layoutParams = FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT,
            )
            isClickable = true
            isFocusable = true
            importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_NO_HIDE_DESCENDANTS
            visibility = View.GONE
        }
        root.addView(transactionInputBlocker)
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindBackButtonHandling()
        bindTransactionInProgress()
        bindSceneState()
    }

    //Back Button Handling

    private fun bindBackButtonHandling() {
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    dispatchBack()
                }
            },
        )
    }

    private fun dispatchBack() {
        if (transactionIsInProgress()) {
            return
        }

        val modalScene = getCurrentModalFragment() as? SceneFragment<*>
        if (modalScene != null) {
            modalScene.backButtonAction()
            return
        }

        val baseScene = childFragmentManager.findFragmentById(R.id.child_fragment_container)
                as? SceneFragment<*>
        baseScene?.backButtonAction()
    }

    //Transaction In Progress Handling

    private fun bindTransactionInProgress() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                containerFrameworkViewModel.transactionInProgress.collect { inProgress ->
                    updateTransactionInputBlocker(inProgress)
                }
            }
        }
    }

    private fun updateTransactionInputBlocker(blockInput: Boolean) {
        transactionInputBlocker.visibility = if (blockInput) View.VISIBLE else View.GONE
    }

    //Scene State Transition Handling

    private fun bindSceneState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.sceneStateFlow.collect { sceneState ->
                    processIncomingSceneState(sceneState)
                }
            }
        }
    }

    private fun processIncomingSceneState(sceneState: SceneState) {
        viewModel.logger.log("Evaluate Scene State")
        //Always set initial modal container visibility for currently active scene state
        //This ensures that re-evaluated sceneState due to a configuration change has modal container visibility set correctly
        //Because this property defaults to GONE as set in the xml resource file
        setInitialModalContainerSceneState()

        if (!shouldAcceptIncomingSceneState(sceneState)) {
            viewModel.logger.log("Reject Scene State")
            return
        }

        containerFrameworkViewModel.activeSceneState = sceneState
        transitionToSceneState(sceneState)
    }

    private fun setInitialModalContainerSceneState() {
        val activeSceneState = containerFrameworkViewModel.activeSceneState
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
            viewModel.logger.log("Reject Scene State: Fragment State Saved")
            return false
        } else if (transactionIsInProgress()) {
            viewModel.logger.log("Reject Scene State: Transaction in Progress")
            return false
        } else if (incomingSceneStateMatchesCurrentActiveSceneState(incomingSceneState)) {
            viewModel.logger.log("Reject Scene State: incoming scene state matches currently active scene state")
            return false
        } else {
            return true
        }
    }

    private fun transactionIsInProgress(): Boolean {
        return containerFrameworkViewModel.transactionInProgress.value
    }

    private fun incomingSceneStateMatchesCurrentActiveSceneState(incomingSceneState: SceneState): Boolean {
        val currentActiveSceneState = containerFrameworkViewModel.activeSceneState ?: return false
        return currentActiveSceneState.hasIdenticalScenesAs(incomingSceneState)
    }

    private fun transitionToSceneState(sceneState: SceneState) {
        //save active scene view state requires a locatable view model; so this must be done before resetting the Service Locator which locates those View Models
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
            viewModel.logger.log("Case A show()")
            updateBaseSceneWithAnimation(sceneState)
        }
        else if (sceneStatePresentsModal) {
            viewModel.logger.log("Case B Present Modal")
            presentModal(sceneState)
        }
        else if (sceneStateDismissesModal) {
            viewModel.logger.log("Case C Dismiss Modal")
            dismissModal(sceneState)
        }
        else {
            viewModel.logger.log("Case D Reject")
            //implementation depends on how self-recovering this component is - we could simply throw a fatal error here
        }
    }

    private fun saveActiveSceneViewState() {
        val activeScene = childFragmentManager.findFragmentById(R.id.child_fragment_container)
                as? SceneFragment<*> ?: return

        val viewModel = serviceLocator.viewModelForId(activeScene.viewModelId) as? ApplicationViewModel
            ?: return
        viewModel.fragmentSavedState =
            childFragmentManager.saveFragmentInstanceState(activeScene)
    }

    private fun saveActiveModalViewState() {
        val activeModal = childFragmentManager.findFragmentById(R.id.modal_fragment_container)
                as? SceneFragment<*> ?: return

        val viewModel = serviceLocator.viewModelForId(activeModal.viewModelId) as? ApplicationViewModel
            ?: return
        viewModel.fragmentSavedState =
            childFragmentManager.saveFragmentInstanceState(activeModal)
    }

    private fun updateViewModelLocatorForIncomingSceneState(sceneState: SceneState) {
        serviceLocator.clear()
        serviceLocator.cacheScene(sceneState.scene)
        if (sceneState.modalScene != null) {
            serviceLocator.cacheScene(sceneState.modalScene)
        }
    }

    private fun sceneStateChangesBaseScene(sceneState: SceneState): Boolean {
        val activeScene = childFragmentManager.findFragmentById(R.id.child_fragment_container)
        if (activeScene == null) {
            return true
        }
        return (activeScene is SceneFragment<*> && activeScene.viewModelId != sceneState.scene.viewModel.id)
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
        val incomingFragment = createFragmentForScene(sceneState.scene)
        val (newScreenEntryAnimation, priorScreenExitAnimation, animationDuration) = animationsParametersFor(sceneState.sceneTransitionAnimation)

        containerFrameworkViewModel.setTransactionInProgress(animationDuration)
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

        containerFrameworkViewModel.setTransactionInProgress(animationDuration)
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
        containerFrameworkViewModel.setTransactionInProgress(animationDuration)

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
        val fragment = newInstance(
            scene.fragmentType,
            scene.viewModel.id,
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
