package com.example.campuscircleapp.features.admin.fragments

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.campuscircleapp.R
import com.example.campuscircleapp.adapters.MarkAttendanceStudentAdapter
import com.example.campuscircleapp.core.models.UiState
import com.example.campuscircleapp.features.home.models.AdminCourseResponse
import com.example.campuscircleapp.features.home.models.MarkAttendanceRequest
import com.example.campuscircleapp.features.home.models.MarkAttendanceStudent
import com.example.campuscircleapp.features.home.models.MarkAttendanceStudentUi
import com.example.campuscircleapp.features.home.viewModels.MarkAttendanceViewModel
import com.example.campuscircleapp.shared.services.SessionManager
import com.google.android.material.button.MaterialButton
import com.google.android.material.progressindicator.CircularProgressIndicator
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class MarkAttendanceFragment : Fragment() {

    private val viewModel by lazy {
        ViewModelProvider(this)[MarkAttendanceViewModel::class.java]
    }

    private lateinit var loadingView: CircularProgressIndicator
    private lateinit var errorView: TextView
    private lateinit var courseSpinner: Spinner
    private lateinit var dateValue: TextView
    private lateinit var pickDateBtn: MaterialButton
    private lateinit var submitBtn: MaterialButton
    private lateinit var studentsAdapter: MarkAttendanceStudentAdapter

    private var selectedCourse: AdminCourseResponse? = null
    private var selectedDateTime: String? = null
    private var courses: List<AdminCourseResponse> = emptyList()
    private var token: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_mark_attendance, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bindViews(view)
        setupRecycler(view)
        setupActions()
        observeState()

        token = SessionManager.getToken(requireContext())
        if (token.isNullOrBlank()) {
            showError("Session expired. Please sign in again.")
            submitBtn.isEnabled = false
            return
        }

        viewModel.loadCourses(token!!)
    }

    private fun bindViews(view: View) {
        loadingView = view.findViewById(R.id.markAttendanceLoading)
        errorView = view.findViewById(R.id.markAttendanceError)
        courseSpinner = view.findViewById(R.id.adminCourseSpinner)
        dateValue = view.findViewById(R.id.attendanceDateValue)
        pickDateBtn = view.findViewById(R.id.pickDateTimeBtn)
        submitBtn = view.findViewById(R.id.markAttendanceBtn)
    }

    private fun setupRecycler(view: View) {
        studentsAdapter = MarkAttendanceStudentAdapter(emptyList())
        val recycler = view.findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.markStudentsRecycler)
        recycler.layoutManager = LinearLayoutManager(requireContext())
        recycler.adapter = studentsAdapter
    }

    private fun setupActions() {
        pickDateBtn.setOnClickListener {
            showDateTimePicker()
        }

        submitBtn.setOnClickListener {
            submitAttendance()
        }

        courseSpinner.onItemSelectedListener = object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: android.widget.AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedCourse = courses.getOrNull(position)
                val selectedId = selectedCourse?.id ?: return
                val localToken = token ?: return
                viewModel.loadEnrollments(localToken, selectedId)
            }

            override fun onNothingSelected(parent: android.widget.AdapterView<*>?) = Unit
        }
    }

    private fun observeState() {
        viewModel.coursesState.asLiveData().observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Loading -> showLoading(true)
                is UiState.Success -> {
                    showLoading(false)
                    errorView.isVisible = false
                    bindCourses(state.data)
                }
                is UiState.Error -> {
                    showLoading(false)
                    showError(state.message)
                }
                is UiState.Idle -> Unit
                else -> {

                }
            }
        }

        viewModel.enrollmentsState.asLiveData().observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Loading -> showLoading(true)
                is UiState.Success -> {
                    showLoading(false)
                    errorView.isVisible = false
                    val items = state.data.map {
                        // API response currently gives enrollmentId; use it as student identifier for submission.
                        MarkAttendanceStudentUi(
                            studentId = it.enrollmentId,
                            studentName = it.studentName,
                            rollNo = it.rollNo,
                            status = "present"
                        )
                    }
                    studentsAdapter.updateData(items)
                }
                is UiState.Error -> {
                    showLoading(false)
                    showError(state.message)
                }
                is UiState.Idle -> Unit

                else -> {

                }
            }
        }

        viewModel.markState.asLiveData().observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Loading -> {
                    showLoading(true)
                    submitBtn.isEnabled = false
                }
                is UiState.Success -> {
                    showLoading(false)
                    submitBtn.isEnabled = true
                    errorView.isVisible = false
                    Toast.makeText(requireContext(), state.data, Toast.LENGTH_SHORT).show()
                }
                is UiState.Error -> {
                    showLoading(false)
                    submitBtn.isEnabled = true
                    showError(state.message)
                }
                is UiState.Idle -> Unit
                else->{}
            }
        }
    }

    private fun bindCourses(items: List<AdminCourseResponse>) {
        courses = items
        val courseNames = items.map { "${it.code} - ${it.name}" }
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, courseNames)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        courseSpinner.adapter = adapter

        if (items.isNotEmpty()) {
            selectedCourse = items.first()
        }
    }

    private fun showDateTimePicker() {
        val calendar = Calendar.getInstance()
        val dateDialog = DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                val selectedCalendar = Calendar.getInstance().apply {
                    set(Calendar.YEAR, year)
                    set(Calendar.MONTH, month)
                    set(Calendar.DAY_OF_MONTH, dayOfMonth)
                }
                showTimePicker(selectedCalendar)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        dateDialog.show()
    }

    private fun showTimePicker(calendar: Calendar) {
        val timeDialog = TimePickerDialog(
            requireContext(),
            { _, hourOfDay, minute ->
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                calendar.set(Calendar.MINUTE, minute)
                calendar.set(Calendar.SECOND, 0)

                val apiFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
                val displayFormat = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())

                selectedDateTime = apiFormat.format(calendar.time)
                dateValue.text = displayFormat.format(calendar.time)
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            false
        )
        timeDialog.show()
    }

    private fun submitAttendance() {
        val localToken = token
        val course = selectedCourse
        val attendanceDate = selectedDateTime
        val students = studentsAdapter.getCurrentData()

        if (localToken.isNullOrBlank()) {
            showError("Session expired. Please sign in again.")
            return
        }
        if (course == null) {
            showError("Please select a course.")
            return
        }
        if (attendanceDate.isNullOrBlank()) {
            showError("Please select attendance date and time.")
            return
        }
        if (students.isEmpty()) {
            showError("No students found for this course.")
            return
        }

        val payload = MarkAttendanceRequest(
            courseId = course.id,
            attendanceDate = attendanceDate,
            attendance = students.map {
                MarkAttendanceStudent(
                    studentId = it.studentId,
                    status = it.status
                )
            }
        )

        viewModel.submitAttendance(localToken, payload)
    }

    private fun showLoading(show: Boolean) {
        loadingView.isVisible = show
    }

    private fun showError(message: String) {
        errorView.isVisible = true
        errorView.text = message
    }
}
