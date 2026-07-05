package com.example.campuscircleapp.features.home.fragments

import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
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
    private lateinit var calendarGrid: LinearLayout

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

        val ctx = requireContext()

        val cal = currentCalendar.clone() as Calendar
        cal.set(Calendar.DAY_OF_MONTH, 1)
        val firstDayOfWeek = cal.get(Calendar.DAY_OF_WEEK) - 1
        val daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
        val currentMonth = cal.get(Calendar.MONTH)

        val birthdayDays = allBirthdays.mapNotNull { b ->
            if (b.birthdayMonth?.lowercase() == monthNames[currentMonth].lowercase()) b.birthdayDay else null
        }.toSet()

        val cells = mutableListOf<Int>()
        repeat(firstDayOfWeek) { cells.add(0) }
        for (d in 1..daysInMonth) cells.add(d)
        while (cells.size % 7 != 0) cells.add(0)

        for (row in cells.chunked(7)) {
            val rowLayout = LinearLayout(ctx).apply {
                orientation = LinearLayout.HORIZONTAL
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
            }

            for (dayNum in row) {
                if (dayNum == 0) {
                    rowLayout.addView(View(ctx).apply {
                        layoutParams = LinearLayout.LayoutParams(0, 1, 1f)
                    })
                } else {
                    val isToday = today.get(Calendar.MONTH) == currentMonth &&
                                  today.get(Calendar.DAY_OF_MONTH) == dayNum &&
                                  today.get(Calendar.YEAR) == cal.get(Calendar.YEAR)
                    val hasBirthday = birthdayDays.contains(dayNum)
                    val isSelected = dayNum == selectedDay && !isToday

                    val tv = TextView(ctx).apply {
                        text = dayNum.toString()
                        textSize = 14f
                        gravity = Gravity.CENTER
                        setPadding(0, 16, 0, 16)
                        layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f).apply {
                            setMargins(4, 8, 4, 8)
                        }

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
                            selectedDay = dayNum
                            renderCalendar()
                            showBirthdaysForSelectedDay()
                        }
                    }
                    rowLayout.addView(tv)
                }
            }

            calendarGrid.addView(rowLayout)
        }
    }

    private val today = Calendar.getInstance()
}