package com.example.campuscircleapp.features.home.fragments

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
import com.example.campuscircleapp.adapters.AttendanceHistoryAdapter
import com.example.campuscircleapp.core.models.UiState
import com.example.campuscircleapp.features.home.models.StudentEnrollmentResponse
import com.example.campuscircleapp.features.home.viewModels.AttendanceViewModel
import com.example.campuscircleapp.shared.services.SessionManager
import com.google.android.material.progressindicator.CircularProgressIndicator

class AttendanceFragment : Fragment() {

    private val viewModel by lazy {
        ViewModelProvider(this)[AttendanceViewModel::class.java]
    }

    private lateinit var courseSpinner: Spinner
    private lateinit var historyRecyclerView: RecyclerView
    private lateinit var historyAdapter: AttendanceHistoryAdapter
    private lateinit var loadingView: CircularProgressIndicator
    private lateinit var errorView: TextView
    private lateinit var summaryText: TextView
    private lateinit var emptyText: TextView

    private var token: String = ""
    private var courses: List<StudentEnrollmentResponse> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_attendance, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        courseSpinner = view.findViewById(R.id.courseSpinner)
        historyRecyclerView = view.findViewById(R.id.attendanceHistoryRecyclerView)
        loadingView = view.findViewById(R.id.attendanceLoading)
        errorView = view.findViewById(R.id.attendanceError)
        summaryText = view.findViewById(R.id.attendanceSummary)
        emptyText = view.findViewById(R.id.emptyHistoryText)

        historyAdapter = AttendanceHistoryAdapter(emptyList())
        historyRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        historyRecyclerView.adapter = historyAdapter

        observeStates()

        val sessionToken = SessionManager.getToken(requireContext())
        if (sessionToken.isNullOrBlank()) {
            showError("Session expired. Please login again.")
            return
        }

        token = sessionToken
        viewModel.loadStudentCourses(token)
    }

    private fun observeStates() {
        viewModel.coursesState.asLiveData().observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Idle -> Unit
                is UiState.Loading -> showLoading(true)
                is UiState.Success -> {
                    showLoading(false)
                    courses = state.data
                    bindCoursesSpinner(state.data)
                }
                is UiState.Error -> {
                    showLoading(false)
                    showError(state.message)
                }
            }
        }

        viewModel.historyState.asLiveData().observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Idle -> Unit
                is UiState.Loading -> {
                    showLoading(true)
                    errorView.isVisible = false
                }
                is UiState.Success -> {
                    showLoading(false)
                    errorView.isVisible = false
                    historyAdapter.updateData(state.data)
                    emptyText.isVisible = state.data.isEmpty()
                }
                is UiState.Error -> {
                    showLoading(false)
                    showError(state.message)
                }
            }
        }
    }

    private fun bindCoursesSpinner(items: List<StudentEnrollmentResponse>) {
        if (items.isEmpty()) {
            summaryText.text = "No enrolled courses found"
            emptyText.isVisible = true
            return
        }

        val names = items.map { it.courseName.trim() }
        val spinnerAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, names)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        courseSpinner.adapter = spinnerAdapter

        courseSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selected = items[position]
                val percentage = if (selected.totalClasses == 0) {
                    0
                } else {
                    (selected.attended * 100) / selected.totalClasses
                }

                summaryText.text = "${selected.attended}/${selected.totalClasses} classes attended (${percentage}%)"
                viewModel.loadAttendanceHistory(token, selected.enrollmentId)
            }

            override fun onNothingSelected(parent: AdapterView<*>) = Unit
        }
    }

    private fun showLoading(show: Boolean) {
        loadingView.isVisible = show
        historyRecyclerView.isVisible = !show
    }

    private fun showError(message: String) {
        errorView.isVisible = true
        errorView.text = message
    }
}