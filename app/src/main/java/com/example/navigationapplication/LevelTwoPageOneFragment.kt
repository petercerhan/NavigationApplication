package com.example.navigationapplication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment

class LevelTwoPageOneFragment : Fragment() {
    private var backPressedCallback: OnBackPressedCallback? = null
    private var isNavigatingBack = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_level_two_page_one, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                navigateBackToPageTwo()
            }
        }
        backPressedCallback = callback
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }

    private fun navigateBackToPageTwo() {
        if (isNavigatingBack) return
        isNavigatingBack = true
        backPressedCallback?.isEnabled = false

        val topContainerFragment = requireParentFragment()
        val topContainerView = topContainerFragment.view

        if (topContainerView == null) {
            requireActivity().supportFragmentManager.beginTransaction()
                .remove(topContainerFragment)
                .commit()
            return
        }

        val duration = resources.getInteger(android.R.integer.config_shortAnimTime).toLong()
        topContainerView.animate()
            .translationY(topContainerView.height.toFloat())
            .setDuration(duration)
            .withEndAction {
                if (!isAdded) return@withEndAction
                requireActivity().supportFragmentManager.beginTransaction()
                    .remove(topContainerFragment)
                    .commit()
            }
            .start()
    }
}
