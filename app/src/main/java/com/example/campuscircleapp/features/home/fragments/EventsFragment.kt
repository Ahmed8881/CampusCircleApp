package com.example.campuscircleapp.features.home.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.campuscircleapp.R
import com.example.campuscircleapp.adapters.BirthdayAdapter
import com.example.campuscircleapp.core.models.UiState
import com.example.campuscircleapp.features.home.models.BirthdayResponse
import com.example.campuscircleapp.features.home.viewModels.EventsViewModel
import com.example.campuscircleapp.shared.services.SessionManager
import java.text.SimpleDateFormat
import java.util.*

class EventsFragment : Fragment() {

    private lateinit var viewModel: EventsViewModel
    private var currentCalendar = Calendar.getInstance()
    private var allBirthdays: List<BirthdayResponse> = emptyList()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        inflater.inflate(R.layout.fragment_events, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this)[EventsViewModel::class.java]

        val monthTitle = view.findViewById<TextView>(R.id.eventsMonthTitle)
        val prevBtn = view.findViewById<TextView>(R.id.eventsPrevMonth)
        val nextBtn = view.findViewById<TextView>(R.id.eventsNextMonth)
        val calendarGrid = view.findViewById<GridLayout>(R.id.eventsCalendarGrid)
        val birthdayList = view.findViewById<RecyclerView>(R.id.eventsBirthdayList)
        val loadingView = view.findViewById<View>(R.id.eventsLoadingView)
        val emptyView = view.findViewById<TextView>(R.id.eventsEmptyView)

        birthdayList.layoutManager = LinearLayoutManager(requireContext())

        prevBtn.setOnClickListener {
            currentCalendar.add(Calendar.MONTH, -1)
            renderCalendar(monthTitle, calendarGrid)
        }
        nextBtn.setOnClickListener {
            currentCalendar.add(Calendar.MONTH, 1)
            renderCalendar(monthTitle, calendarGrid)
        }

        val token = SessionManager.getToken(requireContext()) ?: return

        viewModel.birthdaysState.asLiveData().observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Loading -> {
                    loadingView.visibility = View.VISIBLE
                    birthdayList.visibility = View.GONE
                }
                is UiState.Success -> {
                    loadingView.visibility = View.GONE
                    allBirthdays = state.data
                    renderCalendar(monthTitle, calendarGrid)
                    if (state.data.isEmpty()) {
                        birthdayList.visibility = View.GONE
                        emptyView.visibility = View.VISIBLE
                    } else {
                        birthdayList.visibility = View.VISIBLE
                        emptyView.visibility = View.GONE
                        birthdayList.adapter = BirthdayAdapter(state.data)
                    }
                }
                is UiState.Error -> {
                    loadingView.visibility = View.GONE
                    emptyView.visibility = View.VISIBLE
                    emptyView.text = state.message
                }
                else -> {}
            }
        }

        viewModel.load(token)
        renderCalendar(monthTitle, calendarGrid)
    }

    private fun renderCalendar(monthTitle: TextView, grid: GridLayout) {
        val sdf = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
        monthTitle.text = sdf.format(currentCalendar.time)

        grid.removeAllViews()
        grid.columnCount = 7

        val dayNames = listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
        dayNames.forEach { day ->
            val tv = TextView(requireContext()).apply {
                text = day
                textSize = 11f
                gravity = android.view.Gravity.CENTER
                setTextColor(0xFF94A3B8.toInt())
                val spec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
                layoutParams = GridLayout.LayoutParams(spec, spec).apply {
                    width = 0
                    height = ViewGroup.LayoutParams.WRAP_CONTENT
                    setMargins(2, 4, 2, 4)
                }
            }
            grid.addView(tv)
        }

        val cal = currentCalendar.clone() as Calendar
        cal.set(Calendar.DAY_OF_MONTH, 1)
        val firstDayOfWeek = cal.get(Calendar.DAY_OF_WEEK) - 1
        val daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
        val currentMonth = cal.get(Calendar.MONTH)

        val birthdayDays = allBirthdays.mapNotNull { b ->
            try {
                val parts = b.dob.split("-")
                if (parts.size >= 2) parts[1].toIntOrNull()?.minus(1) else null
            } catch (_: Exception) { null }
        }.toSet()

        repeat(firstDayOfWeek) {
            grid.addView(TextView(requireContext()).apply {
                val spec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
                layoutParams = GridLayout.LayoutParams(spec, spec).apply { width = 0 }
            })
        }

        val today = Calendar.getInstance()
        for (day in 1..daysInMonth) {
            val isToday = today.get(Calendar.MONTH) == currentMonth &&
                    today.get(Calendar.DAY_OF_MONTH) == day &&
                    today.get(Calendar.YEAR) == cal.get(Calendar.YEAR)

            val tv = TextView(requireContext()).apply {
                text = day.toString()
                textSize = 13f
                gravity = android.view.Gravity.CENTER
                setPadding(4, 8, 4, 8)

                if (isToday) {
                    setBackgroundColor(0xFF3B82F6.toInt())
                    setTextColor(0xFFFFFFFF.toInt())
                } else {
                    setTextColor(0xFF1E293B.toInt())
                }

                val spec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
                layoutParams = GridLayout.LayoutParams(spec, spec).apply {
                    width = 0
                    setMargins(2, 2, 2, 2)
                }
            }
            grid.addView(tv)
        }
    }
}
