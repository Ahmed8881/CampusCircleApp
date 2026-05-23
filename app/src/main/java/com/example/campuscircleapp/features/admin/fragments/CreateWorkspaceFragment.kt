package com.example.campuscircleapp.features.admin.fragments

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
import com.example.campuscircleapp.core.models.UiState
import com.example.campuscircleapp.features.admin.viewModels.CreateWorkspaceViewModel
import com.example.campuscircleapp.shared.services.SessionManager
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import www.sanju.motiontoast.MotionToast
import www.sanju.motiontoast.MotionToastStyle

class CreateWorkspaceFragment : Fragment() {

    private lateinit var viewModel: CreateWorkspaceViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        inflater.inflate(R.layout.fragment_create_workspace, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this)[CreateWorkspaceViewModel::class.java]

        val nameInput = view.findViewById<TextInputEditText>(R.id.workspaceNameInput)
        val descInput = view.findViewById<TextInputEditText>(R.id.workspaceDescInput)
        val createBtn = view.findViewById<MaterialButton>(R.id.createWorkspaceBtn)
        val spacesRecycler = view.findViewById<RecyclerView>(R.id.workspacesRecyclerView)
        val emptyView = view.findViewById<TextView>(R.id.workspacesEmptyView)

        spacesRecycler.layoutManager = LinearLayoutManager(requireContext())

        val token = SessionManager.getToken(requireContext()) ?: return

        viewModel.createState.asLiveData().observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Loading -> createBtn.isEnabled = false
                is UiState.Success -> {
                    createBtn.isEnabled = true
                    MotionToast.createColorToast(requireActivity(), "Created",
                        state.message ?: "Workspace created!", MotionToastStyle.SUCCESS,
                        MotionToast.GRAVITY_TOP, MotionToast.LONG_DURATION,
                        ResourcesCompat.getFont(requireContext(), www.sanju.motiontoast.R.font.helvetica_regular))
                    nameInput.text?.clear()
                    descInput.text?.clear()
                    viewModel.loadSpaces(token)
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

        viewModel.spacesState.asLiveData().observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Success -> {
                    if (state.data.isEmpty()) {
                        emptyView.visibility = View.VISIBLE
                        spacesRecycler.visibility = View.GONE
                    } else {
                        emptyView.visibility = View.GONE
                        spacesRecycler.visibility = View.VISIBLE
                        spacesRecycler.adapter = com.example.campuscircleapp.adapters.SimpleStringAdapter(
                            state.data.map { it.name }
                        )
                    }
                }
                else -> {}
            }
        }

        createBtn.setOnClickListener {
            val name = nameInput.text.toString().trim()
            val desc = descInput.text.toString().trim()
            viewModel.createWorkspace(token, name, desc)
        }

        viewModel.loadSpaces(token)
    }
}
