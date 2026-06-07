package com.example.campuscircleapp.features.teacher.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.campuscircleapp.R
import com.example.campuscircleapp.adapters.TeacherCourseAdapter
import com.example.campuscircleapp.core.models.UiState
import com.example.campuscircleapp.features.teacher.viewModels.TeacherCoursesViewModel
import com.example.campuscircleapp.shared.services.SessionManager
import com.google.android.material.progressindicator.CircularProgressIndicator

class TeacherCoursesFragment : Fragment() {

    private val viewModel by lazy {
        ViewModelProvider(this)[TeacherCoursesViewModel::class.java]
    }

    private lateinit var loadingView: CircularProgressIndicator
    private lateinit var errorView: TextView
    private lateinit var emptyView: View
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: TeacherCourseAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_teacher_courses, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadingView = view.findViewById(R.id.teacherCoursesLoading)
        errorView = view.findViewById(R.id.teacherCoursesError)
        emptyView = view.findViewById(R.id.teacherCoursesEmpty)
        recyclerView = view.findViewById(R.id.teacherCoursesRecycler)

        adapter = TeacherCourseAdapter(emptyList())
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        observeState()

        val token = SessionManager.getToken(requireContext())
        if (token.isNullOrBlank()) {
            showError("Session expired. Please sign in again.")
            return
        }

        viewModel.loadCourses(token)
    }

    private fun observeState() {
        viewModel.coursesState.asLiveData().observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Idle -> Unit
                is UiState.Loading -> {
                    loadingView.isVisible = true
                    errorView.isVisible = false
                    recyclerView.isVisible = false
                    emptyView.isVisible = false
                }
                is UiState.Success -> {
                    loadingView.isVisible = false
                    errorView.isVisible = false
                    if (state.data.isEmpty()) {
                        emptyView.isVisible = true
                        recyclerView.isVisible = false
                    } else {
                        emptyView.isVisible = false
                        recyclerView.isVisible = true
                        adapter.updateData(state.data)
                    }
                }
                is UiState.Error -> showError(state.message)
                else -> {}
            }
        }
    }

    private fun showError(message: String) {
        loadingView.isVisible = false
        recyclerView.isVisible = false
        emptyView.isVisible = false
        errorView.isVisible = true
        errorView.text = message
    }
}
