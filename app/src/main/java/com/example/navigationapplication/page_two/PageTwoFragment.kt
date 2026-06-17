package com.example.navigationapplication.page_two

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.navigationapplication.R
import com.example.navigationapplication.controller_library.SceneFragment
import com.example.navigationapplication.level_two.ContainerFragment
import com.example.navigationapplication.level_two.InitialScreen
import java.util.UUID

class PageTwoFragment : SceneFragment<PageTwoViewModel>() {

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
            viewModel.next()
        }
    }

    override fun backButtonAction() {
        viewModel.back()
    }



    //Legacy Navigation Functions//

    private fun navigateToLevelTwoContainer() {
        requireActivity().supportFragmentManager.beginTransaction()
            .setCustomAnimations(
                R.anim.fragment_slide_in_bottom,
                0,
                0,
                R.anim.fragment_slide_out_bottom
            )
            .add(
                R.id.main,
                ContainerFragment.newInstance(
                    InitialScreen.LEVEL_TWO_PAGE_ONE,
                    UUID.randomUUID() //placeholder to compile; shouldn't need to pass a coordinator ID
                )
            )
            .commit()
    }
}
