package com.example.navigationapplication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.navigationapplication.level_two.ContainerFragment
import com.example.navigationapplication.level_two.InitialScreen
import java.util.UUID

class PageTwoFragment() : Fragment() {

    private val activityViewModel: MainActivityViewModel by activityViewModels()
    private val systemViewModel: SystemViewModel<PageTwoViewModel> by viewModels {
        object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val viewModelId = UUID.fromString(
                    requireArguments().getString(VIEW_MODEL_ID)
                        ?: error("Missing $VIEW_MODEL_ID")
                )
                return SystemViewModel<PageTwoViewModel>(
                    viewModelId=viewModelId,
                    serviceLocator=activityViewModel.serviceLocator
                ) as T
            }
        }
    }

    private val viewModel: PageTwoViewModel
        get() = systemViewModel.viewModel

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
        const val VIEW_MODEL_ID = "view_model_id"

        fun newInstance(viewModelId: String): PageTwoFragment {
            return PageTwoFragment().apply {
                arguments = Bundle().apply {
                    putString(VIEW_MODEL_ID, viewModelId)
                }
            }
        }
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
                    activityViewModel.coordinator.id
                )
            )
            .commit()
    }

}
