package com.example.campuscircleapp.features.admin.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import android.widget.TimePicker
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.campuscircleapp.R
import com.example.campuscircleapp.adapters.TimetableAdapter
import com.example.campuscircleapp.core.models.UiState
import com.example.campuscircleapp.features.admin.models.CreateTimetableEntryRequest
import com.example.campuscircleapp.features.admin.viewModels.AdminTimetableViewModel
import com.example.campuscircleapp.shared.services.SessionManager
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import www.sanju.motiontoast.MotionToast
import www.sanju.motiontoast.MotionToastStyle

class AdminTimetableFragment : Fragment() {

    private lateinit var viewModel: AdminTimetableViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        inflater.inflate(R.layout.fragment_admin_timetable, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this)[AdminTimetableViewModel::class.java]

        val daySpinner = view.findViewById<Spinner>(R.id.timetableDaySpinner)
        val courseIdInput = view.findViewById<TextInputEditText>(R.id.timetableCourseIdInput)
        val startTimeInput = view.findViewById<TextInputEditText>(R.id.timetableStartTimeInput)
        val endTimeInput = view.findViewById<TextInputEditText>(R.id.timetableEndTimeInput)
        val roomInput = view.findViewById<TextInputEditText>(R.id.timetableRoomInput)
        val addBtn = view.findViewById<MaterialButton>(R.id.addTimetableEntryBtn)
        val recycler = view.findViewById<RecyclerView>(R.id.adminTimetableRecyclerView)
        val emptyView = view.findViewById<TextView>(R.id.adminTimetableEmptyView)

        recycler.layoutManager = LinearLayoutManager(requireContext())

        val days = listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday")
        daySpinner.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, days).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }

        val token = SessionManager.getToken(requireContext()) ?: return

        viewModel.createState.asLiveData().observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Loading -> addBtn.isEnabled = false
                is UiState.Success -> {
                    addBtn.isEnabled = true
                    MotionToast.createColorToast(requireActivity(), "Added",
                        state.message ?: "Entry added!", MotionToastStyle.SUCCESS,
                        MotionToast.GRAVITY_TOP, MotionToast.LONG_DURATION,
                        ResourcesCompat.getFont(requireContext(), www.sanju.motiontoast.R.font.helvetica_regular))
                    listOf(courseIdInput, startTimeInput, endTimeInput, roomInput).forEach { it.text?.clear() }
                    viewModel.load(token)
                    viewModel.resetCreateState()
                }
                is UiState.Error -> {
                    addBtn.isEnabled = true
                    MotionToast.createColorToast(requireActivity(), "Error",
                        state.message, MotionToastStyle.ERROR,
                        MotionToast.GRAVITY_BOTTOM, MotionToast.LONG_DURATION,
                        ResourcesCompat.getFont(requireContext(), www.sanju.motiontoast.R.font.helvetica_regular))
                    viewModel.resetCreateState()
                }
                else -> addBtn.isEnabled = true
            }
        }

        viewModel.timetableState.asLiveData().observe(viewLifecycleOwner) { state ->
            if (state is UiState.Success) {
                if (state.data.isEmpty()) { emptyView.visibility = View.VISIBLE; recycler.visibility = View.GONE }
                else { emptyView.visibility = View.GONE; recycler.visibility = View.VISIBLE; recycler.adapter = TimetableAdapter(state.data) }
            }
        }

        addBtn.setOnClickListener {
            val courseId = courseIdInput.text.toString().toLongOrNull() ?: 1L
            val day = daySpinner.selectedItem.toString()
            val start = startTimeInput.text.toString().trim()
            val end = endTimeInput.text.toString().trim()
            val room = roomInput.text.toString().trim()
            if (start.isBlank() || end.isBlank() || room.isBlank()) {
                MotionToast.createColorToast(requireActivity(), "Error", "All fields required",
                    MotionToastStyle.ERROR, MotionToast.GRAVITY_BOTTOM, MotionToast.LONG_DURATION,
                    ResourcesCompat.getFont(requireContext(), www.sanju.motiontoast.R.font.helvetica_regular))
                return@setOnClickListener
            }
            viewModel.createEntry(token, CreateTimetableEntryRequest(courseId, day, start, end, room, 1L))
        }

        viewModel.load(token)
    }
}
