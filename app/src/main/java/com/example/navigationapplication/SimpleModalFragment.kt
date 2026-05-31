package com.example.navigationapplication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

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
