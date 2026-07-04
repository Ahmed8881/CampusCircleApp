package com.example.campuscircleapp.features.admin.fragments

import android.app.DatePickerDialog
import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.campuscircleapp.R
import com.example.campuscircleapp.adapters.SemesterAdapter
import com.example.campuscircleapp.core.models.UiState
import com.example.campuscircleapp.features.admin.models.SemesterResponse
import com.example.campuscircleapp.features.admin.viewModels.CreateSemesterViewModel
import com.example.campuscircleapp.shared.services.SessionManager
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import www.sanju.motiontoast.MotionToast
import www.sanju.motiontoast.MotionToastStyle
import java.text.SimpleDateFormat
import java.util.Locale

class CreateSemesterFragment : Fragment() {

    private lateinit var viewModel: CreateSemesterViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        inflater.inflate(R.layout.fragment_create_semester, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this)[CreateSemesterViewModel::class.java]

        val nameInput = view.findViewById<TextInputEditText>(R.id.semesterNameInput)
        val noInput = view.findViewById<TextInputEditText>(R.id.semesterNoInput)
        val startInput = view.findViewById<TextInputEditText>(R.id.semesterStartInput)
        val endInput = view.findViewById<TextInputEditText>(R.id.semesterEndInput)
        val createBtn = view.findViewById<MaterialButton>(R.id.createSemesterBtn)
        val recycler = view.findViewById<RecyclerView>(R.id.semestersRecyclerView)
        val emptyView = view.findViewById<TextView>(R.id.semestersEmptyView)

        recycler.layoutManager = LinearLayoutManager(requireContext())

        fun pickDate(input: TextInputEditText) {
            DatePickerDialog(requireContext()).apply {
                setOnDateSetListener { _, year, month, day ->
                    val mm = (month + 1).toString().padStart(2, '0')
                    val dd = day.toString().padStart(2, '0')
                    input.setText("$dd-$mm-$year")
                }
            }.show()
        }
        startInput.setOnClickListener { pickDate(startInput) }
        endInput.setOnClickListener { pickDate(endInput) }

        val token = SessionManager.getToken(requireContext()) ?: return

