package com.example.campuscircleapp.features.teacher.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.campuscircleapp.R
import com.example.campuscircleapp.adapters.TeacherStudentAdapter
import com.example.campuscircleapp.core.models.UiState
import com.example.campuscircleapp.features.home.models.AdminCourseResponse
import com.example.campuscircleapp.features.teacher.viewModels.TeacherStudentsViewModel
import com.example.campuscircleapp.shared.services.SessionManager
import com.google.android.material.progressindicator.CircularProgressIndicator

class TeacherStudentsFragment : Fragment() {

    private val viewModel by lazy {
        ViewModelProvider(this)[TeacherStudentsViewModel::class.java]
    }

    private lateinit var loadingView: CircularProgressIndicator
    private lateinit var errorView: TextView
    private lateinit var emptyView: TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var courseSpinner: Spinner
    private lateinit var adapter: TeacherStudentAdapter

    private var courses: List<AdminCourseResponse> = emptyList()
    private var token: String = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_teacher_students, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadingView = view.findViewById(R.id.teacherStudentsLoading)
        errorView = view.findViewById(R.id.teacherStudentsError)
        emptyView = view.findViewById(R.id.teacherStudentsEmpty)
        recyclerView = view.findViewById(R.id.teacherStudentsRecycler)
        courseSpinner = view.findViewById(R.id.teacherStudentCourseSpinner)

        adapter = TeacherStudentAdapter(emptyList())
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        val sessionToken = SessionManager.getToken(requireContext())
        if (sessionToken.isNullOrBlank()) {
            showError("Session expired. Please sign in again.")
            return
        }
        token = sessionToken

        observeStates()
        viewModel.loadCourses(token)
    }

    private fun observeStates() {
        viewModel.coursesState.asLiveData().observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Success -> {
                    courses = state.data
                    val names = courses.map { it.name }
                    val spinnerAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, names)
                    spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    courseSpinner.adapter = spinnerAdapter

                    courseSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(parent: AdapterView<*>, v: View?, position: Int, id: Long) {
                            viewModel.loadStudentsByCourse(token, courses[position].id)
                        }
                        override fun onNothingSelected(parent: AdapterView<*>) = Unit
                    }

                    if (courses.isNotEmpty()) {
                        viewModel.loadStudentsByCourse(token, courses[0].id)
                    }
                }
                is UiState.Error -> showError(state.message)
                else -> {}
            }
        }

        viewModel.studentsState.asLiveData().observe(viewLifecycleOwner) { state ->
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
