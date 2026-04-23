package com.example.navigationapplication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

class ContainerFragment : Fragment() {
    private val initialScreen: InitialScreen by lazy {
        val value = arguments?.getString(ARG_INITIAL_SCREEN) ?: InitialScreen.HOME.name
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
            InitialScreen.HOME -> HomeFragment()
            InitialScreen.LEVEL_TWO_PAGE_ONE -> LevelTwoPageOneFragment()
        }

        childFragmentManager.beginTransaction()
            .replace(R.id.child_fragment_container, initialFragment)
            .commit()
    }

    companion object {
        private const val ARG_INITIAL_SCREEN = "arg_initial_screen"

        fun newInstance(initialScreen: InitialScreen): ContainerFragment {
            return ContainerFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_INITIAL_SCREEN, initialScreen.name)
                }
            }
        }
    }
}

enum class InitialScreen {
    HOME,
    LEVEL_TWO_PAGE_ONE
}
