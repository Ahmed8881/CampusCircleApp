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
import com.example.campuscircleapp.adapters.InstructorAdapter
import com.example.campuscircleapp.core.models.UiState
import com.example.campuscircleapp.features.admin.models.InstructorResponse
import com.example.campuscircleapp.features.admin.viewModels.CreateInstructorsViewModel
import com.example.campuscircleapp.shared.services.SessionManager
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import www.sanju.motiontoast.MotionToast
import www.sanju.motiontoast.MotionToastStyle

class CreateInstructorsFragment : Fragment() {

    private lateinit var viewModel: CreateInstructorsViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        inflater.inflate(R.layout.fragment_create_instructors, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this)[CreateInstructorsViewModel::class.java]

        val nameInput = view.findViewById<TextInputEditText>(R.id.instructorNameInput)
        val emailInput = view.findViewById<TextInputEditText>(R.id.instructorEmailInput)
        val usernameInput = view.findViewById<TextInputEditText>(R.id.instructorUsernameInput)
        val passwordInput = view.findViewById<TextInputEditText>(R.id.instructorPasswordInput)
        val dobInput = view.findViewById<TextInputEditText>(R.id.instructorDobInput)
        val createBtn = view.findViewById<MaterialButton>(R.id.createInstructorBtn)
        val recycler = view.findViewById<RecyclerView>(R.id.instructorsRecyclerView)
        val emptyView = view.findViewById<TextView>(R.id.instructorsEmptyView)

        recycler.layoutManager = LinearLayoutManager(requireContext())

        dobInput.setOnClickListener {
            DatePickerDialog(requireContext()).apply {
                setOnDateSetListener { _, year, month, day ->
                    val mm = (month + 1).toString().padStart(2, '0')
                    dobInput.setText("${day.toString().padStart(2,'0')}-$mm-$year")
                }
            }.show()
        }

        val token = SessionManager.getToken(requireContext()) ?: return

        viewModel.createState.asLiveData().observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Loading -> createBtn.isEnabled = false
                is UiState.Success -> {
                    createBtn.isEnabled = true
                    MotionToast.createColorToast(requireActivity(), "Created",
                        state.message ?: "Instructor created!", MotionToastStyle.SUCCESS,
                        MotionToast.GRAVITY_TOP, MotionToast.LONG_DURATION,
                        ResourcesCompat.getFont(requireContext(), www.sanju.motiontoast.R.font.helvetica_regular))
                    listOf(nameInput, emailInput, usernameInput, passwordInput, dobInput).forEach { it.text?.clear() }
                    viewModel.loadInstructors(token)
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

        viewModel.instructorsState.asLiveData().observe(viewLifecycleOwner) { state ->
            if (state is UiState.Success) {
                if (state.data.isEmpty()) { emptyView.visibility = View.VISIBLE; recycler.visibility = View.GONE }
                else {
                    emptyView.visibility = View.GONE; recycler.visibility = View.VISIBLE
                    recycler.adapter = InstructorAdapter(
                        state.data,
                        onEdit = { i -> showEditDialog(i, token) },
                        onDelete = { i -> showDeleteDialog(i, token) }
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
                    viewModel.loadInstructors(token)
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
            viewModel.createInstructor(token,
                nameInput.text.toString().trim(), emailInput.text.toString().trim(),
                usernameInput.text.toString().trim(), passwordInput.text.toString().trim(),
                dobInput.text.toString().trim())
        }

        viewModel.loadInstructors(token)
    }

    private fun showEditDialog(i: InstructorResponse, token: String) {
        val inflater = LayoutInflater.from(requireContext())
        val dialogView = inflater.inflate(R.layout.dialog_edit_instructor, null)
        val nameInput = dialogView.findViewById<EditText>(R.id.dialogInstructorName)
        val emailInput = dialogView.findViewById<EditText>(R.id.dialogInstructorEmail)

        nameInput.setText(i.name)
        emailInput.setText(i.email ?: "")

        AlertDialog.Builder(requireContext())
            .setTitle("Edit Instructor")
            .setView(dialogView)
            .setPositiveButton("Save") { _, _ ->
                val newName = nameInput.text.toString().trim()
                val newEmail = emailInput.text.toString().trim()
                if (newName.isNotBlank() && newEmail.isNotBlank()) {
                    viewModel.createInstructor(token, newName, newEmail, i.username ?: "", "", "")
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showDeleteDialog(i: InstructorResponse, token: String) {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete Instructor")
            .setMessage("Are you sure you want to delete \"${i.name}\"? This action cannot be undone.")
            .setPositiveButton("Delete") { _, _ ->
                viewModel.deleteInstructor(token, i.id)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}
