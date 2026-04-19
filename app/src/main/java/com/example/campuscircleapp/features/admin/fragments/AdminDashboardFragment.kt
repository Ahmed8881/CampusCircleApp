package com.example.campuscircleapp.features.admin.fragments

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.campuscircleapp.R
import com.example.campuscircleapp.adapters.AdminDashboardCourseAdapter
import com.example.campuscircleapp.core.models.UiState
import com.example.campuscircleapp.features.home.models.AdminDashboardCourse
import com.example.campuscircleapp.features.home.models.AdminDashboardResponse
import com.example.campuscircleapp.features.home.viewModels.AdminDashboardViewModel
import com.example.campuscircleapp.shared.services.SessionManager
import com.github.mikephil.charting.charts.HorizontalBarChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.PercentFormatter
import com.google.android.material.progressindicator.CircularProgressIndicator
import www.sanju.motiontoast.MotionToast
import java.util.Locale

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
    private lateinit var blueIndexValue: TextView
    private lateinit var attendancePieChart: PieChart
    private lateinit var courseBarChart: HorizontalBarChart
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
        blueIndexValue = view.findViewById(R.id.adminBlueIndexValue)
        attendancePieChart = view.findViewById(R.id.adminAttendancePieChart)
        courseBarChart = view.findViewById(R.id.adminCourseBarChart)
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
                else->{
                }
            }
        }
    }

    private fun renderDashboard(data: AdminDashboardResponse) {
        activeCoursesValue.text = data.analytics.activeCourses.toString()
        totalStudentsValue.text = data.analytics.totalStudents.toString()
        activeInstructorsValue.text = data.analytics.activeInstructors.toString()
        val average = data.analytics.averageAttendance.coerceIn(0.0, 100.0)
        averageAttendanceValue.text = String.format(Locale.getDefault(), "%.1f%%", average)
        blueIndexValue.text = (100 + data.analytics.activeCourses + data.analytics.activeInstructors).toString()

        setupAttendancePieChart(average.toFloat())
        setupCourseBarChart(data.courses)
        coursesAdapter.updateData(data.courses)
    }

    private fun setupAttendancePieChart(averageAttendance: Float) {
        val safeAverage = averageAttendance.coerceIn(0f, 100f)
        val remaining = (100f - safeAverage).coerceAtLeast(0f)

        val entries = arrayListOf(
            PieEntry(safeAverage, "Attended"),
            PieEntry(remaining, "Gap")
        )

        val dataSet = PieDataSet(entries, "").apply {
            colors = listOf(
                Color.parseColor("#2563EB"),
                Color.parseColor("#BFDBFE")
            )
            valueTextColor = Color.WHITE
            valueTextSize = 12f
            sliceSpace = 2f
        }

        attendancePieChart.apply {
            data = PieData(dataSet).apply {
                setValueFormatter(PercentFormatter(attendancePieChart))
            }
            setUsePercentValues(true)
            description = Description().apply { text = "" }
            legend.textColor = Color.parseColor("#334155")
            setEntryLabelColor(Color.parseColor("#0F172A"))
            centerText = "${String.format(Locale.getDefault(), "%.1f", safeAverage)}%"
            setCenterTextSize(18f)
            setCenterTextColor(Color.parseColor("#1E3A8A"))
            setHoleColor(Color.WHITE)
            holeRadius = 62f
            transparentCircleRadius = 66f
            animateY(900)
            invalidate()
        }
    }

    private fun setupCourseBarChart(courses: List<AdminDashboardCourse>) {
        if (courses.isEmpty()) {
            courseBarChart.clear()
            courseBarChart.setNoDataText("No course attendance data")
            courseBarChart.invalidate()
            return
        }

        val topCourses = courses
            .sortedByDescending { it.attendance }
            .take(6)

        val entries = topCourses.mapIndexed { index, item ->
            BarEntry(index.toFloat(), item.attendance.toFloat())
        }
        val labels = topCourses.map { it.courseCode.trim() }

        val dataSet = BarDataSet(entries, "").apply {
            color = Color.parseColor("#2563EB")
            valueTextColor = Color.parseColor("#1E3A8A")
            valueTextSize = 11f
        }

        courseBarChart.apply {
            data = BarData(dataSet).apply { barWidth = 0.55f }
            setFitBars(true)
            description = Description().apply { text = "" }
            legend.isEnabled = false
            axisRight.isEnabled = false
            setDrawValueAboveBar(true)
            setScaleEnabled(false)
            setPinchZoom(false)
            setExtraOffsets(8f, 8f, 12f, 8f)

            xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                granularity = 1f
                setDrawGridLines(false)
                textColor = Color.parseColor("#334155")
                valueFormatter = IndexAxisValueFormatter(labels)
            }

            axisLeft.apply {
                axisMinimum = 0f
                axisMaximum = 100f
                granularity = 20f
                setDrawGridLines(true)
                gridColor = Color.parseColor("#E2E8F0")
                textColor = Color.parseColor("#334155")
            }

            animateY(900)
            invalidate()
        }
    }

    private fun showError(message: String) {
        loadingView.isVisible = false
        contentView.isVisible = false
        errorView.isVisible = true
        errorView.text = message
    }
}