        viewModel.createState.asLiveData().observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Loading -> createBtn.isEnabled = false
                is UiState.Success -> {
                    createBtn.isEnabled = true
                    MotionToast.createColorToast(requireActivity(), "Created",
                        state.message ?: "Semester created!", MotionToastStyle.SUCCESS,
                        MotionToast.GRAVITY_TOP, MotionToast.LONG_DURATION,
                        ResourcesCompat.getFont(requireContext(), www.sanju.motiontoast.R.font.helvetica_regular))
                    nameInput.text?.clear(); noInput.text?.clear()
                    startInput.text?.clear(); endInput.text?.clear()
                    viewModel.loadSemesters(token)
                    viewModel.resetCreateState()
                }
                is UiState.Error -> {
                    createBtn.isEnabled = true
                    MotionToast.createColorToast(requireActivity(), "Error",
                        state.message, MotionToastStyle.ERROR,
                        MotionToast.GRAVITY_BOTTOM, MotionToast.LONG_DURATION,
                        ResourcesCompat.getFont(requireContext(), www.sanju.motiontoast.R.font.helvetica_regular))
                    viewModel.resetCreateState()
                }
                else -> createBtn.isEnabled = true
            }
        }

        viewModel.semestersState.asLiveData().observe(viewLifecycleOwner) { state ->
            if (state is UiState.Success) {
                if (state.data.isEmpty()) { emptyView.visibility = View.VISIBLE; recycler.visibility = View.GONE }
                else {
                    emptyView.visibility = View.GONE; recycler.visibility = View.VISIBLE
                    recycler.adapter = SemesterAdapter(
                        state.data,
                        onEdit = { s -> showEditDialog(s, token) },
                        onDelete = { s -> showDeleteDialog(s, token) }
                    )
                }
            }
        }

        viewModel.actionState.asLiveData().observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Success -> {
                    MotionToast.createColorToast(requireActivity(), "Success",
                        state.message ?: "Action completed", MotionToastStyle.SUCCESS,
                        MotionToast.GRAVITY_TOP, MotionToast.LONG_DURATION,
                        ResourcesCompat.getFont(requireContext(), www.sanju.motiontoast.R.font.helvetica_regular))
                    viewModel.loadSemesters(token)
                    viewModel.resetActionState()
                }
                is UiState.Error -> {
                    MotionToast.createColorToast(requireActivity(), "Error",
                        state.message, MotionToastStyle.ERROR,
                        MotionToast.GRAVITY_BOTTOM, MotionToast.LONG_DURATION,
                        ResourcesCompat.getFont(requireContext(), www.sanju.motiontoast.R.font.helvetica_regular))
                    viewModel.resetActionState()
                }
                else -> {}
            }
        }

        createBtn.setOnClickListener {
            val no = noInput.text.toString().toIntOrNull() ?: 1
            val startRaw = startInput.text.toString().trim()
            val endRaw = endInput.text.toString().trim()
            val startISO = convertDateToISO(startRaw)
            val endISO = convertDateToISO(endRaw)
            viewModel.createSemester(token, nameInput.text.toString().trim(), no, startISO, endISO)
        }

        viewModel.loadSemesters(token)
    }

    private fun convertDateToISO(dateStr: String): String {
        if (dateStr.isBlank()) return ""
        return try {
            val sdf = SimpleDateFormat("dd-MM-yyyy", Locale.US)
            val date = sdf.parse(dateStr)
            SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US).format(date!!)
        } catch (_: Exception) {
            dateStr
        }
    }

    private fun showEditDialog(s: SemesterResponse, token: String) {
        val inflater = LayoutInflater.from(requireContext())
        val dialogView = inflater.inflate(R.layout.dialog_edit_semester, null)
        val nameInput = dialogView.findViewById<EditText>(R.id.dialogSemesterName)
        val noInput = dialogView.findViewById<EditText>(R.id.dialogSemesterNo)
        val startInput = dialogView.findViewById<EditText>(R.id.dialogSemesterStart)
        val endInput = dialogView.findViewById<EditText>(R.id.dialogSemesterEnd)

        nameInput.setText(s.name)
        noInput.setText(s.number.toString())
        val displayStart = try {
            val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US)
            val date = sdf.parse(s.startDate.take(19))
            SimpleDateFormat("dd-MM-yyyy", Locale.US).format(date!!)
        } catch (_: Exception) { s.startDate.take(10) }
        val displayEnd = try {
            val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US)
            val date = sdf.parse(s.endDate.take(19))
            SimpleDateFormat("dd-MM-yyyy", Locale.US).format(date!!)
        } catch (_: Exception) { s.endDate.take(10) }
        startInput.setText(displayStart)
        endInput.setText(displayEnd)

        startInput.setOnClickListener {
            DatePickerDialog(requireContext()).apply {
                setOnDateSetListener { _, year, month, day ->
                    val mm = (month + 1).toString().padStart(2, '0')
                    val dd = day.toString().padStart(2, '0')
                    startInput.setText("$dd-$mm-$year")
                }
            }.show()
        }
        endInput.setOnClickListener {
            DatePickerDialog(requireContext()).apply {
                setOnDateSetListener { _, year, month, day ->
                    val mm = (month + 1).toString().padStart(2, '0')
                    val dd = day.toString().padStart(2, '0')
                    endInput.setText("$dd-$mm-$year")
                }
            }.show()
        }

        AlertDialog.Builder(requireContext())
            .setTitle("Edit Semester")
            .setView(dialogView)
            .setPositiveButton("Save") { _, _ ->
                val newName = nameInput.text.toString().trim()
                val newNo = noInput.text.toString().toIntOrNull() ?: s.number
                val newStart = convertDateToISO(startInput.text.toString().trim())
                val newEnd = convertDateToISO(endInput.text.toString().trim())
                viewModel.updateSemester(token, s.id, newName, newNo, newStart, newEnd)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showDeleteDialog(s: SemesterResponse, token: String) {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete Semester")
            .setMessage("Are you sure you want to delete \"${s.name}\"? This action cannot be undone.")
            .setPositiveButton("Delete") { _, _ ->
                viewModel.deleteSemester(token, s.id)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}
