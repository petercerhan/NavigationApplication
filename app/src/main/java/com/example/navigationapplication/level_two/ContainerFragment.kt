package com.example.navigationapplication.level_two

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.navigationapplication.MainActivityViewModel
import com.example.navigationapplication.R
import java.util.UUID
import kotlin.getValue

class ContainerFragment : Fragment() {

    val activityViewModel: MainActivityViewModel by activityViewModels()

    val rootCoordinatorId: UUID by lazy {
        UUID.fromString(
            requireArguments().getString(ARG_ROOT_COORDINATOR_ID)
                ?: error("Missing $ARG_ROOT_COORDINATOR_ID")
        )
    }

    private val initialScreen: InitialScreen by lazy {
        val value = arguments?.getString(ARG_INITIAL_SCREEN) ?: InitialScreen.LEVEL_TWO_PAGE_ONE.name
        InitialScreen.valueOf(value)
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
            InitialScreen.LEVEL_TWO_PAGE_ONE -> LevelTwoPageOneFragment()
        }

        childFragmentManager.beginTransaction()
            .replace(R.id.child_fragment_container, initialFragment)
            .commit()
    }

    companion object {
        private const val ARG_INITIAL_SCREEN = "arg_initial_screen"
        private const val ARG_ROOT_COORDINATOR_ID = "arg_root_coordinator_id"

        fun newInstance(initialScreen: InitialScreen, rootCoordinatorId: UUID): ContainerFragment {
            return ContainerFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_INITIAL_SCREEN, initialScreen.name)
                    putString(ARG_ROOT_COORDINATOR_ID, rootCoordinatorId.toString())
                }
            }
        }
    }
}

enum class InitialScreen {
    LEVEL_TWO_PAGE_ONE
}
