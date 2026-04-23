package com.example.navigationapplication

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment

class PageTwoFragment : Fragment() {
    private var backPressedCallback: OnBackPressedCallback? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_page_two, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<View>(R.id.button_next).setOnClickListener {
            navigateToLevelTwoContainer()
        }
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                navigateToHome()
            }
        }
        backPressedCallback = callback
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }

    override fun onDestroyView() {
        Log.d("PageTwoFragment", "onDestroyView() for instance=${System.identityHashCode(this)}")
        backPressedCallback?.remove()
        backPressedCallback = null
        super.onDestroyView()
    }

    override fun onDestroy() {
        Log.d("PageTwoFragment", "onDestroy() for instance=${System.identityHashCode(this)}")
        super.onDestroy()
    }

    private fun navigateToHome() {
        requireParentFragment().childFragmentManager.beginTransaction()
            .setCustomAnimations(
                R.anim.fragment_slide_in_left,
                R.anim.fragment_slide_out_right
            )
            .replace(R.id.child_fragment_container, HomeFragment())
            .commit()
    }

    private fun navigateToLevelTwoContainer() {
        requireActivity().supportFragmentManager.beginTransaction()
            .setCustomAnimations(
                R.anim.fragment_slide_in_right,
                R.anim.fragment_slide_out_left,
                R.anim.fragment_slide_in_left,
                R.anim.fragment_slide_out_right
            )
            .replace(
                R.id.main,
                ContainerFragment.newInstance(InitialScreen.LEVEL_TWO_PAGE_ONE)
            )
            .addToBackStack(null)
            .commit()
    }

}
