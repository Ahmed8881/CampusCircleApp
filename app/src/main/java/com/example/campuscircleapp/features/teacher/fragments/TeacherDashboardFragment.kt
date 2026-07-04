package com.example.campuscircleapp.features.teacher.fragments

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.campuscircleapp.R
import com.example.campuscircleapp.core.theme.ChartThemeHelper
import com.example.campuscircleapp.shared.services.SessionManager
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter

class TeacherDashboardFragment : Fragment() {

    private data class MonthlyAttendance(val month: String, val percentage: Float)

    private val monthlyData = listOf(
        MonthlyAttendance("Jan", 85f),
        MonthlyAttendance("Feb", 88f),
        MonthlyAttendance("Mar", 92f),
        MonthlyAttendance("Apr", 87f),
        MonthlyAttendance("May", 90f),
        MonthlyAttendance("Jun", 89f),
        MonthlyAttendance("Jul", 91f),
        MonthlyAttendance("Aug", 88f)
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_teacher_dashboard, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val name = SessionManager.getName(requireContext()) ?: "Teacher"
        view.findViewById<TextView>(R.id.teacherDashboardSubtitle).text =
            "Welcome back, $name"

        view.findViewById<TextView>(R.id.teacherAvgAttendance).text = "87.5%"
        view.findViewById<TextView>(R.id.teacherTotalStudents).text = "156"
        view.findViewById<TextView>(R.id.teacherTotalCourses).text = "4"
        view.findViewById<TextView>(R.id.teacherPresentToday).text = "32"
        view.findViewById<TextView>(R.id.teacherAbsentToday).text = "5"

        setupMonthlyChart(view.findViewById(R.id.teacherMonthlyChart))
    }

    private fun setupMonthlyChart(chart: LineChart) {
        val entries = monthlyData.mapIndexed { i, d -> Entry(i.toFloat(), d.percentage) }
        val labels = monthlyData.map { it.month }

        val ctx = requireContext()
        val dataSet = LineDataSet(entries, "Attendance %").apply {
            color = ChartThemeHelper.lineColor(ctx)
            valueTextColor = ChartThemeHelper.textColor(ctx)
            lineWidth = 3f
            setDrawCircles(true)
            setCircleColor(ChartThemeHelper.lineColor(ctx))
            circleRadius = 5f
            setDrawValues(false)
            mode = LineDataSet.Mode.CUBIC_BEZIER
            fillAlpha = 60
            setDrawFilled(true)
            fillColor = ChartThemeHelper.fillColor(ctx)
        }

        chart.apply {
            data = LineData(dataSet)
            description = Description().apply { text = "" }
            axisRight.isEnabled = false
            legend.isEnabled = false
            setTouchEnabled(false)
            setScaleEnabled(false)
            setExtraOffsets(8f, 12f, 12f, 8f)

            xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                granularity = 1f
                setDrawGridLines(false)
                valueFormatter = IndexAxisValueFormatter(labels)
                textColor = ChartThemeHelper.textColor(ctx)
            }

            axisLeft.apply {
                axisMinimum = 80f
                axisMaximum = 95f
                granularity = 5f
                setDrawGridLines(true)
                textColor = ChartThemeHelper.textColor(ctx)
            }

            invalidate()
            animateX(900)
        }
    }
}
