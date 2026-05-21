package com.example.navigationapplication

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.os.Bundle

class HomeFragment : SceneFragment<HomeViewModel>() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<View>(R.id.button_next).setOnClickListener {
            navigateToPageTwo()
        }
    }

    private fun navigateToPageTwo() {
        viewModel.next()

//        val newFragment = PageTwoFragment()
//
//        requireParentFragment().childFragmentManager.beginTransaction()
//            .setCustomAnimations(
//                R.anim.fragment_slide_in_right,
//                R.anim.fragment_slide_out_left
//            )
//            .replace(R.id.child_fragment_container, newFragment)
//            .commit()
    }
}
