package com.example.campuscircleapp.features.home.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.campuscircleapp.R
import com.example.campuscircleapp.adapters.TimetableAdapter
import com.example.campuscircleapp.core.models.UiState
import com.example.campuscircleapp.features.home.models.TimetableEntry
import com.example.campuscircleapp.features.home.viewModels.TimetableViewModel
import com.example.campuscircleapp.shared.services.SessionManager
import com.google.android.material.progressindicator.CircularProgressIndicator
import java.util.Calendar

class TimetableFragment : Fragment() {

    private val viewModel by lazy {
        ViewModelProvider(this)[TimetableViewModel::class.java]
    }

    private lateinit var loadingView: CircularProgressIndicator
    private lateinit var errorView: TextView
    private lateinit var contentView: androidx.core.widget.NestedScrollView
    private lateinit var todayDayName: TextView
    private lateinit var noClassesToday: TextView
    private lateinit var noTimetableText: TextView
    private lateinit var todayAdapter: TimetableAdapter
    private lateinit var weekAdapter: TimetableAdapter

    private val dayNames = listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_timetable, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadingView = view.findViewById(R.id.timetableLoading)
        errorView = view.findViewById(R.id.timetableError)
        contentView = view.findViewById(R.id.timetableContent)
        todayDayName = view.findViewById(R.id.todayDayName)
        noClassesToday = view.findViewById(R.id.noClassesToday)
        noTimetableText = view.findViewById(R.id.noTimetableText)

        todayAdapter = TimetableAdapter(emptyList())
        weekAdapter = TimetableAdapter(emptyList())

        val todayRecycler = view.findViewById<RecyclerView>(R.id.todayClassesRecycler)
        todayRecycler.layoutManager = LinearLayoutManager(requireContext())
        todayRecycler.adapter = todayAdapter

        val weekRecycler = view.findViewById<RecyclerView>(R.id.weekTimetableRecycler)
        weekRecycler.layoutManager = LinearLayoutManager(requireContext())
        weekRecycler.adapter = weekAdapter

        val todayIndex = getTodayIndex()
        todayDayName.text = if (todayIndex >= 0) dayNames[todayIndex] else "Weekend"

        observeState()

        val token = SessionManager.getToken(requireContext())
        if (token.isNullOrBlank()) {
            showError("Session expired. Please sign in again.")
            return
        }

        viewModel.loadTimetable(token, 1L)
    }

    private fun observeState() {
        viewModel.timetableState.asLiveData().observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Idle -> {
                    loadingView.isVisible = false
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
                    renderTimetable(state.data)
                }
                is UiState.Error -> showError(state.message)
                else -> {}
            }
        }
    }

    private fun renderTimetable(entries: List<TimetableEntry>) {
        val todayIndex = getTodayIndex()
        val todayEntries = if (todayIndex >= 0) {
            entries.filter { it.day - 1 == todayIndex }
        } else {
            emptyList()
        }

        if (todayEntries.isEmpty()) {
            noClassesToday.isVisible = true
        } else {
            noClassesToday.isVisible = false
            todayAdapter.updateData(todayEntries)
        }

        if (entries.isEmpty()) {
            noTimetableText.isVisible = true
        } else {
            noTimetableText.isVisible = false
            weekAdapter.updateData(entries.sortedWith(compareBy({ it.day }, { it.startTime })))
        }
    }

    private fun showError(message: String) {
        loadingView.isVisible = false
        contentView.isVisible = false
        errorView.isVisible = true
        errorView.text = message
    }

    private fun getTodayIndex(): Int {
        val jsDay = Calendar.getInstance().get(Calendar.DAY_OF_WEEK)
        return when (jsDay) {
            Calendar.MONDAY -> 0
            Calendar.TUESDAY -> 1
            Calendar.WEDNESDAY -> 2
            Calendar.THURSDAY -> 3
            Calendar.FRIDAY -> 4
            else -> -1
        }
    }
}
