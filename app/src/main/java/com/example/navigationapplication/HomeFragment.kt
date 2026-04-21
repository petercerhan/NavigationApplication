package com.example.navigationapplication

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

class HomeFragment : Fragment() {
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
        parentFragmentManager.beginTransaction()
            .setCustomAnimations(
                R.anim.fragment_slide_in_right,
                R.anim.fragment_slide_out_left
            )
            .replace(R.id.main, PageTwoFragment())
            .commit()
    }

}
