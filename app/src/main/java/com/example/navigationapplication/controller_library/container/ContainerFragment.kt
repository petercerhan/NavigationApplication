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
import com.example.navigationapplication.controller_library.container.animations.BaseSceneTransitionAnimation
import kotlinx.coroutines.launch

open class ContainerFragment<VM : ContainerViewModel> : SceneFragment<VM>() {

    override fun backButtonAction() {
        //unreachable
    }

    protected open val layoutRes: Int
        get() = R.layout.fragment_container

    val serviceLocatorViewModel: ServiceLocatorViewModel by viewModels()

    val containerFrameworkViewModel: ContainerFrameworkViewModel by viewModels()

    private val serviceLocator: ServiceLocator
        get() = serviceLocatorViewModel.serviceLocator

    private lateinit var interactionBlocker: View

    //Lifecycle

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val root = inflater.inflate(layoutRes, container, false) as FrameLayout
        interactionBlocker = View(requireContext()).apply {
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
        root.addView(interactionBlocker)
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

        val modalScene = getCurrentModalFragment()
        if (modalScene != null) {
            modalScene.backButtonAction()
            return
        }

        getCurrentBaseScene()?.backButtonAction()
    }

    private fun transactionIsInProgress(): Boolean {
        return containerFrameworkViewModel.transactionInProgress.value
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
        interactionBlocker.visibility = if (blockInput) View.VISIBLE else View.GONE
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

        //Always set initial modal container visibility before evaluating guards
        //This ensures that after a configuration change, the modal container visibility is set correctly
        //Even though the re-emitted scene state will not pass the guard below to prevent re-transitioning after a configuration change
        //Modal Container visibility defaults to GONE as set in the xml resource file - need it to be visible if active state includes a modal
        setModalContainerVisibilityForInitialSceneState()

        if (childFragmentManager.isStateSaved) {
            //Child fragment manager cannot receive new fragments after this point in its lifecycle
            viewModel.logger.log("Reject Scene State: Fragment Manager isStateSaved=true")
            return
        }

        if (incomingSceneStateMatchesCurrentActiveSceneState(sceneState)) {
            //On configuration change, the Container Fragment is recreated and the collector re-emits its last value; we get a repeated scene state and intercept it here
            viewModel.logger.log("Reject Scene State: incoming scene state matches currently active scene state")
            return
        }

        if (!viewModelAndFragmentAgreeOnOutgoingSceneState(sceneState)) {
            //ContainerFragment and ContainerViewModel states are inconsistent - restart app to avoid errors
            viewModel.logger.log("Reject Scene State: outgoing scene state inconsistent between View Model and Fragment")
            requestAppRelaunch()
            return
        }

        transitionToSceneState(sceneState)
    }

    private fun setModalContainerVisibilityForInitialSceneState() {
        val activeSceneState = containerFrameworkViewModel.activeSceneState
        if (activeSceneState == null)  {
            hideModalContainer()
        } else if (activeSceneState.modalScene == null) {
            hideModalContainer()
        } else {
            showModalContainer()
        }
    }

    private fun viewModelAndFragmentAgreeOnOutgoingSceneState(incomingSceneState: SceneState): Boolean {
        val outgoingSceneState = containerFrameworkViewModel.activeSceneState
        return (outgoingSceneState?.id == incomingSceneState.previousState?.id)
    }

    private fun incomingSceneStateMatchesCurrentActiveSceneState(incomingSceneState: SceneState): Boolean {
        val currentActiveSceneState = containerFrameworkViewModel.activeSceneState ?: return false
        return currentActiveSceneState.id == incomingSceneState.id
    }

    private fun transitionToSceneState(sceneState: SceneState) {
        //save active scene view state requires a locatable view model; so this must be done before resetting the Service Locator which locates those View Models
        saveBaseSceneViewState()
        saveModalSceneViewState()

        updateViewModelLocatorForIncomingSceneState(sceneState)

        when (sceneState.transitionType) {
            SceneStateTransitionType.TransitionBaseScene -> {
                updateBaseSceneWithAnimation(sceneState)
            }
            SceneStateTransitionType.PresentModal -> {
                presentModal(sceneState)
            }
            SceneStateTransitionType.DismissModal -> {
                dismissModal(sceneState)
            }
        }

        containerFrameworkViewModel.activeSceneState = sceneState
    }

    private fun saveBaseSceneViewState() {
        val baseSceneFragment = childFragmentManager.findFragmentById(R.id.child_fragment_container)
                as? SceneFragment<*> ?: return

        val viewModel = serviceLocator.viewModelForId(baseSceneFragment.viewModelId) as? ApplicationViewModel
            ?: return
        viewModel.fragmentSavedState =
            childFragmentManager.saveFragmentInstanceState(baseSceneFragment)
    }

