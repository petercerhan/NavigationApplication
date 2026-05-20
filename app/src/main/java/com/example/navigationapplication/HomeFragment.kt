package com.example.navigationapplication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import java.util.UUID

class HomeFragment : Fragment() {

    val activityViewModel: MainActivityViewModel by activityViewModels()
    val systemViewModel: SystemViewModel<HomeViewModel> by viewModels {
        object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val viewModelId = UUID.fromString(
                    requireArguments().getString(VIEW_MODEL_ID)
                        ?: error("Missing $VIEW_MODEL_ID")
                )
                return SystemViewModel<HomeViewModel>(
                    viewModelId,
                    activityViewModel.serviceLocator,
                ) as T
            }
        }
    }

    private val viewModel: HomeViewModel
        get() = systemViewModel.viewModel

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
        val homeId =  arguments?.getString(VIEW_MODEL_ID)
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

    companion object {
        const val VIEW_MODEL_ID = "view_model_id"

        fun newInstance(viewModelId: String): HomeFragment {
            return HomeFragment().apply {
                arguments = Bundle().apply {
                    putString(VIEW_MODEL_ID, viewModelId)
                }
            }
        }
    }

}
