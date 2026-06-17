package com.example.navigationapplication.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.navigationapplication.R
import com.example.navigationapplication.controller_library.SceneFragment

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
        view.findViewById<TextView>(R.id.text_home_scene_number).text =
            getString(R.string.home_scene_number_label, viewModel.homeSceneNumber)
        view.findViewById<View>(R.id.button_next).setOnClickListener {
            navigateToPageTwo()
        }
    }

    private fun navigateToPageTwo() {
        viewModel.next()
    }

    override fun backButtonAction() {
        Log.d("Peter Cerhan", "HomeFragment blocking back action")
    }
}
