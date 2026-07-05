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
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.campuscircleapp.R
import com.example.campuscircleapp.adapters.BirthdayAdapter
import com.example.campuscircleapp.core.models.UiState
import com.example.campuscircleapp.core.theme.ChartThemeHelper
import com.example.campuscircleapp.features.home.models.BirthdayResponse
import com.example.campuscircleapp.features.home.viewModels.EventsViewModel
import com.example.campuscircleapp.shared.services.SessionManager
import java.text.SimpleDateFormat
import java.util.*

class EventsFragment : Fragment() {

    private lateinit var viewModel: EventsViewModel
    private var currentCalendar = Calendar.getInstance()
    private var allBirthdays: List<BirthdayResponse> = emptyList()
    private var selectedDay: Int = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)

    private lateinit var birthdayList: RecyclerView
    private lateinit var emptyView: TextView
    private lateinit var monthTitle: TextView
    private lateinit var calendarGrid: GridLayout

    private val monthNames = listOf("January","February","March","April","May","June","July","August","September","October","November","December")

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        inflater.inflate(R.layout.fragment_events, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this)[EventsViewModel::class.java]

        monthTitle = view.findViewById(R.id.eventsMonthTitle)
        val prevBtn = view.findViewById<TextView>(R.id.eventsPrevMonth)
        val nextBtn = view.findViewById<TextView>(R.id.eventsNextMonth)
        calendarGrid = view.findViewById(R.id.eventsCalendarGrid)
        birthdayList = view.findViewById(R.id.eventsBirthdayList)
        val loadingView = view.findViewById<View>(R.id.eventsLoadingView)
        emptyView = view.findViewById(R.id.eventsEmptyView)

        birthdayList.layoutManager = LinearLayoutManager(requireContext())

        prevBtn.setOnClickListener {
            currentCalendar.add(Calendar.MONTH, -1)
            selectedDay = 1
            renderCalendar()
            showBirthdaysForSelectedDay()
        }

        nextBtn.setOnClickListener {
            currentCalendar.add(Calendar.MONTH, 1)
            selectedDay = 1
            renderCalendar()
            showBirthdaysForSelectedDay()
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
                    renderCalendar()
                    showBirthdaysForSelectedDay()
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
        renderCalendar()
    }

    private fun showBirthdaysForSelectedDay() {
        val currentMonth = currentCalendar.get(Calendar.MONTH)
        val currentMonthName = monthNames[currentMonth]
        val filtered = allBirthdays.filter { b ->
            b.birthdayMonth?.lowercase() == currentMonthName.lowercase() && b.birthdayDay == selectedDay
        }
        if (filtered.isEmpty()) {
            birthdayList.visibility = View.GONE
            emptyView.visibility = View.VISIBLE
            emptyView.text = "No birthdays on this day"
        } else {
            birthdayList.visibility = View.VISIBLE
            emptyView.visibility = View.GONE
            birthdayList.adapter = BirthdayAdapter(filtered)
        }
    }

    private fun renderCalendar() {
        val sdf = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
        monthTitle.text = sdf.format(currentCalendar.time)

        calendarGrid.removeAllViews()
        calendarGrid.columnCount = 7

        val dayNames = listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
        dayNames.forEach { day ->
            val tv = TextView(requireContext()).apply {
                text = day
                textSize = 12f
                typeface = ResourcesCompat.getFont(requireContext(), R.font.inter_medium)
                gravity = Gravity.CENTER
                setTextColor(ChartThemeHelper.mutedColor(requireContext()))
                val spec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
                layoutParams = GridLayout.LayoutParams(spec, spec).apply {
                    width = 0
                    height = ViewGroup.LayoutParams.WRAP_CONTENT
                    setMargins(2, 4, 2, 12)
                }
            }
            calendarGrid.addView(tv)
        }

        val cal = currentCalendar.clone() as Calendar
        cal.set(Calendar.DAY_OF_MONTH, 1)
        val firstDayOfWeek = cal.get(Calendar.DAY_OF_WEEK) - 1
        val daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
        val currentMonth = cal.get(Calendar.MONTH)

        val birthdayDays = allBirthdays.mapNotNull { b ->
            if (b.birthdayMonth?.lowercase() == monthNames[currentMonth].lowercase()) b.birthdayDay else null
        }.toSet()

        repeat(firstDayOfWeek) {
            calendarGrid.addView(View(requireContext()).apply {
                val spec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
                layoutParams = GridLayout.LayoutParams(spec, spec).apply { width = 0 }
            })
        }

        val today = Calendar.getInstance()
        val ctx = requireContext()

        for (day in 1..daysInMonth) {
            val isToday = today.get(Calendar.MONTH) == currentMonth &&
                          today.get(Calendar.DAY_OF_MONTH) == day &&
                          today.get(Calendar.YEAR) == cal.get(Calendar.YEAR)

            val hasBirthday = birthdayDays.contains(day)
            val isSelected = day == selectedDay && !isToday

            val tv = TextView(ctx).apply {
                text = day.toString()
                textSize = 14f
                gravity = Gravity.CENTER

                val spec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
                layoutParams = GridLayout.LayoutParams(spec, spec).apply {
                    width = 0
                    height = ViewGroup.LayoutParams.WRAP_CONTENT
                    setMargins(4, 8, 4, 8)
                }

                setPadding(0, 16, 0, 16)

                when {
                    isToday -> {
                        background = GradientDrawable().apply {
                            shape = GradientDrawable.OVAL
                            setColor(ChartThemeHelper.brandAccent(ctx))
                        }
                        setTextColor(ChartThemeHelper.surfaceColor(ctx))
                        typeface = ResourcesCompat.getFont(ctx, R.font.inter_bold)
                    }
                    isSelected -> {
                        background = GradientDrawable().apply {
                            shape = GradientDrawable.OVAL
                            setColor(ChartThemeHelper.brandPrimary(ctx))
                            alpha = 30
                        }
                        setTextColor(ChartThemeHelper.brandPrimary(ctx))
                        typeface = ResourcesCompat.getFont(ctx, R.font.inter_bold)
                    }
                    hasBirthday -> {
                        setTextColor(ChartThemeHelper.brandAccent(ctx))
                        typeface = ResourcesCompat.getFont(ctx, R.font.inter_bold)
                    }
                    else -> {
                        setTextColor(ChartThemeHelper.headingColor(ctx))
                        typeface = ResourcesCompat.getFont(ctx, R.font.inter_regular)
                    }
                }

                setOnClickListener {
                    selectedDay = day
                    renderCalendar()
                    showBirthdaysForSelectedDay()
                }
            }
            calendarGrid.addView(tv)
        }
    }
}