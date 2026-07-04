package com.example.campuscircleapp.features.admin.fragments

import android.app.TimePickerDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import java.util.Calendar
import androidx.core.content.res.ResourcesCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.campuscircleapp.R
import com.example.campuscircleapp.adapters.TimetableAdapter
import com.example.campuscircleapp.core.models.UiState
import com.example.campuscircleapp.features.admin.models.CreateTimetableEntryRequest
import com.example.campuscircleapp.features.admin.models.UpdateTimetableEntryRequest
import com.example.campuscircleapp.features.admin.viewModels.AdminTimetableViewModel
import com.example.campuscircleapp.features.home.models.AdminCourseResponse
import com.example.campuscircleapp.features.home.models.TimetableEntry
import com.example.campuscircleapp.shared.services.SessionManager
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import www.sanju.motiontoast.MotionToast
import www.sanju.motiontoast.MotionToastStyle

class AdminTimetableFragment : Fragment() {

    private lateinit var viewModel: AdminTimetableViewModel
    private var courses: List<AdminCourseResponse> = emptyList()
    private var editEntry: TimetableEntry? = null

    private val dayNames = listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday")

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        inflater.inflate(R.layout.fragment_admin_timetable, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this)[AdminTimetableViewModel::class.java]

        val addBtn = view.findViewById<MaterialButton>(R.id.addTimetableEntryBtn)
        val recycler = view.findViewById<RecyclerView>(R.id.adminTimetableRecyclerView)
        val emptyView = view.findViewById<TextView>(R.id.adminTimetableEmptyView)

        recycler.layoutManager = LinearLayoutManager(requireContext())

        val token = SessionManager.getToken(requireContext()) ?: return

        viewModel.loadCourses(token)
        viewModel.load(token)

        viewModel.timetableState.asLiveData().observe(viewLifecycleOwner) { state ->
            if (state is UiState.Success) {
                if (state.data.isEmpty()) { emptyView.visibility = View.VISIBLE; recycler.visibility = View.GONE }
                else { emptyView.visibility = View.GONE; recycler.visibility = View.VISIBLE; recycler.adapter = TimetableAdapter(state.data) { entry -> showEntryDialog(token, entry) } }
            }
        }

        viewModel.coursesState.asLiveData().observe(viewLifecycleOwner) { state ->
            if (state is UiState.Success) courses = state.data
        }

