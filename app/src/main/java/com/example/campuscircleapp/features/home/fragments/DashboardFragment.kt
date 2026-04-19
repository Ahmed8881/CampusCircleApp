package com.example.campuscircleapp.features.home.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.graphics.Color
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.core.view.isVisible
import androidx.lifecycle.asLiveData
import androidx.lifecycle.ViewModelProvider
import com.example.campuscircleapp.R
import com.example.campuscircleapp.core.models.UiState
import com.example.campuscircleapp.features.home.models.AttendancePerCourseItem
import com.example.campuscircleapp.features.home.models.AttendanceTrendItem
import com.example.campuscircleapp.features.home.models.DashboardAnalyticsResponse
import com.example.campuscircleapp.features.home.viewModels.DashboardViewModel
import com.example.campuscircleapp.shared.services.SessionManager
import com.github.mikephil.charting.charts.HorizontalBarChart
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.google.android.material.progressindicator.CircularProgressIndicator

class DashboardFragment : Fragment() {

    private val viewModel by lazy {
        ViewModelProvider(this)[DashboardViewModel::class.java]
    }

    private lateinit var loadingView: CircularProgressIndicator
    private lateinit var errorView: TextView
    private lateinit var contentView: ViewGroup
    private lateinit var dashboardTitle: TextView
    private lateinit var totalPresentValue: TextView
    private lateinit var totalAbsentValue: TextView
    private lateinit var totalLeavesValue: TextView
    private lateinit var totalClassesValue: TextView
    private lateinit var averageAttendanceValue: TextView
    private lateinit var lineChart: LineChart
    private lateinit var barChart: HorizontalBarChart

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_dashboard, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bindViews(view)
        observeDashboardState()

        val token = SessionManager.getToken(requireContext())
        if (token.isNullOrBlank()) {
            showError("Session expired. Please sign in again to view your dashboard.")
            return
        }

        viewModel.loadDashboardData(token)
    }

    private fun bindViews(view: View) {
        loadingView = view.findViewById(R.id.dashboardLoading)
        errorView = view.findViewById(R.id.dashboardError)
        contentView = view.findViewById(R.id.dashboardContent)
        dashboardTitle = view.findViewById(R.id.dashboardTitle)
        totalPresentValue = view.findViewById(R.id.totalPresentValue)
        totalAbsentValue = view.findViewById(R.id.totalAbsentValue)
        totalLeavesValue = view.findViewById(R.id.totalLeavesValue)
        totalClassesValue = view.findViewById(R.id.totalClassesValue)
        averageAttendanceValue = view.findViewById(R.id.averageAttendanceValue)
        lineChart = view.findViewById(R.id.attendanceTrendChart)
        barChart = view.findViewById(R.id.courseAttendanceChart)
    }

    private fun observeDashboardState() {
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

    private fun showError(message: String) {
        loadingView.isVisible = false
        contentView.isVisible = false
        errorView.isVisible = true
        errorView.text = message
    }

    private fun renderDashboard(data: DashboardAnalyticsResponse) {
        dashboardTitle.text = "Dashboard Overview"
        totalPresentValue.text = data.totalPresent.toString()
        totalAbsentValue.text = data.totalAbsent.toString()
        totalLeavesValue.text = data.totalLeaves.toString()
        totalClassesValue.text = data.totalClasses.toString()
        averageAttendanceValue.text = "${data.averageAttendance}%"

        setupTrendChart(data.attendanceTrendData)
        setupCourseChart(data.attendancePerCourseData)
    }

    private fun setupTrendChart(items: List<AttendanceTrendItem>) {
        if (items.isEmpty()) {
            lineChart.clear()
            lineChart.setNoDataText("No attendance trend data yet")
            lineChart.invalidate()
            return
        }

        val entries = items.mapIndexed { index, item -> Entry(index.toFloat(), item.attendance) }
        val labels = items.map { it.month.trim() }

        val dataSet = LineDataSet(entries, "Attendance Trend").apply {
            color = Color.parseColor("#2F80ED")
            valueTextColor = Color.parseColor("#102A43")
            lineWidth = 3f
            setDrawCircles(true)
            setCircleColor(Color.parseColor("#2F80ED"))
            circleRadius = 5f
            setDrawValues(false)
            mode = LineDataSet.Mode.CUBIC_BEZIER
            highLightColor = Color.parseColor("#2F80ED")
            fillAlpha = 80
            setDrawFilled(true)
            fillColor = Color.parseColor("#2F80ED")
        }

        lineChart.apply {
            data = LineData(dataSet)
            setNoDataText("No attendance trend data yet")
            description = Description().apply { text = "" }
            axisRight.isEnabled = false
            legend.isEnabled = false
            setTouchEnabled(true)
            isDragEnabled = true
            setScaleEnabled(false)
            setPinchZoom(false)
            setExtraOffsets(8f, 12f, 12f, 8f)

            xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                granularity = 1f
                setDrawGridLines(false)
                valueFormatter = IndexAxisValueFormatter(labels)
                textColor = Color.parseColor("#102A43")
            }

            axisLeft.apply {
                axisMinimum = 0f
                axisMaximum = 100f
                granularity = 20f
                setDrawGridLines(true)
                textColor = Color.parseColor("#102A43")
            }

            invalidate()
            animateX(900)
        }
    }

    private fun setupCourseChart(items: List<AttendancePerCourseItem>) {
        if (items.isEmpty()) {
            barChart.clear()
            barChart.setNoDataText("No course attendance data yet")
            barChart.invalidate()
            return
        }

        val entries = items.take(8).mapIndexed { index, item -> BarEntry(index.toFloat(), item.attendance) }
        val labels = items.take(8).map { it.course.trim() }

        val dataSet = BarDataSet(entries, "Attendance by Course").apply {
            color = Color.parseColor("#0F766E")
            valueTextColor = Color.parseColor("#102A43")
            valueTextSize = 12f
            setDrawValues(true)
        }

        barChart.apply {
            data = BarData(dataSet).apply { barWidth = 0.55f }
            setFitBars(true)
            setNoDataText("No course attendance data yet")
            description = Description().apply { text = "" }
            legend.isEnabled = false
            axisRight.isEnabled = false
            setDrawValueAboveBar(true)
            setTouchEnabled(true)
            isDragEnabled = true
            setScaleEnabled(false)
            setPinchZoom(false)
            setExtraOffsets(8f, 8f, 12f, 8f)

            xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                granularity = 1f
                valueFormatter = IndexAxisValueFormatter(labels)
                setDrawGridLines(false)
                textColor = Color.parseColor("#102A43")
            }

            axisLeft.apply {
                axisMinimum = 0f
                axisMaximum = 100f
                granularity = 20f
                setDrawGridLines(true)
                textColor = Color.parseColor("#102A43")
            }

            invalidate()
            animateY(900)
        }
    }
}