    private fun saveModalSceneViewState() {
        val modalSceneFragment = childFragmentManager.findFragmentById(R.id.modal_fragment_container)
                as? SceneFragment<*> ?: return

        val viewModel = serviceLocator.viewModelForId(modalSceneFragment.viewModelId) as? ApplicationViewModel
            ?: return
        viewModel.fragmentSavedState =
            childFragmentManager.saveFragmentInstanceState(modalSceneFragment)
    }

    private fun updateViewModelLocatorForIncomingSceneState(sceneState: SceneState) {
        serviceLocator.reset()
        serviceLocator.registerViewModel(sceneState.baseScene.viewModel)
        if (sceneState.modalScene != null) {
            serviceLocator.registerViewModel(sceneState.modalScene.viewModel)
        }
    }

    //Show()

    private fun updateBaseSceneWithAnimation(sceneState: SceneState) {
        val incomingFragment = createFragmentForScene(sceneState.baseScene)
        val animationValues = sceneState.baseSceneTransitionAnimation ?: BaseSceneTransitionAnimation.NoAnimation

        setTransactionInProgress(animationValues.duration)

        val transaction = childFragmentManager.beginTransaction()
            .setCustomAnimations(animationValues.enterAnimation, animationValues.exitAnimation)
            .setReorderingAllowed(true)
            .replace(R.id.child_fragment_container, incomingFragment)

        transaction.commit()
    }


    //PresentModal()

    private fun presentModal(sceneState: SceneState) {
        val modalScene = sceneState.modalScene ?: return
        val animationValues = sceneState.modalPresentationAnimation ?: ModalPresentationAnimation.NoAnimation

        setTransactionInProgress(animationValues.duration)
        showModalContainer()
        val fragment = createFragmentForScene(modalScene)
        val transaction = childFragmentManager.beginTransaction()
            .setCustomAnimations(animationValues.enterAnimation, 0, 0, 0)
            .setReorderingAllowed(true)
            .replace(R.id.modal_fragment_container, fragment)

        transaction.commit()
    }


    //DismissModal()

    private fun dismissModal(sceneState: SceneState) {
        val modalFragment = getCurrentModalFragment() ?: run {
            hideModalContainer()
            return
        }
        val animationValues = sceneState.modalDismissalAnimation ?: ModalDismissalAnimation.NoAnimation

        registerCallbackToHideModalContainerOnModalFragmentRemoval(modalFragment)

        setTransactionInProgress(animationValues.duration)
        childFragmentManager.beginTransaction()
            .setCustomAnimations(0, animationValues.exitAnimation, 0, 0)
            .setReorderingAllowed(true)
            .remove(modalFragment)
            .commit()
    }

    private fun registerCallbackToHideModalContainerOnModalFragmentRemoval(modalFragment: Fragment) {
        val hideModalContainerAfterModalViewIsDestroyed = object : FragmentManager.FragmentLifecycleCallbacks() {
            override fun onFragmentViewDestroyed(fragmentManager: FragmentManager, fragment: Fragment) {
                if (fragment !== modalFragment) {
                    return
                }
                fragmentManager.unregisterFragmentLifecycleCallbacks(this)
                // Guard against possibility of view already being null (theoretical but not likely possibility according to agent review)
                if (view != null) {
                    hideModalContainer()
                }
            }
        }
        childFragmentManager.registerFragmentLifecycleCallbacks(hideModalContainerAfterModalViewIsDestroyed, false)
    }


    //Shared Subroutines

    private fun getCurrentModalFragment(): SceneFragment<*>? {
        return childFragmentManager.findFragmentById(R.id.modal_fragment_container) as? SceneFragment<*>
    }

    fun getCurrentBaseScene(): SceneFragment<*>? {
        return childFragmentManager.findFragmentById(R.id.child_fragment_container) as? SceneFragment<*>
    }

    private fun createFragmentForScene(scene: Scene): Fragment {
        val fragment = newInstance(
            scene.fragmentType,
            scene.viewModel.id,
        ).apply {
            setInitialSavedState(scene.viewModel.fragmentSavedState)
        }
        return fragment
    }

    private fun setTransactionInProgress(animationDuration: Long) {
        containerFrameworkViewModel.setTransactionInProgress(animationDuration)
    }

    private fun showModalContainer() {
        requireView().findViewById<View>(R.id.modal_fragment_container).visibility = View.VISIBLE
    }

    private fun hideModalContainer() {
        requireView().findViewById<View>(R.id.modal_fragment_container).visibility = View.GONE
    }

}
