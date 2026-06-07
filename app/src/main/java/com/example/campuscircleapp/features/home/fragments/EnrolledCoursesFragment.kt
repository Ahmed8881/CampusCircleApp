package com.example.campuscircleapp.features.home.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.campuscircleapp.R
import com.example.campuscircleapp.adapters.EnrolledCourseAdapter
import com.example.campuscircleapp.core.models.UiState
import com.example.campuscircleapp.features.home.models.StudentEnrollmentResponse
import com.example.campuscircleapp.features.home.viewModels.EnrolledCoursesViewModel
import com.example.campuscircleapp.shared.services.SessionManager
import com.google.android.material.chip.Chip

class EnrolledCoursesFragment : Fragment() {

    private lateinit var viewModel: EnrolledCoursesViewModel
    private var allCourses: List<StudentEnrollmentResponse> = emptyList()
    private var activeFilter = "all"
    private var searchQuery = ""

    private lateinit var recyclerView: RecyclerView
    private lateinit var emptyView: View

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        inflater.inflate(R.layout.fragment_enrolled_courses, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this)[EnrolledCoursesViewModel::class.java]

        recyclerView = view.findViewById(R.id.enrolledRecyclerView)
        emptyView = view.findViewById(R.id.enrolledEmptyView)
        val loadingView = view.findViewById<View>(R.id.enrolledLoadingView)
        val tvTotalCourses = view.findViewById<TextView>(R.id.tvTotalCourses)
        val tvAvgAttendance = view.findViewById<TextView>(R.id.tvAvgAttendance)
        val tvCredits = view.findViewById<TextView>(R.id.tvCredits)
        val tvCourseCount = view.findViewById<TextView>(R.id.tvCourseCount)
        val etSearch = view.findViewById<EditText>(R.id.etSearch)
        val chipAll = view.findViewById<Chip>(R.id.chipAll)
        val chipOngoing = view.findViewById<Chip>(R.id.chipOngoing)
        val chipCompleted = view.findViewById<Chip>(R.id.chipCompleted)
        val chipPending = view.findViewById<Chip>(R.id.chipPending)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchQuery = s?.toString().orEmpty()
                applyFilter()
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        val filterMap = mapOf(
            R.id.chipAll to "all",
            R.id.chipOngoing to "approved",
            R.id.chipCompleted to "completed",
            R.id.chipPending to "pending"
        )
        val chips = listOf(chipAll, chipOngoing, chipCompleted, chipPending)
        chips.forEach { chip ->
            chip.setOnClickListener {
                chips.forEach { it.isChecked = false }
                chip.isChecked = true
                activeFilter = filterMap[chip.id] ?: "all"
                applyFilter()
            }
        }

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
                    allCourses = state.data

                    val approvedCount = state.data.count { it.status.lowercase() == "approved" }
                    val avgPct = state.data
                        .filter { it.totalClasses > 0 }
                        .map { it.attended * 100 / it.totalClasses }
                        .average()
                        .let { if (it.isNaN()) 0.0 else it }

                    tvTotalCourses.text = state.data.size.toString()
                    tvAvgAttendance.text = "${avgPct.toInt()}%"
                    tvCredits.text = approvedCount.toString()
                    tvCourseCount.text = "$approvedCount active"

                    applyFilter()
                }
                is UiState.Error -> {
                    loadingView.visibility = View.GONE
                    recyclerView.visibility = View.GONE
                    emptyView.visibility = View.VISIBLE
                }
                else -> {}
            }
        }

        viewModel.load(token)
    }

    private fun applyFilter() {
        val filtered = allCourses
            .filter { activeFilter == "all" || it.status.lowercase() == activeFilter }
            .filter { searchQuery.isBlank() || it.courseName.contains(searchQuery, ignoreCase = true) }

        if (filtered.isEmpty()) {
            recyclerView.visibility = View.GONE
            emptyView.visibility = View.VISIBLE
        } else {
            recyclerView.visibility = View.VISIBLE
            emptyView.visibility = View.GONE
            recyclerView.adapter = EnrolledCourseAdapter(filtered)
        }
    }
}
