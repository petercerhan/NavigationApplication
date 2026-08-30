package com.example.navigationapplication.modal_sequence.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.navigationapplication.R
import com.example.navigationapplication.controller_library.NavigableFragment
import com.example.navigationapplication.controller_library.SceneFragment
import com.example.navigationapplication.modal_sequence.controller.ModalPageTwoViewModel

class ModalPageTwoFragment : SceneFragment<ModalPageTwoViewModel>(), NavigableFragment {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_modal_page_two, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<View>(R.id.button_next).setOnClickListener {
            next()
        }
    }

    override fun backButtonAction() {
        viewModel.back()
    }

    //NavigableFragment Interface

    override fun next() {
        viewModel.next()
    }

    override fun back() {
        viewModel.back()
    }

}
