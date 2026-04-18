package com.example.campuscircleapp.features.admin.fragments

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
import com.example.campuscircleapp.R
import com.example.campuscircleapp.adapters.AdminDashboardCourseAdapter
import com.example.campuscircleapp.core.models.UiState
import com.example.campuscircleapp.features.home.models.AdminDashboardResponse
import com.example.campuscircleapp.features.home.viewModels.AdminDashboardViewModel
import com.example.campuscircleapp.shared.services.SessionManager
import com.google.android.material.progressindicator.CircularProgressIndicator

class AdminDashboardFragment : Fragment() {

    private val viewModel by lazy {
        ViewModelProvider(this)[AdminDashboardViewModel::class.java]
    }

    private lateinit var loadingView: CircularProgressIndicator
    private lateinit var errorView: TextView
    private lateinit var contentView: ViewGroup
    private lateinit var activeCoursesValue: TextView
    private lateinit var totalStudentsValue: TextView
    private lateinit var activeInstructorsValue: TextView
    private lateinit var averageAttendanceValue: TextView
    private lateinit var coursesAdapter: AdminDashboardCourseAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_admin_dashboard, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bindViews(view)
        setupRecycler(view)
        observeState()

        val token = SessionManager.getToken(requireContext())
        if (token.isNullOrBlank()) {
            showError("Session expired. Please sign in again.")
            return
        }

        viewModel.loadDashboard(token, 1L)
    }

    private fun bindViews(view: View) {
        loadingView = view.findViewById(R.id.adminDashboardLoading)
        errorView = view.findViewById(R.id.adminDashboardError)
        contentView = view.findViewById(R.id.adminDashboardContent)
        activeCoursesValue = view.findViewById(R.id.activeCoursesValue)
        totalStudentsValue = view.findViewById(R.id.totalStudentsValue)
        activeInstructorsValue = view.findViewById(R.id.activeInstructorsValue)
        averageAttendanceValue = view.findViewById(R.id.averageAttendanceValue)
    }

    private fun setupRecycler(view: View) {
        coursesAdapter = AdminDashboardCourseAdapter(emptyList())
        val recycler = view.findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.adminCoursesRecycler)
        recycler.layoutManager = LinearLayoutManager(requireContext())
        recycler.adapter = coursesAdapter
    }

    private fun observeState() {
        viewModel.dashboardState.asLiveData().observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Idle -> {
                    loadingView.isVisible = false
                    errorView.isVisible = false
                    contentView.isVisible = true
                }
                is UiState.Loading -> {
                    loadingView.isVisible = true
                    errorView.isVisible = false
                    contentView.isVisible = false
                }
                is UiState.Success -> {
                    loadingView.isVisible = false
                    errorView.isVisible = false
                    contentView.isVisible = true
                    renderDashboard(state.data)
                }
                is UiState.Error -> showError(state.message)
            }
        }
    }

    private fun renderDashboard(data: AdminDashboardResponse) {
        activeCoursesValue.text = data.analytics.activeCourses.toString()
        totalStudentsValue.text = data.analytics.totalStudents.toString()
        activeInstructorsValue.text = data.analytics.activeInstructors.toString()
        averageAttendanceValue.text = "${data.analytics.averageAttendance}%"
        coursesAdapter.updateData(data.courses)
    }

    private fun showError(message: String) {
        loadingView.isVisible = false
        contentView.isVisible = false
        errorView.isVisible = true
        errorView.text = message
    }
}
