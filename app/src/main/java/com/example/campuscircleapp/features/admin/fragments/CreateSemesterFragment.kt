package com.example.campuscircleapp.features.admin.fragments

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.example.campuscircleapp.features.admin.viewModels.CreateSemesterViewModel
import com.example.campuscircleapp.shared.services.SessionManager
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import www.sanju.motiontoast.MotionToast
import www.sanju.motiontoast.MotionToastStyle

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
                else { emptyView.visibility = View.GONE; recycler.visibility = View.VISIBLE; recycler.adapter = SemesterAdapter(state.data) }
            }
        }

        createBtn.setOnClickListener {
            val no = noInput.text.toString().toIntOrNull() ?: 1
            viewModel.createSemester(token, nameInput.text.toString().trim(), no,
                startInput.text.toString().trim(), endInput.text.toString().trim())
        }

        viewModel.loadSemesters(token)
    }
}
