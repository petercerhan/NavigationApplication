package com.example.navigationapplication.simple_modal

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.navigationapplication.R
import com.example.navigationapplication.controller_library.SceneFragment

class SimpleModalFragment : SceneFragment<SimpleModalViewModel>() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_simple_modal, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<View>(R.id.button_dismiss).setOnClickListener {
            viewModel.dismiss()
        }
    }

    override fun backButtonAction() {
        viewModel.dismiss()
    }

}
