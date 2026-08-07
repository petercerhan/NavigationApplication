package com.example.navigationapplication.root_sequence.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.navigationapplication.R
import com.example.navigationapplication.controller_library.SceneFragment
import com.example.navigationapplication.root_sequence.controller.TableViewModel

class TableFragment : SceneFragment<TableViewModel>() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_table, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val countries = resources.getStringArray(R.array.countries).toList()
        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_countries)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = CountryAdapter(countries)

        view.findViewById<View>(R.id.button_next).setOnClickListener {
            viewModel.next()
        }
    }

    override fun backButtonAction() {
        viewModel.back()
    }

}
