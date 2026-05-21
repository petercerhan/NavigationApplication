package com.example.navigationapplication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import com.example.navigationapplication.level_two.ContainerFragment
import com.example.navigationapplication.level_two.InitialScreen
import java.util.UUID

class PageTwoFragment : SceneFragment<PageTwoViewModel>() {

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
            viewModel.next()
        }
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                viewModel.back()
            }
        }
        backPressedCallback = callback
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }

    companion object {
        fun newInstance(viewModelId: String): PageTwoFragment =
            SceneFragment.newInstance(viewModelId)
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
