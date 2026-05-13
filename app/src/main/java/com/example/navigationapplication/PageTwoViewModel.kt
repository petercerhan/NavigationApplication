package com.example.navigationapplication

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory

class PageTwoViewModel() : ViewModel() {
    fun onLevelTwoNavigationRequested() {

    }

//    companion object {
//        val PAGE_TWO_INNER_VIEW_MODEL_KEY =
//            object : CreationExtras.Key<PageTwoInnerViewModel> {}
//
//        val Factory: ViewModelProvider.Factory = viewModelFactory {
//            initializer {
//                val inner = this[PAGE_TWO_INNER_VIEW_MODEL_KEY]
//                    ?: error("PageTwoInnerViewModel missing from CreationExtras")
//                PageTwoViewModel(inner)
//            }
//        }
//    }
}
