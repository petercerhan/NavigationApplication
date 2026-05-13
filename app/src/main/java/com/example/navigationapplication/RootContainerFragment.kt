package com.example.navigationapplication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import java.util.UUID

class RootContainerFragment : Fragment() {

    val activityViewModel: MainActivityViewModel by activityViewModels()

    val rootContainerViewModel: RootContainerViewModel by viewModels {
        object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val id = UUID.fromString(
                    requireArguments().getString(ARG_ROOT_CONTAINER_ID)
                        ?: error("Missing $ARG_ROOT_CONTAINER_ID")
                )
                return RootContainerViewModel(
                    rootContainerId = id,
                    serviceLocator = activityViewModel.serviceLocator,
                ) as T
            }
        }
    }

    val rootContainerId: UUID get() = rootContainerViewModel.rootContainerId

    private val initialScreen: RootInitialScreen by lazy {
        val value = arguments?.getString(ARG_INITIAL_SCREEN) ?: RootInitialScreen.HOME.name
        RootInitialScreen.valueOf(value)
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

        if (savedInstanceState != null) return
        if (childFragmentManager.findFragmentById(R.id.child_fragment_container) != null) return

        val initialFragment: Fragment = when (initialScreen) {
            RootInitialScreen.HOME -> HomeFragment()
        }

        childFragmentManager.beginTransaction()
            .replace(R.id.child_fragment_container, initialFragment)
            .commit()
    }

    companion object {
        private const val ARG_INITIAL_SCREEN = "arg_initial_screen"
        const val ARG_ROOT_CONTAINER_ID = "arg_root_container_id"

        fun newInstance(initialScreen: RootInitialScreen, rootContainerId: UUID): RootContainerFragment {
            return RootContainerFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_INITIAL_SCREEN, initialScreen.name)
                    putString(ARG_ROOT_CONTAINER_ID, rootContainerId.toString())
                }
            }
        }
    }
}

enum class RootInitialScreen {
    HOME
}
