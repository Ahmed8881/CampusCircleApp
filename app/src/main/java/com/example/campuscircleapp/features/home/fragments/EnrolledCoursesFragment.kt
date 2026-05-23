package com.example.campuscircleapp.features.home.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.campuscircleapp.R
import com.example.campuscircleapp.adapters.EnrolledCourseAdapter
import com.example.campuscircleapp.core.models.UiState
import com.example.campuscircleapp.features.home.viewModels.EnrolledCoursesViewModel
import com.example.campuscircleapp.shared.services.SessionManager

class EnrolledCoursesFragment : Fragment() {

    private lateinit var viewModel: EnrolledCoursesViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        inflater.inflate(R.layout.fragment_enrolled_courses, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this)[EnrolledCoursesViewModel::class.java]

        val recyclerView = view.findViewById<RecyclerView>(R.id.enrolledRecyclerView)
        val emptyView = view.findViewById<TextView>(R.id.enrolledEmptyView)
        val loadingView = view.findViewById<View>(R.id.enrolledLoadingView)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        val token = SessionManager.getToken(requireContext()) ?: return

        viewModel.coursesState.asLiveData().observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Loading -> {
                    loadingView.visibility = View.VISIBLE
                    recyclerView.visibility = View.GONE
                    emptyView.visibility = View.GONE
                }
                is UiState.Success -> {
                    loadingView.visibility = View.GONE
                    if (state.data.isEmpty()) {
                        recyclerView.visibility = View.GONE
                        emptyView.visibility = View.VISIBLE
                    } else {
                        recyclerView.visibility = View.VISIBLE
                        emptyView.visibility = View.GONE
                        recyclerView.adapter = EnrolledCourseAdapter(state.data)
                    }
                }
                is UiState.Error -> {
                    loadingView.visibility = View.GONE
                    recyclerView.visibility = View.GONE
                    emptyView.visibility = View.VISIBLE
                    emptyView.text = state.message
                }
                else -> {}
            }
        }

        viewModel.load(token)
    }
}
