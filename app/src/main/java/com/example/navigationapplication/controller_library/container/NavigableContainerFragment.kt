package com.example.navigationapplication.controller_library.container

import android.os.Bundle
import android.view.View
import com.example.navigationapplication.R

class NavigableContainerFragment : ContainerFragment<NavigableContainerViewModel>() {

    override val layoutRes: Int
        get() = R.layout.fragment_navigable_container

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<View>(R.id.button_container_next).setOnClickListener {
            viewModel.next()
        }
        view.findViewById<View>(R.id.button_container_back).setOnClickListener {
            viewModel.back()
        }
    }

}