        viewModel.createState.asLiveData().observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Success -> {
                    MotionToast.createColorToast(requireActivity(), "Added",
                        state.message ?: "Entry added!", MotionToastStyle.SUCCESS,
                        MotionToast.GRAVITY_TOP, MotionToast.LONG_DURATION,
                        ResourcesCompat.getFont(requireContext(), www.sanju.motiontoast.R.font.helvetica_regular))
                    viewModel.load(token)
                    viewModel.resetCreateState()
                }
                is UiState.Error -> {
                    MotionToast.createColorToast(requireActivity(), "Error",
                        state.message, MotionToastStyle.ERROR,
                        MotionToast.GRAVITY_BOTTOM, MotionToast.LONG_DURATION,
                        ResourcesCompat.getFont(requireContext(), www.sanju.motiontoast.R.font.helvetica_regular))
                    viewModel.resetCreateState()
                }
                else -> {}
            }
        }

        viewModel.updateState.asLiveData().observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Success -> {
                    MotionToast.createColorToast(requireActivity(), "Updated",
                        state.message ?: "Entry updated!", MotionToastStyle.SUCCESS,
                        MotionToast.GRAVITY_TOP, MotionToast.LONG_DURATION,
                        ResourcesCompat.getFont(requireContext(), www.sanju.motiontoast.R.font.helvetica_regular))
                    viewModel.load(token)
                    viewModel.resetUpdateState()
                }
                is UiState.Error -> {
                    MotionToast.createColorToast(requireActivity(), "Error",
                        state.message, MotionToastStyle.ERROR,
                        MotionToast.GRAVITY_BOTTOM, MotionToast.LONG_DURATION,
                        ResourcesCompat.getFont(requireContext(), www.sanju.motiontoast.R.font.helvetica_regular))
                    viewModel.resetUpdateState()
                }
                else -> {}
            }
        }

        viewModel.deleteState.asLiveData().observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Success -> {
                    MotionToast.createColorToast(requireActivity(), "Deleted",
                        state.message ?: "Entry deleted!", MotionToastStyle.SUCCESS,
                        MotionToast.GRAVITY_TOP, MotionToast.LONG_DURATION,
                        ResourcesCompat.getFont(requireContext(), www.sanju.motiontoast.R.font.helvetica_regular))
                    viewModel.load(token)
                    viewModel.resetDeleteState()
                }
                is UiState.Error -> {
                    MotionToast.createColorToast(requireActivity(), "Error",
                        state.message, MotionToastStyle.ERROR,
                        MotionToast.GRAVITY_BOTTOM, MotionToast.LONG_DURATION,
                        ResourcesCompat.getFont(requireContext(), www.sanju.motiontoast.R.font.helvetica_regular))
                    viewModel.resetDeleteState()
                }
                else -> {}
            }
        }

        addBtn.setOnClickListener { showEntryDialog(token, null) }
    }

    private fun showEntryDialog(token: String, entry: TimetableEntry?) {
        editEntry = entry
        val isEdit = entry != null
        val dialogView = layoutInflater.inflate(R.layout.dialog_timetable_entry, null)

        val title = dialogView.findViewById<TextView>(R.id.dialogTitle)
        val courseSpinner = dialogView.findViewById<Spinner>(R.id.dialogCourseSpinner)
        val daySpinner = dialogView.findViewById<Spinner>(R.id.dialogDaySpinner)
        val startTimeInput = dialogView.findViewById<TextInputEditText>(R.id.dialogStartTimeInput)
        val endTimeInput = dialogView.findViewById<TextInputEditText>(R.id.dialogEndTimeInput)
        val roomInput = dialogView.findViewById<TextInputEditText>(R.id.dialogRoomInput)
        val deleteBtn = dialogView.findViewById<MaterialButton>(R.id.dialogDeleteBtn)
        val saveBtn = dialogView.findViewById<MaterialButton>(R.id.dialogSaveBtn)
        val cancelBtn = dialogView.findViewById<MaterialButton>(R.id.dialogCancelBtn)

        title.text = if (isEdit) "Edit Class" else "Add Class"

        val courseNames = courses.map { "${it.name} (${it.code})" }
        val courseAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, courseNames).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
        courseSpinner.adapter = courseAdapter

        if (isEdit) {
            deleteBtn.visibility = View.VISIBLE

            startTimeInput.setText(entry.startTime.take(5))
            endTimeInput.setText(entry.endTime.take(5))
            roomInput.setText(entry.room)

            val courseIdx = courses.indexOfFirst { it.id == entry.courseId }
            if (courseIdx >= 0) courseSpinner.setSelection(courseIdx)

            val dayIdx = when {
                entry.day in 0..6 -> entry.day
                entry.day in 1..7 -> entry.day - 1
                else -> 0
            }
            daySpinner.setSelection(dayIdx)
        } else {
            deleteBtn.visibility = View.GONE
        }

        daySpinner.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, dayNames).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }

        startTimeInput.addTextChangedListener(EndTimeAutoFillWatcher(startTimeInput, endTimeInput))

        val startCal = Calendar.getInstance()
        if (isEdit) {
            val parts = entry!!.startTime.split(":")
            startCal.set(Calendar.HOUR_OF_DAY, parts[0].toIntOrNull() ?: 0)
            startCal.set(Calendar.MINUTE, parts[1].toIntOrNull() ?: 0)
        }
        val endCal = Calendar.getInstance()
        if (isEdit) {
            val parts = entry!!.endTime.split(":")
            endCal.set(Calendar.HOUR_OF_DAY, parts[0].toIntOrNull() ?: 0)
            endCal.set(Calendar.MINUTE, parts[1].toIntOrNull() ?: 0)
        }

        startTimeInput.setOnClickListener {
            TimePickerDialog(requireContext(), { _, hour, minute ->
                val time = String.format("%02d:%02d", hour, minute)
                startTimeInput.setText(time)
                startCal.set(Calendar.HOUR_OF_DAY, hour)
                startCal.set(Calendar.MINUTE, minute)
                endTimeInput.text?.let {
                    if (it.isBlank()) {
                        val newHour = (hour + 1) % 24
                        endTimeInput.setText(String.format("%02d:%02d", newHour, minute))
                        endCal.set(Calendar.HOUR_OF_DAY, newHour)
                        endCal.set(Calendar.MINUTE, minute)
                    }
                }
            }, startCal.get(Calendar.HOUR_OF_DAY), startCal.get(Calendar.MINUTE), false).show()
        }
        endTimeInput.setOnClickListener {
            TimePickerDialog(requireContext(), { _, hour, minute ->
                val time = String.format("%02d:%02d", hour, minute)
                endTimeInput.setText(time)
                endCal.set(Calendar.HOUR_OF_DAY, hour)
                endCal.set(Calendar.MINUTE, minute)
            }, endCal.get(Calendar.HOUR_OF_DAY), endCal.get(Calendar.MINUTE), false).show()
        }

        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setView(dialogView)
            .setCancelable(true)
            .create()

        saveBtn.setOnClickListener {
            val coursePos = courseSpinner.selectedItemPosition
            if (coursePos < 0 || coursePos >= courses.size) {
                MotionToast.createColorToast(requireActivity(), "Error", "Please select a course",
                    MotionToastStyle.ERROR, MotionToast.GRAVITY_BOTTOM, MotionToast.LONG_DURATION,
                    ResourcesCompat.getFont(requireContext(), www.sanju.motiontoast.R.font.helvetica_regular))
                return@setOnClickListener
            }
            val courseId = courses[coursePos].id
            val day = if (isEdit) dayToString(entry!!.day) else dayNames[daySpinner.selectedItemPosition]
            val start = startTimeInput.text?.toString()?.trim() ?: ""
            val end = endTimeInput.text?.toString()?.trim() ?: ""
            val room = roomInput.text?.toString()?.trim() ?: ""

            if (start.isBlank() || end.isBlank() || room.isBlank()) {
                MotionToast.createColorToast(requireActivity(), "Error", "All fields required",
                    MotionToastStyle.ERROR, MotionToast.GRAVITY_BOTTOM, MotionToast.LONG_DURATION,
                    ResourcesCompat.getFont(requireContext(), www.sanju.motiontoast.R.font.helvetica_regular))
                return@setOnClickListener
            }

            val fmtStart = formatTime(start)
            val fmtEnd = formatTime(end)

            if (isEdit) {
                viewModel.updateEntry(token, UpdateTimetableEntryRequest(
                    id = entry!!.id,
                    courseId = courseId,
                    dayOfWeek = day,
                    startTime = fmtStart,
                    endTime = fmtEnd,
                    room = room,
                    spaceId = 1L
                ))
            } else {
                viewModel.createEntry(token, CreateTimetableEntryRequest(
                    courseId = courseId,
                    dayOfWeek = day,
                    startTime = fmtStart,
                    endTime = fmtEnd,
                    room = room,
                    spaceId = 1L
                ))
            }
            dialog.dismiss()
        }

        cancelBtn.setOnClickListener { dialog.dismiss() }

        deleteBtn.setOnClickListener {
            dialog.dismiss()
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Delete Entry")
                .setMessage("Are you sure you want to delete this timetable entry?")
                .setPositiveButton("Delete") { _, _ ->
                    entry?.let { viewModel.deleteEntry(token, it.id) }
                }
                .setNegativeButton("Cancel", null)
                .show()
        }

        dialog.show()
    }

    private fun dayToString(day: Int): String {
        return when (day) {
            in 0..6 -> dayNames.getOrElse(day) { "Unknown" }
            in 1..7 -> dayNames.getOrElse(day - 1) { "Unknown" }
            else -> "Unknown"
        }
    }

    private fun formatTime(time: String): String {
        val cleaned = time.replace(" ", "")
        if (cleaned.length == 5 && cleaned[2] == ':') return "$cleaned:00"
        if (cleaned.length == 4 && cleaned[1] == ':') return "0$cleaned:00"
        if (cleaned.length == 2) return "$cleaned:00:00"
        return cleaned
    }

    private class EndTimeAutoFillWatcher(
        private val startInput: TextInputEditText,
        private val endInput: TextInputEditText
    ) : TextWatcher {
        private var isUpdating = false

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

        override fun afterTextChanged(s: Editable?) {
            if (isUpdating) return
            val text = s?.toString()?.trim() ?: return
            if (text.length == 5 && text[2] == ':') {
                val parts = text.split(":")
                val hour = parts[0].toIntOrNull() ?: return
                val minute = parts[1]
                val newHour = (hour + 1) % 24
                val newTime = "${newHour.toString().padStart(2, '0')}:$minute"
                isUpdating = true
                if (endInput.text?.toString().isNullOrBlank()) {
                    endInput.setText(newTime)
                }
                isUpdating = false
            }
        }
    }
}
