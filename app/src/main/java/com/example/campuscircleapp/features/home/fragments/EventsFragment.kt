package com.example.campuscircleapp.features.home.fragments

import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.TextView
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

    private lateinit var birthdayList: RecyclerView
    private lateinit var emptyView: TextView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        inflater.inflate(R.layout.fragment_events, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this)[EventsViewModel::class.java]

        val monthTitle = view.findViewById<TextView>(R.id.eventsMonthTitle)
        val prevBtn = view.findViewById<TextView>(R.id.eventsPrevMonth)
        val nextBtn = view.findViewById<TextView>(R.id.eventsNextMonth)
        val calendarGrid = view.findViewById<GridLayout>(R.id.eventsCalendarGrid)
        birthdayList = view.findViewById(R.id.eventsBirthdayList)
        val loadingView = view.findViewById<View>(R.id.eventsLoadingView)
        emptyView = view.findViewById(R.id.eventsEmptyView)

        birthdayList.layoutManager = LinearLayoutManager(requireContext())

        prevBtn.setOnClickListener {
            currentCalendar.add(Calendar.MONTH, -1)
            renderCalendar(monthTitle, calendarGrid)
            showBirthdaysForCurrentMonth()
        }

        nextBtn.setOnClickListener {
            currentCalendar.add(Calendar.MONTH, 1)
            renderCalendar(monthTitle, calendarGrid)
            showBirthdaysForCurrentMonth()
        }

        val token = SessionManager.getToken(requireContext()) ?: return

        viewModel.birthdaysState.asLiveData().observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Loading -> {
                    loadingView.visibility = View.VISIBLE
                    birthdayList.visibility = View.GONE
                    emptyView.visibility = View.GONE
                }
                is UiState.Success -> {
                    loadingView.visibility = View.GONE
                    allBirthdays = state.data
                    renderCalendar(monthTitle, calendarGrid)
                    showBirthdaysForCurrentMonth()
                }
                is UiState.Error -> {
                    loadingView.visibility = View.GONE
                    emptyView.visibility = View.VISIBLE
                    emptyView.text = state.message
                    birthdayList.visibility = View.GONE
                }
                else -> {}
            }
        }

        viewModel.load(token)
        renderCalendar(monthTitle, calendarGrid)
    }

    private fun showBirthdaysForCurrentMonth() {
        val currentMonth = currentCalendar.get(Calendar.MONTH)
        val filtered = allBirthdays.filter { b ->
            try {
                val parts = b.dob.split("-")
                parts.size >= 3 && parts[1].toIntOrNull()?.minus(1) == currentMonth
            } catch (_: Exception) { false }
        }
        if (filtered.isEmpty()) {
            birthdayList.visibility = View.GONE
            emptyView.visibility = View.VISIBLE
            emptyView.text = "No birthdays this month"
        } else {
            birthdayList.visibility = View.VISIBLE
            emptyView.visibility = View.GONE
            birthdayList.adapter = BirthdayAdapter(filtered)
        }
    }

    private fun renderCalendar(monthTitle: TextView, grid: GridLayout) {
        val sdf = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
        monthTitle.text = sdf.format(currentCalendar.time)

        grid.removeAllViews()
        grid.columnCount = 7

        // Clean typography for headers
        val dayNames = listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
        dayNames.forEach { day ->
            val tv = TextView(requireContext()).apply {
                text = day
                textSize = 12f
                typeface = Typeface.create("sans-serif-medium", Typeface.NORMAL)
                gravity = Gravity.CENTER
                setTextColor(0xFF64748B.toInt()) // Slate grey
                val spec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
                layoutParams = GridLayout.LayoutParams(spec, spec).apply {
                    width = 0
                    height = ViewGroup.LayoutParams.WRAP_CONTENT
                    setMargins(2, 4, 2, 12)
                }
            }
            grid.addView(tv)
        }

        val cal = currentCalendar.clone() as Calendar
        cal.set(Calendar.DAY_OF_MONTH, 1)
        val firstDayOfWeek = cal.get(Calendar.DAY_OF_WEEK) - 1
        val daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
        val currentMonth = cal.get(Calendar.MONTH)

        // Correctly parse day from YYYY-MM-DD to highlight birthdays in the grid
        val birthdayDays = allBirthdays.mapNotNull { b ->
            try {
                val parts = b.dob.split("-")
                if (parts.size >= 3) {
                    val dobMonth = parts[1].toIntOrNull()?.minus(1)
                    val dobDay = parts[2].substringBefore('T').toIntOrNull()
                    if (dobMonth == currentMonth) dobDay else null
                } else null
            } catch (_: Exception) { null }
        }.toSet()

        // Empty cells before the 1st of the month
        repeat(firstDayOfWeek) {
            grid.addView(View(requireContext()).apply {
                val spec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
                layoutParams = GridLayout.LayoutParams(spec, spec).apply { width = 0 }
            })
        }

        val today = Calendar.getInstance()
        
        for (day in 1..daysInMonth) {
            val isToday = today.get(Calendar.MONTH) == currentMonth &&
                          today.get(Calendar.DAY_OF_MONTH) == day &&
                          today.get(Calendar.YEAR) == cal.get(Calendar.YEAR)
                          
            val hasBirthday = birthdayDays.contains(day)

            val tv = TextView(requireContext()).apply {
                text = day.toString()
                textSize = 14f
                gravity = Gravity.CENTER
                
                // Creates a perfectly square cell so the circular background fits perfectly
                val spec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
                layoutParams = GridLayout.LayoutParams(spec, spec).apply {
                    width = 0
                    // Ensures height matches width based on constraints
                    height = ViewGroup.LayoutParams.WRAP_CONTENT 
                    setMargins(4, 8, 4, 8)
                }
                
                // Add symmetric padding to force a circular look on the background shape
                setPadding(0, 16, 0, 16)

                when {
                    isToday -> {
                        // Premium programmatic circular blue highlight
                        background = GradientDrawable().apply {
                            shape = GradientDrawable.OVAL
                            setColor(0xFF3B82F6.toInt()) // Modern Blue
                        }
                        setTextColor(0xFFFFFFFF.toInt())
                        typeface = Typeface.create("sans-serif-medium", Typeface.BOLD)
                    }
                    hasBirthday -> {
                        // Subtle indicator for birthdays
                        setTextColor(0xFF3B82F6.toInt())
                        typeface = Typeface.create("sans-serif-bold", Typeface.BOLD)
                    }
                    else -> {
                        setTextColor(0xFF0F172A.toInt()) // Slate dark
                        typeface = Typeface.create("sans-serif", Typeface.NORMAL)
                    }
                }
            }
            grid.addView(tv)
        }
    }
}