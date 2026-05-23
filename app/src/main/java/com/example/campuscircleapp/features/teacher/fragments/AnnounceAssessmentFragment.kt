package com.example.campuscircleapp.features.teacher.fragments

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioGroup
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import com.example.campuscircleapp.R
import com.example.campuscircleapp.core.models.UiState
import com.example.campuscircleapp.features.teacher.models.AnnounceAssessmentRequest
import com.example.campuscircleapp.features.teacher.viewModels.AnnounceAssessmentViewModel
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import java.util.Calendar

class AnnounceAssessmentFragment : Fragment() {

    private val viewModel by lazy {
        ViewModelProvider(this)[AnnounceAssessmentViewModel::class.java]
    }

    private lateinit var typeGroup: RadioGroup
    private lateinit var titleInput: TextInputEditText
    private lateinit var descriptionInput: TextInputEditText
    private lateinit var dueDateInput: TextInputEditText
    private lateinit var totalMarksInput: TextInputEditText
    private lateinit var publishBtn: MaterialButton
    private lateinit var successText: TextView
    private lateinit var errorText: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_announce_assessment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        typeGroup = view.findViewById(R.id.assessmentTypeGroup)
        titleInput = view.findViewById(R.id.assessmentTitle)
        descriptionInput = view.findViewById(R.id.assessmentDescription)
        dueDateInput = view.findViewById(R.id.assessmentDueDate)
        totalMarksInput = view.findViewById(R.id.assessmentTotalMarks)
        publishBtn = view.findViewById(R.id.publishAssessmentBtn)
        successText = view.findViewById(R.id.assessmentSuccessText)
        errorText = view.findViewById(R.id.assessmentErrorText)

        dueDateInput.setOnClickListener { showDatePicker() }

        publishBtn.setOnClickListener { submit() }

        observeState()
    }

    private fun observeState() {
        viewModel.submitState.asLiveData().observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Success -> {
                    successText.isVisible = true
                    successText.text = state.message ?: "Assessment published!"
                    errorText.isVisible = false
                    clearForm()
                    viewModel.resetState()
                }
                is UiState.Error -> {
                    errorText.isVisible = true
                    errorText.text = state.message
                    successText.isVisible = false
                }
                else -> {}
            }
        }
    }

    private fun submit() {
        val title = titleInput.text?.toString()?.trim() ?: ""
        val dueDate = dueDateInput.text?.toString()?.trim() ?: ""
        val marksStr = totalMarksInput.text?.toString()?.trim() ?: ""

        if (title.isEmpty() || dueDate.isEmpty() || marksStr.isEmpty()) {
            errorText.isVisible = true
            errorText.text = "Please fill all required fields"
            successText.isVisible = false
            return
        }

        val marks = marksStr.toIntOrNull() ?: 0
        val type = when (typeGroup.checkedRadioButtonId) {
            R.id.radioQuiz -> "Quiz"
            R.id.radioExam -> "Exam"
            else -> "Assignment"
        }

        errorText.isVisible = false
        successText.isVisible = false

        viewModel.submitAssessment(
            AnnounceAssessmentRequest(
                type = type,
                title = title,
                description = descriptionInput.text?.toString()?.trim() ?: "",
                dueDate = dueDate,
                totalMarks = marks
            )
        )
    }

    private fun showDatePicker() {
        val cal = Calendar.getInstance()
        DatePickerDialog(
            requireContext(),
            { _, year, month, day ->
                dueDateInput.setText(String.format("%04d-%02d-%02d", year, month + 1, day))
            },
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun clearForm() {
        titleInput.setText("")
        descriptionInput.setText("")
        dueDateInput.setText("")
        totalMarksInput.setText("")
        typeGroup.check(R.id.radioAssignment)
    }
}
